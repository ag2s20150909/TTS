package me.ag2s.tts;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.dnsoverhttps.DnsOverHttps;


public class APP extends Application {
    /**
     * 全局的android.content.Context
     */
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    /**
     * 用于DoH的@see:okhttp3.OkHttpClient
     */
    private static final okhttp3.OkHttpClient bootClient = new OkHttpClient.Builder().build();

    private static okhttp3.OkHttpClient okHttpClient = null;

    public static okhttp3.OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (APP.class) {
                if (okHttpClient == null) {
                    DnsOverHttps dns = new DnsOverHttps.Builder().client(
                            APP.bootClient.newBuilder().cache(getCache("doh", 1024 * 1024 * 100)).build())
                            .url(HttpUrl.get("https://dns.google/dns-query"))//30ms
                            .includeIPv6(true)
                            .build();
                    okHttpClient = bootClient.newBuilder()
                            .cookieJar(new PersistentCookieJar(new SetCookieCache(),
                                    new SharedPrefsCookiePersistor(getContext())))
                            .pingInterval(40, TimeUnit.SECONDS) // 设置 PING 帧发送间隔
                            .dns(dns)
                            .build();
                }
            }
        }
        return okHttpClient;
    }


    public static Context getContext() {
        if (mContext == null) {
            mContext = initAndGetAppCtxWithReflection();
        }
        return mContext;
    }


    public static okhttp3.Cache getCache(String name, int maxSize) {
        File file = new File(getContext().getExternalCacheDir(), name);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                Log.e(APP.class.getSimpleName(), "创建文件夹失败");
            }
        }
        return new okhttp3.Cache(file, maxSize);
    }


    /**
     * 反射获取Context
     */
    @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
    private static Context initAndGetAppCtxWithReflection() {
        // Fallback, should only run once per non default process.
        try {
            return (Context) Class.forName("android.app.ActivityThread")
                    .getDeclaredMethod("currentApplication")
                    .invoke(null);
        } catch (Exception e) {
            return null;
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
    }

    public static void showToast(String msg){
        if(isMainThread()){
            Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
        }else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext = getApplicationContext();
    }
}
