package me.ag2s.tts.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.ag2s.tts.APP;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;


public class HttpTool {

    public static final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public interface DownloadCallBack {
        void onSuccess(String path);

        void onError(String err);
    }

    private static final String TAG = HttpTool.class.getSimpleName();
    public static final String HTTP_ERROR = "error:";
    public static final MediaType JSON = MediaType.get("application/json;charset=UTF-8");


    public static String httpGet(String url) {
        OkHttpClient client = APP.getOkHttpClient();
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
                Log.e(TAG, HTTP_ERROR + response.message() + " errorCode:" + response.code());
                return HTTP_ERROR + response.message() + " errorCode:" + response.code();
            }
        } catch (Exception e) {
            return HTTP_ERROR + CommonTool.getStackTrace(e);
        }
    }

    public static String httpGet(String url, HashMap<String, String> headers) {
        OkHttpClient client = APP.getOkHttpClient();
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
                Log.e(TAG, HTTP_ERROR + response.message() + " errorCode:" + response.code());
                return HTTP_ERROR + response.message() + " errorCode:" + response.code();
            }
        } catch (Exception e) {
            return HTTP_ERROR + CommonTool.getStackTrace(e);
        }
    }


    @SuppressWarnings("unused")
    public static String httpPost(String url, HashMap<String, String> headers, HashMap<String, String> map) {

        OkHttpClient client = APP.getOkHttpClient();
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
        Request request = requestBuilder.build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
            } else {
                return HTTP_ERROR + response.message() + " errors:" + response.code();
            }
        } catch (Exception e) {
            return HTTP_ERROR + CommonTool.getStackTrace(e);
        }

    }

    public static void downLoadFile(String url, String path, DownloadCallBack cb) {
        OkHttpClient client = APP.getOkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                cb.onError(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                try (BufferedSink bufferedSink = Okio.buffer(Okio.sink(new File(path)))) {
                    bufferedSink.write(Objects.requireNonNull(response.body()).byteString());
                    cb.onSuccess(path);
                } catch (IOException e) {
                    e.printStackTrace();
                    e.printStackTrace();
                    cb.onError(e.getLocalizedMessage());
                }

            }

        });
    }

    @SuppressWarnings("unused")
    public static String httpPostJson(String url, HashMap<String, String> headers, String json) {
        OkHttpClient client = APP.getOkHttpClient();
        RequestBody body = RequestBody.create(json, JSON);
        String refer = url.substring(0, url.lastIndexOf("/") + 1);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .header("Referer", refer)
                .header("User-Agent", UA)
                .post(body);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = requestBuilder.build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
            } else {
                return HTTP_ERROR + response.message() + " errors:" + response.code();
            }
        } catch (Exception e) {
            return HTTP_ERROR + CommonTool.getStackTrace(e);
        }

    }


    // --Commented out by Inspection (2022/1/24 22:32):public static final String PcUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36";
    public static final String UA = "Mozilla/5.0 (Linux; Android 12; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.0.0 Mobile Safari/537.36";


}
