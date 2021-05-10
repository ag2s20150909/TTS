package me.ag2s.tts;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import me.ag2s.tts.utils.LoggingInterceptor;
import me.ag2s.tts.utils.OkHttpDns;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;


public class APP extends Application {
    private static final String TAG = "APP";
    /**
     * 全局的android.content.Context
     */
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    /**
     * 用于DoH的@see:okhttp3.OkHttpClient
     */
    private static okhttp3.OkHttpClient bootClient;

    public static Context getContext() {
        return mContext;
    }


    /**
     * 所有OkHttpClient的唯一实例，注意该dns不能使用doh
     *
     * @return OkHttpClient
     */
    public static OkHttpClient getBootClient() {
        if (bootClient == null) {
            bootClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new LoggingInterceptor())
                    .build();
        }
        return bootClient;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext = this;
    }
}
