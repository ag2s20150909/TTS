package me.ag2s.tts;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.dnsoverhttps.DnsOverHttps;

@SuppressWarnings("unused")
public class APP extends Application {
    /**
     * 全局的android.content.Context
     */
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;


    /**
     * 用于DoH的@see:okhttp3.OkHttpClient
     */
    private static final okhttp3.OkHttpClient bootClient = new OkHttpClient.Builder()
            .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
            .fastFallback(true)
            .build();
    private static volatile DnsOverHttps dns = null;

    private static DnsOverHttps getDns() {

        if (dns == null) {
            synchronized (APP.class) {
                if (dns == null) {
                    OkHttpClient temp = APP.bootClient.newBuilder().cache(getCache("doh", 1024 * 1024 * 100)).build();
                    dns = new DnsOverHttps.Builder()
                            .client(temp)
                            .url(HttpUrl.get("https://dns.alidns.com/dns-query"))
                            .bootstrapDnsHosts(
                                    getByName("223.5.5.5"),
                                    getByName("223.6.6.6"),
                                    getByName("2400:3200::1"),
                                    getByName("2400:3200:baba::1")
                            )

                            .includeIPv6(true)
                            .build();
                }
            }
        }
        return dns;
    }
    //private static final DnsOverHttps dns = new DnsOverHttps.Builder().client(


    private static volatile okhttp3.OkHttpClient okHttpClient = null;

    public static okhttp3.OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (APP.class) {
                if (okHttpClient == null) {

                    okHttpClient = new OkHttpClient.Builder()
                            .cookieJar(new PersistentCookieJar(new SetCookieCache(),
                                    new SharedPrefsCookiePersistor(getContext())))
                            .pingInterval(20, TimeUnit.SECONDS) // 设置 PING 帧发送间隔
                            .fastFallback(true)
//                            .dns(s -> {
//
//                                return getDns().lookup(s);
//                            })
                            .build();
                }
            }
        }
        return okHttpClient;
    }


    public static void putString(String key, @Nullable String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.apply();

    }


    public static void putStringSet(String key, @Nullable Set<String> values) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putStringSet(key, values);
        editor.apply();

    }


    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.apply();

    }


    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(key, value);
        editor.apply();
    }


    public static void putFloat(String key, float value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putFloat(key, value);
        editor.apply();

    }


    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();

    }


    @Nullable
    public static String getString(String key, @Nullable String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }


    @Nullable
    public static Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return getSharedPreferences().getStringSet(key, defValues);
    }


    public static int getInt(String key, int defValue) {
        return getSharedPreferences().getInt(key, defValue);
    }


    public static long getLong(String key, long defValue) {
        return getSharedPreferences().getLong(key, defValue);
    }

    public static float getFloat(String key, float defValue) {
        return getSharedPreferences().getFloat(key, defValue);
    }


    public static boolean getBoolean(String key, boolean defValue) {
        return getSharedPreferences().getBoolean(key, defValue);
    }


    private static final class PreferencesHolder {
        public static final SharedPreferences preferences = getContext().getSharedPreferences("TTS", Context.MODE_PRIVATE);
    }

    public static SharedPreferences getSharedPreferences() {
        return PreferencesHolder.preferences;
    }

    public static @Nullable
    InetAddress getByName(@NonNull String ip) {

        try {
            return InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            return null;
        }
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

    private static void tryInstallGmsSsl(Context context) {
        try {
            Context gmsContext = context.createPackageContext("com.google.android.gms",
                    CONTEXT_INCLUDE_CODE | CONTEXT_IGNORE_SECURITY);
            gmsContext.getClassLoader()
                    .loadClass("com.google.android.gms.common.security.ProviderInstallerImpl")
                    .getDeclaredMethod("insertProvider", Context.class)
                    .invoke(null, gmsContext);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(() -> {
            tryInstallGmsSsl(APP.this);
        }).start();
        mContext = this.getApplicationContext();
    }

    public static void showToast(String msg) {
        if (isMainThread()) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        } else {
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());
        }
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //Security.insertProviderAt(Conscrypt.newProvider(), 1);
        mContext = getApplicationContext();
    }
}