package me.ag2s.tts.utils;

import androidx.annotation.NonNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.Dns;

/**
 * Internal Bootstrap DNS implementation for handling initial connection to DNS over HTTPS server.
 * <p>
 * Returns hardcoded results for the known host.
 */
final class BootstrapDns implements Dns {

    private static final Pattern IPV4 = Pattern.compile("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");
    private final String dnsHostname;
    private final List<InetAddress> dnsServers;

    BootstrapDns(String dnsHostname, List<InetAddress> dnsServers) {
        this.dnsHostname = dnsHostname;
        this.dnsServers = dnsServers;
    }

    @NonNull
    @Override
    public List<InetAddress> lookup(@NonNull String hostname) throws UnknownHostException {
        if (IPV4.matcher(hostname).find()) {
            return Arrays.asList(InetAddress.getAllByName(hostname));
        }
        if (!this.dnsHostname.equals(hostname)) {
            throw new UnknownHostException(
                    "BootstrapDns called for " + hostname + " instead of " + dnsHostname);
        }

        return dnsServers;
    }
}