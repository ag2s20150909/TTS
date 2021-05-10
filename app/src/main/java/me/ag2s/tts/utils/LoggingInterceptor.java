package me.ag2s.tts.utils;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.ByteString;

public class LoggingInterceptor implements Interceptor {
    private static final String TAG = LoggingInterceptor.class.getSimpleName();

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        //这个chain里面包含了request和response，所以你要什么都可以从这里拿
        Request request = chain.request();
        String host = "";

        long t1 = System.nanoTime();//请求发起的时间

        Log.d(TAG, "DNS:" + chain.connection());
        //Log.d(TAG, "DNS:" + request.url().queryParameter("dns"));
        if ((request.headers().get("Content-Type") != null && Objects.equals(request.headers().get("Content-Type"), "application/dns-message")) ||
                request.url().queryParameter("dns") != null) {
            try {
                ByteString byteString = null;

                if (request.method().equals("GET")) {
                    //Log.d(TAG, "DNS:" + request.method());
                    String s = request.url().queryParameter("dns");
                    byteString = ByteString.decodeBase64(Objects.requireNonNull(s));
                } else if (request.method().equals("POST")) {
                    final Request copy = request.newBuilder().build();
                    final Buffer buffer = new Buffer();
                    Objects.requireNonNull(copy.body()).writeTo(buffer);
                    byteString = buffer.readByteString();
                } else {
                    Log.d(TAG, "DNS:" + request.method());
                }
                host = DnsRecordCodec.decodeQuery(byteString);
                Log.v(TAG, String.format("DNS请求:[%s],\n%s", host, chain.connection()));

            } catch (Exception e) {
                Log.e(TAG, "decode rqquest", e);
            }
        } else {
            Log.v(TAG, String.format("\r\n发送请求 %s \non %s%n%s",
                    request.url(), chain.connection(), request.headers()));

        }
        Response response = chain.proceed(request);


        long t2 = System.nanoTime();//收到响应的时间

        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        ResponseBody responseBody = response.peekBody(1024 * 1024);
        if (response.headers().get("Content-Type") != null && Objects.equals(response.headers().get("Content-Type"), "application/dns-message")) {
            StringBuilder msg = new StringBuilder();
            List<InetAddress> inetAddresses = new ArrayList<>();
            try {
                inetAddresses.addAll(DnsRecordCodec.decodeAnswers(host, responseBody.byteString()));
            } catch (Exception ignored) {
            }
            for (InetAddress i : inetAddresses) {
                //ComonTool.hookDNS(host, i.getHostAddress());
                msg.append(host).append("->").append(i.getHostAddress()).append("\n");
            }

            Log.v(TAG, String.format("DNS记录：相应时间%.1fms\r\n%s",
                    (t2 - t1) / 1e6d,
                    msg.toString()));
        } else {
            Log.v(TAG, String.format(Locale.CHINA, "\r\n接收响应: [%s] %n返回json:【%s】 %.1fms%n%s",
                    response.request().url(), response.message(),
                    (t2 - t1) / 1e6d,
                    response.headers()));
        }


        return response;
    }
}
