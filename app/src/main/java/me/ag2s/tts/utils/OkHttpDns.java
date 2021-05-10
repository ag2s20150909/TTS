package me.ag2s.tts.utils;


import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dns;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.publicsuffix.PublicSuffixDatabase;
import okio.ByteString;

public class OkHttpDns implements Dns {

    public static final MediaType DNS_MESSAGE = MediaType.get("application/dns-message");
    public static final int MAX_RESPONSE_SIZE = 64 * 1024;
    private final OkHttpClient client;
    private final HttpUrl url;
    private final HttpUrl wurl;
    private final boolean includeIPv6;
    private final boolean post;
    private final boolean resolvePrivateAddresses;
    private final boolean resolvePublicAddresses;
    private final ArrayList<String> whiteList;

    OkHttpDns(Builder builder) {
        if (builder.client == null) {
            throw new NullPointerException("client not set");
        }
        if (builder.url == null) {
            throw new NullPointerException("url not set");
        }

        this.url = builder.url;
        this.wurl = builder.wurl;
        this.includeIPv6 = builder.includeIPv6;
        this.post = builder.post;
        this.resolvePrivateAddresses = builder.resolvePrivateAddresses;
        this.resolvePublicAddresses = builder.resolvePublicAddresses;
        this.client = builder.client.newBuilder().dns(buildBootstrapClient(builder)).build();
        this.whiteList = builder.whitelist;
    }

    private static Dns buildBootstrapClient(Builder builder) {
        List<InetAddress> hosts = builder.bootstrapDnsHosts;

        if (hosts != null) {
            return new BootstrapDns(Objects.requireNonNull(builder.url).host(), hosts);
        } else {
            return builder.systemDns;
        }
    }

    private HttpUrl getUrl(String hostname) {
        for (String s : whiteList) {
            if (hostname.contains(s)) {
                return wurl;
            }
        }
        return url;
    }

    public HttpUrl url() {
        return url;
    }

    public boolean post() {
        return post;
    }

    public boolean includeIPv6() {
        return includeIPv6;
    }

    public OkHttpClient client() {
        return client;
    }

    public boolean resolvePrivateAddresses() {
        return resolvePrivateAddresses;
    }

    public boolean resolvePublicAddresses() {
        return resolvePublicAddresses;
    }


    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        if (!resolvePrivateAddresses || !resolvePublicAddresses) {
            boolean privateHost = isPrivateHost(hostname);

            if (privateHost && !resolvePrivateAddresses) {
                throw new UnknownHostException("private hosts not resolved");
            }

            if (!privateHost && !resolvePublicAddresses) {
                throw new UnknownHostException("public hosts not resolved");
            }
        }

        return lookupHttps(hostname);
    }

    private List<InetAddress> lookupHttps(String hostname) throws UnknownHostException {
        List<Call> networkRequests = new ArrayList<>(2);
        List<Exception> failures = new ArrayList<>(2);
        List<InetAddress> results = new ArrayList<>(5);

        buildRequest(hostname, networkRequests, results, failures, DnsRecordCodec.TYPE_A);

        if (includeIPv6) {
            buildRequest(hostname, networkRequests, results, failures, DnsRecordCodec.TYPE_AAAA);
        }

        executeRequests(hostname, networkRequests, results, failures);

        if (!results.isEmpty()) {
            return results;
        }

        return throwBestFailure(hostname, failures);
    }

    private void buildRequest(String hostname, List<Call> networkRequests, List<InetAddress> results,
                              List<Exception> failures, int type) {
        Request request = buildRequest(hostname, type);
        Response response = getCacheOnlyResponse(request);

        if (response != null) {
            processResponse(response, hostname, results, failures);
        } else {
            networkRequests.add(client.newCall(request));
        }
    }

    private void executeRequests(final String hostname, List<Call> networkRequests,
                                 final List<InetAddress> responses, final List<Exception> failures) {
        final CountDownLatch latch = new CountDownLatch(networkRequests.size());

        for (Call call : networkRequests) {
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    synchronized (failures) {
                        failures.add(e);
                    }
                    latch.countDown();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    processResponse(response, hostname, responses, failures);
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            failures.add(e);
        }
    }

    private void processResponse(Response response, String hostname, List<InetAddress> results,
                                 List<Exception> failures) {
        try {
            List<InetAddress> addresses = readResponse(hostname, response);
            synchronized (results) {
                results.addAll(addresses);
            }
        } catch (Exception e) {
            synchronized (failures) {
                failures.add(e);
            }
        }
    }

    private List<InetAddress> throwBestFailure(String hostname, List<Exception> failures)
            throws UnknownHostException {
        if (failures.size() == 0) {
            throw new UnknownHostException(hostname);
        }

        Exception failure = failures.get(0);

        if (failure instanceof UnknownHostException) {
            throw (UnknownHostException) failure;
        }

        UnknownHostException unknownHostException = new UnknownHostException(hostname);
        unknownHostException.initCause(failure);

        for (int i = 1; i < failures.size(); i++) {
            //Util.addIfAbsent(failures,unknownHostException);
            //addSuppressedIfPossible(unknownHostException, failures.get(i));
        }

        throw unknownHostException;
    }

    private Response getCacheOnlyResponse(Request request) {
        if (!post && client.cache() != null) {
            try {
                Request cacheRequest = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();

                Response cacheResponse = client.newCall(cacheRequest).execute();

                if (cacheResponse.code() != 504) {
                    return cacheResponse;
                }
            } catch (IOException ioe) {
                // Failures are ignored as we can fallback to the network
                // and hopefully repopulate the cache.
            }
        }

        return null;
    }

    private List<InetAddress> readResponse(String hostname, Response response) throws Exception {
        if (response.cacheResponse() == null && response.protocol() != Protocol.HTTP_2) {
            Log.e(OkHttpDns.class.getSimpleName(), "警告:错误的协议: " + response.protocol(), null);
        }

        try {
            if (!response.isSuccessful()) {
                throw new IOException("response: " + response.code() + " " + response.message());
            }

            ResponseBody body = response.body();

            if (Objects.requireNonNull(body).contentLength() > MAX_RESPONSE_SIZE) {
                throw new IOException("response size exceeds limit ("
                        + MAX_RESPONSE_SIZE
                        + " bytes): "
                        + body.contentLength()
                        + " bytes");
            }

            ByteString responseBytes = body.source().readByteString();

            return DnsRecordCodec.decodeAnswers(hostname, responseBytes);
        } finally {
            response.close();
        }
    }

    private Request buildRequest(String hostname, int type) {
        Request.Builder requestBuilder = new Request.Builder().header("Accept", DNS_MESSAGE.toString());

        ByteString query = DnsRecordCodec.encodeQuery(hostname, type);

        if (post) {
            requestBuilder = requestBuilder.url(getUrl(hostname)).post(RequestBody.Companion.create(query, DNS_MESSAGE));
        } else {
            String encoded = query.base64Url().replace("=", "");
            HttpUrl requestUrl = getUrl(hostname).newBuilder().addQueryParameter("dns", encoded).build();

            requestBuilder = requestBuilder.url(requestUrl);
        }

        return requestBuilder.build();
    }

    static boolean isPrivateHost(String host) {
        return PublicSuffixDatabase.Companion.get().getEffectiveTldPlusOne(host) == null;
    }

    public static final class Builder {

        OkHttpClient client = null;

        HttpUrl url = null;

        HttpUrl wurl = HttpUrl.get("https://1.1.1.1/dns-query");
        boolean includeIPv6 = true;
        boolean post = false;
        Dns systemDns = Dns.SYSTEM;
        List<InetAddress> bootstrapDnsHosts = null;
        boolean resolvePrivateAddresses = false;
        boolean resolvePublicAddresses = true;
        ArrayList<String> whitelist = new ArrayList<>();

        public Builder() {
        }

        public OkHttpDns build() {
            return new OkHttpDns(this);
        }

        public Builder whitelist(Collection<String> whitelist) {
            this.whitelist.addAll(whitelist);
            return this;
        }

        public Builder client(OkHttpClient client) {
            this.client = client;
            return this;
        }

        public Builder wurl(HttpUrl wurl) {
            this.wurl = wurl;
            return this;
        }

        public Builder url(HttpUrl url) {
            this.url = url;
            return this;
        }

        public Builder includeIPv6(boolean includeIPv6) {
            this.includeIPv6 = includeIPv6;
            return this;
        }

        public Builder post(boolean post) {
            this.post = post;
            return this;
        }

        public Builder resolvePrivateAddresses(boolean resolvePrivateAddresses) {
            this.resolvePrivateAddresses = resolvePrivateAddresses;
            return this;
        }

        public Builder resolvePublicAddresses(boolean resolvePublicAddresses) {
            this.resolvePublicAddresses = resolvePublicAddresses;
            return this;
        }

        public Builder bootstrapDnsHosts(List<InetAddress> bootstrapDnsHosts) {
            this.bootstrapDnsHosts = bootstrapDnsHosts;
            return this;
        }

        public Builder bootstrapDnsHosts(InetAddress... bootstrapDnsHosts) {
            return bootstrapDnsHosts(Arrays.asList(bootstrapDnsHosts));
        }

        public Builder systemDns(Dns systemDns) {
            this.systemDns = systemDns;
            return this;
        }
    }

}
