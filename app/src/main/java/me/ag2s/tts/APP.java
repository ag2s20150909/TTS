package me.ag2s.tts;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import okhttp3.OkHttpClient;


public class APP extends Application {
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

                    .build();
        }
        return bootClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext=this;
    }
}
