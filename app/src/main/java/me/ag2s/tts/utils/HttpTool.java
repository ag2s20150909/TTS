package me.ag2s.tts.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.webkit.WebSettings;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import me.ag2s.tts.APP;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HttpTool {
    private static final String TAG = HttpTool.class.getSimpleName();
    public static final String HTTPERROR = "error:";


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
                return new String(response.body().bytes(), StandardCharsets.UTF_8);
            } else {
                Log.e(TAG, HTTPERROR + response.message() + " errorCode:" + response.code());
                return HTTPERROR + response.message() + " errorCode:" + response.code();
            }
        } catch (Exception e) {
            return HTTPERROR + CommonTool.getStackTrace(e);
        }
    }

    public static String httpGet(String url, HashMap<String, String> header) {
        OkHttpClient client = APP.getBootClient();
        Request.Builder requestbuilder = new Request.Builder().get().url(url);
        //String refer=url.substring(0,url.lastIndexOf("/")+1);
        //requestbuilder.header("Referer", refer);
        requestbuilder.header("User-Agent", PcUserAgent);

        for (Map.Entry<String, String> entry : header.entrySet()) {
            requestbuilder.addHeader(entry.getKey(), entry.getValue());
        }

        Request request = requestbuilder.build();
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

    public static String httpGetPC(String url) {
        OkHttpClient client = APP.getBootClient();
        Request.Builder requestBuilder = new Request.Builder().get().url(url);
        String refer = url.substring(0, url.lastIndexOf("/") + 1);
        requestBuilder.header("Referer", refer);
        requestBuilder.header("User-Agent", PcUserAgent);
        Request request = requestBuilder.build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
            } else return HTTPERROR + response.message() + " errorCode:" + response.code();
        } catch (Exception e) {
            return HTTPERROR + CommonTool.getStackTrace(e);
        }
    }

    @SuppressWarnings("unused")
    public static Bitmap getBitmap(String url) {
        final OkHttpClient client = APP.getBootClient();
        Request.Builder requestBuilder = new Request.Builder().get().url(url);
        requestBuilder.header("Referer", url);


        requestBuilder.header("User-Agent", UA);

        final Request request = requestBuilder.build();


        try {

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // return new String(response.body().bytes(), "utf-8");
                byte[] bs = Objects.requireNonNull(response.body()).bytes();
                return BitmapFactory.decodeByteArray(bs, 0, bs.length);
                //iv.setImageBitmap(b);
            } else {

                return getErrorBitmap(response.message() + "" + response.code());

                // return "error:" + response.message() + " errorcode:" + response.code();
            }
        } catch (Exception e) {
            return getErrorBitmap(e.toString());
            //return "error:" + e.getMessage();
        }


    }


    public static Bitmap getErrorBitmap(String t) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        Bitmap b = Bitmap.createBitmap(300, 400, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.drawText(t, 9.f, 9.f, paint);
        return b;
    }

    public static String httpPost(String url, HashMap<String, String> headers, HashMap<String, String> map) {
        OkHttpClient.Builder buider = APP.getBootClient().newBuilder();
        OkHttpClient client = buider.build();
        //构建Body
        FormBody.Builder params = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.add(entry.getKey(), entry.getValue());
        }

        //构建headers
        String refer = url.substring(0, url.lastIndexOf("/") + 1);
        Request.Builder rbuilder = new Request.Builder()

                .header("Referer", refer)
                .header("User-Agent", PcUserAgent)
                .url(url)
                .post(params.build());
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            rbuilder.addHeader(entry.getKey(), entry.getValue());
        }

        Request request = rbuilder.build();

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
    public static String httpPost(String url, HashMap<String, String> map) {
        OkHttpClient.Builder httpBuilder = APP.getBootClient().newBuilder();
        OkHttpClient client = httpBuilder.build();
        FormBody.Builder params = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.add(entry.getKey(), entry.getValue());
        }
        String refer = url.substring(0, url.lastIndexOf("/") + 1);

        Request request = new Request.Builder()
                .url(url)
                .header("Referer", refer)
                .header("User-Agent", UA)
                .post(params.build())
                .build();

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
