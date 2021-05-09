package me.ag2s.tts.utils;

import android.media.MediaPlayer;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import me.ag2s.tts.APP;
import me.ag2s.tts.services.TtsConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.Buffer;
import okio.ByteString;

public class TTSTest extends WebSocketListener {
    private static final String TAG = TTSTest.class.getSimpleName();
    private Buffer mData;
    private boolean run = true;

    private volatile static TTSTest mInstance;

    private OkHttpClient client;
    private WebSocket webSocket;
    private Request request;

    public TTSTest() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        } catch (Exception e) {
            e.printStackTrace();
        }
        client = APP.getBootClient().newBuilder()
                .sslSocketFactory(Objects.requireNonNull(sslContext).getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .pingInterval(20, TimeUnit.SECONDS)
                .build();
        this.webSocket = createWS();


    }

    public WebSocket createWS() {

        String url = "https://speech.platform.bing.com/consumer/speech/synthesize/readaloud/edge/v1?TrustedClientToken=6A5AA1D4EAFF4E9FB37E23D68491D6F4";

        this.request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36 Edg/90.0.818.56")
                .addHeader("Origin", "chrome-extension://jdiccldimpdaibmpdkjnbmckianbfold")
                .addHeader("Sec-WebSocket-Key", "vZ8qxy8q/+2qpzpnhFmgQA==")
                .addHeader("Connection", "keep-alive")
                .build();
        this.webSocket = client.newWebSocket(request, this);
        //webSocket.request();
        return webSocket;
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        String endTag = "turn.end";
        String startTag = "turn.start";
        int endIndex = text.lastIndexOf(endTag);
        int startIndex = text.lastIndexOf(startTag);
        if (startIndex != -1) {
            mData = new Buffer();
        }
        if (endIndex != -1) {
            startPlay(mData.readByteString());
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        String audioTag = "Path:audio\r\n";
        String endTag = "turn.end";

        int audioIndex = bytes.lastIndexOf(audioTag.getBytes(StandardCharsets.UTF_8));
        int endIndex = bytes.lastIndexOf(endTag.getBytes(StandardCharsets.UTF_8));
        if (endIndex != -1) {
            startPlay(mData.readByteString());
        } else if (audioIndex != -1) {
            try {
                //setData(bytes.substring(audioIndex+audioTag.length()));
                Log.d(TAG, bytes.substring(0, audioIndex).utf8());
                //bytes.substring(audioIndex+audioTag.length());
                if (mData == null) {
                    mData = new Buffer();
                }
                mData.write(bytes.substring(audioIndex + audioTag.length()));
            } catch (Exception e) {
                Log.d(TAG, "onMessage 111", e);
                e.printStackTrace();
            }
            //System.out.println(bytes.utf8());
        }

    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
        Log.d(TAG, response.headers().toString());
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        this.webSocket = null;
        Log.d(TAG, "onFailure", t);
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosing(webSocket, code, reason);
        Log.d(TAG, "onClosing" + reason);
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);
        Log.d(TAG, "onClosed" + reason);
    }

    private void sendConfig(TtsConfig ttsConfig) {
        String msg = "X-Timestamp:+" + getTime() + "\r\n" +
                "Content-Type:application/json; charset=utf-8\r\n" +
                "Path:speech.config\r\n\r\n"
                + ttsConfig.toString();
        webSocket.send(msg);
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (中国标准时间)", Locale.ENGLISH);
        Date date = new Date();
        return sdf.format(date);
    }

    public void sendText(String text) {
        if (webSocket == null) {
            webSocket = createWS();
        }

        sendConfig(new TtsConfig.Builder().sentenceBoundaryEnabled(true).build());

        String RequestId = "868727dfbb97961edd36361dd7e4044c";
        String name = "zh-CN-XiaoxiaoNeural";
        String time = getTime();
        Log.d(TAG, "SSS:" + time);
        String txt = "X-RequestId:" + RequestId + "\r\n" +
                "Content-Type:application/ssml+xml\r\n" +
                "X-Timestamp:" + time + "Z\r\n" +
                "Path:ssml\r\n\r\n" +
                "<speak version='1.0' xmlns='http://www.w3.org/2001/10/synthesis' xml:lang='en-US'><voice  name='" + name + "'><prosody pitch='+0Hz' rate ='+0%' volume='+0%'>" + text + "</prosody></voice></speak>";
        webSocket.send(txt);
    }

    public void stop() {

    }


    /**
     * 获取单例引用
     *
     * @return
     */
    public static TTSTest getInstance() {
        if (mInstance == null) {
            synchronized (TTSTest.class) {
                if (mInstance == null) {
                    mInstance = new TTSTest();
                }
            }
        }
        return mInstance;
    }


    /**
     * 启动播放
     *
     * @param data
     */
    public void startPlay(ByteString data) {
        try {
            MediaPlayer mp = null;
            File tempMp3 = new File(APP.getContext().getExternalFilesDir("temp"), System.currentTimeMillis() + "temp.mp3");
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(data.toByteArray());
            fos.flush();
            fos.close();
            MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    // free up media player
                    mp.release();
                    tempMp3.deleteOnExit();
                    Log.i(TAG, "MediaPlayer Released");
                }
            };


            mp = new MediaPlayer();
            mp.setOnCompletionListener(listener);
            // create listener to tidy up after playback complete


            FileInputStream fis = new FileInputStream(tempMp3);
            mp.setDataSource(fis.getFD());
            mp.prepare();
            mp.start();

        } catch (Exception e) {
            Log.d(TAG, "playerr", e);
        }


    }


}
