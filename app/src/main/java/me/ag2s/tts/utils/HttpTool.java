package me.ag2s.tts.utils;

import android.util.Log;
import android.webkit.WebSettings;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import me.ag2s.tts.APP;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpTool {
    private static final String TAG = HttpTool.class.getSimpleName();
    public static final String HTTPERROR = "error:";
    public static final MediaType JSON = MediaType.get("application/json;charset=UTF-8");


    public static String httpGet(String url) {
        OkHttpClient client = APP.getBootClient();
        Request.Builder requestBuilder = new Request.Builder().get().url(url);
        String refer = url.substring(0, url.lastIndexOf("/") + 1);
        requestBuilder.header("Referer", refer);
        requestBuilder.header("User-Agent", UA);


        Request request = requestBuilder.build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
            } else {
                Log.e(TAG, HTTPERROR + response.message() + " errorCode:" + response.code());
                return HTTPERROR + response.message() + " errorCode:" + response.code();
            }
        } catch (Exception e) {
            return HTTPERROR + CommonTool.getStackTrace(e);
        }
    }



    @SuppressWarnings("unused")
    public static String httpPost(String url,HashMap<String, String> headers, HashMap<String, String> map) {
        OkHttpClient.Builder httpBuilder = APP.getBootClient().newBuilder();
        OkHttpClient client = httpBuilder.build();
        FormBody.Builder params = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.add(entry.getKey(), entry.getValue());
        }
        String refer = url.substring(0, url.lastIndexOf("/") + 1);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .header("Referer", refer)
                .header("User-Agent", UA)
                .post(params.build());

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.header(entry.getKey(), entry.getValue());
        }
        Request request=requestBuilder.build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
            } else {
                return HTTPERROR + response.message() + " errorcode:" + response.code();
            }
        } catch (Exception e) {
            return HTTPERROR + CommonTool.getStackTrace(e);
        }

    }

    @SuppressWarnings("unused")
    public static String httpPostJson(String url, HashMap<String, String> headers,String json) {
        OkHttpClient.Builder httpBuilder = APP.getBootClient().newBuilder();
        OkHttpClient client = httpBuilder.build();

        RequestBody body=RequestBody.create(json, JSON);
        String refer = url.substring(0, url.lastIndexOf("/") + 1);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .header("Referer", refer)
                .header("User-Agent", UA)
                .post(body);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request=requestBuilder.build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
            } else {
                return HTTPERROR + response.message() + " errorcode:" + response.code();
            }
        } catch (Exception e) {
            return HTTPERROR + CommonTool.getStackTrace(e);
        }

    }


    public static final String PcUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit" + WebSettings.getDefaultUserAgent(APP.getContext()).replaceAll("Mozilla/5.0.*?AppleWebKit", "").replace("Mobile Safari", "Safari");
    public static final String UA = "Mozilla/5.0 (Linux; Android 11; SM-A7160 Build/RP1A.200720.012; wv) AppleWebKit" + WebSettings.getDefaultUserAgent(APP.getContext()).replaceAll("Mozilla/5.0.*?AppleWebKit", "");


}
