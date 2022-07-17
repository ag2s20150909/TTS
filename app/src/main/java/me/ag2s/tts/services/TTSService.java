package me.ag2s.tts.services;

import static me.ag2s.tts.APP.getOkHttpClient;
import static me.ag2s.tts.utils.CommonTool.getTime;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.speech.tts.Voice;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import me.ag2s.tts.APP;
import me.ag2s.tts.BuildConfig;
import me.ag2s.tts.R;
import me.ag2s.tts.utils.ByteArrayMediaDataSource;
import me.ag2s.tts.utils.CommonTool;
import me.ag2s.tts.utils.GcManger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.Buffer;
import okio.ByteString;

public class TTSService extends TextToSpeechService {

    private PowerManager.WakeLock mWakeLock;

    private volatile WebSocketState webSocketState = WebSocketState.OFFLINE;


    private static final String TAG = TTSService.class.getSimpleName();


    @NonNull
    private final OkHttpClient client;
    @Nullable
    private volatile WebSocket webSocket;
    private volatile boolean isPreview = false;
    private volatile boolean isSynthesizing = false;
    //当前的生成格式
    private volatile TtsOutputFormat currentFormat;
    //当前的数据
    @NonNull
    private final Buffer mData = new Buffer();
    @Nullable
    private volatile String currentMime;

    private MediaCodec mediaCodec;

    @Nullable
    private volatile String[] mCurrentLanguage = null;


    private int oldFormatIndex = 0;
    @Nullable
    private SynthesisCallback callback;
    @NonNull
    private final WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
            Log.e(TAG, "onClosed:" + reason);

        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
            Log.e(TAG, "onClosing:" + reason);

            TTSService.this.webSocket = null;
            webSocketState = WebSocketState.OFFLINE;

            Log.e("SS", "SS:" + isSynthesizing);
            if (isSynthesizing) {
                TTSService.this.webSocket = getOrCreateWs();
            }
            updateNotification("TTS服务-错误中", reason);


        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            TTSService.this.webSocket = null;
            webSocketState = WebSocketState.OFFLINE;
            Log.e(TAG, "onFailure" + t.getMessage(), t);
            if (isSynthesizing) {
                TTSService.this.webSocket = getOrCreateWs();
            }
            updateNotification("TTS服务-错误中", t.getMessage());

            APP.showToast("发生错误:" + t.getMessage());


        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
            //Log.v(TAG, "onMessage" + text);
            final String endTag = "turn.end";
            final String startTag = "turn.start";
            int endIndex = text.lastIndexOf(endTag);
            int startIndex = text.lastIndexOf(startTag);
            //生成开始
            if (startIndex != -1) {
                isSynthesizing = true;
                mData.clear();
            } else if (endIndex != -1) {
                if (callback != null && !callback.hasFinished() && isSynthesizing) {
                    if (!currentFormat.needDecode) {
                        doUnDecode(callback, currentFormat, mData.readByteString());
                    } else {
                        doDecode(callback, currentFormat, mData.readByteString());

                    }

                }


            }
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
            //音频数据流标志头
            final String audioTag = "Path:audio\r\n";
            final String startTag = "Content-Type:";
            final String endTag = "\r\nX-StreamId";
            //Log.e(TAG,bytes.utf8());

            int audioIndex = bytes.lastIndexOf(audioTag.getBytes(StandardCharsets.UTF_8)) + audioTag.length();
            int startIndex = bytes.lastIndexOf(startTag.getBytes(StandardCharsets.UTF_8)) + startTag.length();
            int endIndex = bytes.lastIndexOf(endTag.getBytes(StandardCharsets.UTF_8));
            if (audioIndex != -1 && callback != null) {

                try {
                    String temp = bytes.substring(startIndex, endIndex).utf8();
                    Log.v(TAG, "当前Mime:" + temp);
                    if (temp.startsWith("audio")) {
                        currentMime = temp;
                    } else {
                        return;
                    }
                    if (!currentFormat.needDecode) {
                        if ("audio/x-wav".equals(currentMime) && bytes.lastIndexOf("RIFF".getBytes(StandardCharsets.UTF_8)) != -1) {
                            //去除WAV文件的文件头，解决播放开头时的杂音
                            audioIndex += 44;
                            Log.v(TAG, "移除WAV文件头");
                        }


                    }
                    mData.write(bytes.substring(audioIndex));

                } catch (Exception e) {
                    Log.e(TAG, "onMessage Error:", e);

                    //如果出错返回错误
                    if (callback != null) {
                        callback.error();
                    }
                    isSynthesizing = false;
                }

            }
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            Log.e(TAG, "onOpen" + response.headers());
        }
    };

    public TTSService() {
        client = getOkHttpClient();
    }

    private static final String ACTION_STOP_SERVICE = "action_stop_service";


    NotificationManager notificationManager;
    Notification.Builder notificationBuilder;

    final String notificationChannelId = TTSService.class.getName();
    final String notificationName = "文字转语音服务通知";
    private static final int NOTIFICATION_ID = 1;


    /**
     * 开启前台服务
     */
    private void startForegroundService() {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //创建NotificationChannel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, notificationName, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);

        }

        startForeground(NOTIFICATION_ID, getNotification());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP_SERVICE.equals(intent.getAction())) {
            stopForeground(true);
            stopSelf();
            return START_STICKY_COMPATIBILITY;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private Notification getNotification() {
        Intent stopSelf = new Intent(this, TTSService.class);
        stopSelf.setAction(ACTION_STOP_SERVICE);
        PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSelf, Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_audio)
                .setOnlyAlertOnce(true)
                .setVibrate(null)
                .setSound(null)
                .setLights(0, 0, 0)
                .setContentTitle("TTS服务")
                .setContentText("TTS服务正在运行...");

        //停止前台服务
        Notification.Action action;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            action = new Notification.Action.Builder(Icon.createWithResource(this, R.drawable.ic_add), "stop", pStopSelf).build();
        } else {
            action = new Notification.Action.Builder(R.mipmap.ic_launcher, "stop", pStopSelf).build();
        }
        notificationBuilder.addAction(action);


        //设置Notification的ChannelID,否则不能正常显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notificationBuilder.setChannelId(notificationChannelId);

        }
        return notificationBuilder.build();

    }

    public void updateNotification(@NotNull String title, @Nullable String content) {

        if (content == null || content.isEmpty()) {
            return;
        }
        StringBuilder sb = new StringBuilder(content);
        CommonTool.Trim(sb);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(sb.toString());
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    @Override
    public void onCreate() {
        super.onCreate();
        TokenHolder.startToken();
        startForegroundService();
        reNewWakeLock();


    }


    /**
     * 释放WakeLock
     */
    @SuppressWarnings("unused")
    private void releaseWakeLock() {
        if (null != mWakeLock) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
        stopForeground(true);

    }

    /**
     * 创建或刷新20分钟的WakeLock
     */
    private void reNewWakeLock() {

        if (null == mWakeLock) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
                    "TTS:ttsTag");
        }

        if (null != mWakeLock && !mWakeLock.isHeld()) {
            mWakeLock.acquire(60 * 20 * 1000);
            GcManger.getInstance().doGC();
            Log.e(TAG, "刷新WakeLock20分钟");
        }
    }


    private String oldMime;

    /**
     * 根据mime创建MediaCodec
     * 当Mime未变化时复用MediaCodec
     *
     * @param mime mime
     * @return MediaCodec
     */
    private MediaCodec getMediaCodec(String mime, MediaFormat mediaFormat) {
        if (mediaCodec == null || !mime.equals(oldMime)) {
            if (null != mediaCodec) {
                mediaCodec.release();
                GcManger.getInstance().doGC();
            }
            try {
                mediaCodec = MediaCodec.createDecoderByType(mime);

                oldMime = mime;
            } catch (IOException ioException) {
                //设备无法创建，直接抛出
                ioException.printStackTrace();
                throw new RuntimeException(ioException);
            }
        }
        mediaCodec.reset();
        mediaCodec.configure(mediaFormat, null, null, 0);
        return mediaCodec;
    }


    private synchronized void doDecode(SynthesisCallback cb, @SuppressWarnings("unused") TtsOutputFormat format, ByteString data) {
        isSynthesizing = true;
        try {
            MediaExtractor mediaExtractor = new MediaExtractor();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //在高版本上使用自定义MediaDataSource
                mediaExtractor.setDataSource(new ByteArrayMediaDataSource(data.toByteArray()));
            } else {
                //在低版本上使用Base64音频数据
                mediaExtractor.setDataSource("data:" + currentMime + ";base64," + data.base64());
            }


            //找到音频流的索引
            int audioTrackIndex = -1;
            String mime = null;
            MediaFormat trackFormat = null;
            for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
                trackFormat = mediaExtractor.getTrackFormat(i);
                mime = trackFormat.getString(MediaFormat.KEY_MIME);

                if (!TextUtils.isEmpty(mime) && mime.startsWith("audio")) {
                    audioTrackIndex = i;
                    Log.d(TAG, "找到音频流的索引为：" + audioTrackIndex);
                    Log.d(TAG, "找到音频流的mime为：" + mime);
                    break;
                }
            }
            //没有找到音频流的情况下
            if (audioTrackIndex == -1) {
                Log.e(TAG, "initAudioDecoder: 没有找到音频流");
                updateNotification("TTS服务-错误中", "没有找到音频流");
                cb.done();
                isSynthesizing = false;
                return;
            }

            //Log.e("Track", trackFormat.toString());


            //opus的音频必须设置这个才能正确的解码
            if ("audio/opus".equals(mime)) {
                //Log.d(TAG, ByteString.of(trackFormat.getByteBuffer("csd-0")).hex());


                Buffer buf = new Buffer();
                // Magic Signature：固定头，占8个字节，为字符串OpusHead
                buf.write("OpusHead".getBytes(StandardCharsets.UTF_8));
                // Version：版本号，占1字节，固定为0x01
                buf.writeByte(1);
                // Channel Count：通道数，占1字节，根据音频流通道自行设置，如0x02
                buf.writeByte(1);
                // Pre-skip：回放的时候从解码器中丢弃的samples数量，占2字节，为小端模式，默认设置0x00,
                buf.writeShortLe(0);
                // Input Sample Rate (Hz)：音频流的Sample Rate，占4字节，为小端模式，根据实际情况自行设置
                buf.writeIntLe(currentFormat.HZ);
                //Output Gain：输出增益，占2字节，为小端模式，没有用到默认设置0x00, 0x00就好
                buf.writeShortLe(0);
                // Channel Mapping Family：通道映射系列，占1字节，默认设置0x00就好
                buf.writeByte(0);
                //Channel Mapping Table：可选参数，上面的Family默认设置0x00的时候可忽略


                if (BuildConfig.DEBUG) {
                    Log.e(TAG, trackFormat.getByteBuffer("csd-1").order(ByteOrder.nativeOrder()).getLong() + "");
                    Log.e(TAG, trackFormat.getByteBuffer("csd-2").order(ByteOrder.nativeOrder()).getLong() + "");
                    Log.e(TAG, ByteString.of(trackFormat.getByteBuffer("csd-2").array()).hex());
                }

                byte[] csd1bytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
                byte[] csd2bytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
                ByteString hd = buf.readByteString();
                ByteBuffer csd0 = ByteBuffer.wrap(hd.toByteArray());
                trackFormat.setByteBuffer("csd-0", csd0);
                ByteBuffer csd1 = ByteBuffer.wrap(csd1bytes);
                trackFormat.setByteBuffer("csd-1", csd1);
                ByteBuffer csd2 = ByteBuffer.wrap(csd2bytes);
                trackFormat.setByteBuffer("csd-2", csd2);

            }

            //选择此音轨
            mediaExtractor.selectTrack(audioTrackIndex);

            //创建解码器
            MediaCodec mediaCodec = getMediaCodec(mime, trackFormat);//MediaCodec.createDecoderByType(mime);


            mediaCodec.start();

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            ByteBuffer inputBuffer;
            long TIME_OUT_US = 10000;
            while (isSynthesizing) {
                //获取可用的inputBuffer，输入参数-1代表一直等到，0代表不等待，10*1000代表10秒超时
                //超时时间10秒


                int inputIndex = mediaCodec.dequeueInputBuffer(TIME_OUT_US);
                if (inputIndex < 0) {
                    break;
                }
                bufferInfo.presentationTimeUs = mediaExtractor.getSampleTime();
                //bufferInfo.flags=mediaExtractor.getSampleFlags();


                inputBuffer = mediaCodec.getInputBuffer(inputIndex);
                if (inputBuffer != null) {
                    inputBuffer.clear();
                } else {
                    continue;
                }
                //从流中读取的采用数据的大小
                int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);

                if (sampleSize > 0) {
                    bufferInfo.size = sampleSize;
                    //入队解码
                    mediaCodec.queueInputBuffer(inputIndex, 0, sampleSize, 0, 0);
                    //移动到下一个采样点
                    mediaExtractor.advance();
                } else {
                    break;
                }

                //取解码后的数据
                int outputIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIME_OUT_US);
                //不一定能一次取完，所以要循环取
                ByteBuffer outputBuffer;
                byte[] pcmData;
                while (outputIndex >= 0) {
                    outputBuffer = mediaCodec.getOutputBuffer(outputIndex);
                    pcmData = new byte[bufferInfo.size];
                    if (outputBuffer != null) {
                        outputBuffer.get(pcmData);
                        outputBuffer.clear();//用完后清空，复用
                    }
                    cb.audioAvailable(pcmData, 0, bufferInfo.size);
                    //释放
                    mediaCodec.releaseOutputBuffer(outputIndex, false);
                    //再次获取数据
                    outputIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIME_OUT_US);
                }
            }
            mediaCodec.reset();

            cb.done();
            isSynthesizing = false;

        } catch (Exception e) {
            Log.e(TAG, "doDecode", e);
            cb.error();
            isSynthesizing = false;
            //GcManger.getInstance().doGC();
        }
    }


    private synchronized void doUnDecode(SynthesisCallback cb, @SuppressWarnings("unused") TtsOutputFormat format, ByteString data) {
        isSynthesizing = true;
        int length = data.toByteArray().length;
        //最大BufferSize
        final int maxBufferSize = cb.getMaxBufferSize();
        int offset = 0;
        while (offset < length && isSynthesizing) {
            int bytesToWrite = Math.min(maxBufferSize, length - offset);
            cb.audioAvailable(data.toByteArray(), offset, bytesToWrite);
            offset += bytesToWrite;
        }
        cb.done();
        isSynthesizing = false;

    }


    /**
     * 获取或者创建WS
     * WebSocket
     *
     * @return WebSocket
     */
    public WebSocket getOrCreateWs() {

        if (this.webSocket == null) {
            synchronized (TTSService.class) {
                if (this.webSocket == null) {


                    while (TokenHolder.token == null) {
                        try {
                            this.wait(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //避免同时重复请求
                    while (webSocketState == WebSocketState.CONNECTING) {
                        try {
                            this.wait(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    String url;
                    String origin;
                    if (TokenHolder.token != null && APP.getBoolean(Constants.USE_PREVIEW, false)) {
                        url = "wss://eastus.tts.speech.microsoft.com/cognitiveservices/websocket/v1?Authorization=bearer " + TokenHolder.token + "&X-ConnectionId=" + CommonTool.getMD5String(new Date().toString());
                        origin = "https://azure.microsoft.com";
                        isPreview = true;
                    } else {
                        url = Constants.EDGE_URL;
                        isPreview = false;
                        origin = Constants.EDGE_ORIGIN;
                    }
                    Request request = new Request.Builder()
                            .url(url)
                            //.header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                            //.header("Accept-Encoding", "gzip, deflate")
                            .header("User-Agent", Constants.EDGE_UA)
                            .addHeader("Origin", origin)
                            .build();
                    webSocketState = WebSocketState.CONNECTING;
                    this.webSocket = client.newWebSocket(request, webSocketListener);
                    webSocketState = WebSocketState.CONNECTED;
                    sendConfig(Objects.requireNonNull(this.webSocket), new TtsConfig.Builder(APP.getInt(Constants.AUDIO_FORMAT_INDEX, 0)).build());

                }
            }


        }

        return this.webSocket;
    }

    /**
     * 发送合成语音配置,更改格式需要重新发送
     */
    private synchronized void sendConfig(WebSocket ws, TtsConfig ttsConfig) {
        String msg = "X-Timestamp:+" + getTime() + "\r\n" +
                "Content-Type:application/json; charset=utf-8\r\n" +
                "Path:speech.config\r\n\r\n"
                + ttsConfig.toString();
        this.currentFormat = ttsConfig.getFormat();
        ws.send(msg);

    }


    /**
     * 发送合成text请求
     *
     * @param request 需要合成的txt
     */
    public synchronized void sendText(SynthesisRequest request, SynthesisCallback callback) {
//
//        Bundle bundle = request.getParams();
//        Set<String> keySet = bundle.keySet();
//        for (String key : keySet) {
//            Log.e(TAG, key + "__" + bundle.get(key));
//        }

        //设置发送的音质
        int index = APP.getInt(Constants.AUDIO_FORMAT_INDEX, 0);


        TtsConfig ttsConfig = new TtsConfig.Builder(index).build();
        this.currentFormat = ttsConfig.getFormat();
        this.callback = callback;
        reNewWakeLock();


        String name = request.getVoiceName();
        if (APP.getBoolean(Constants.USE_CUSTOM_VOICE, true)) {
            name = APP.getString(Constants.CUSTOM_VOICE, "zh-CN-XiaoxiaoNeural");
        }
        int styleIndex = APP.getInt(Constants.VOICE_STYLE_INDEX, 0);
        TtsStyle ttsStyle = TtsStyleManger.getInstance().get(styleIndex);
        ttsStyle.setStyleDegree(APP.getInt(Constants.VOICE_STYLE_DEGREE, 100));
        ttsStyle.setVolume(APP.getInt(Constants.VOICE_VOLUME, 100));
        boolean useDict = APP.getBoolean(Constants.USE_DICT, false);


        //webSocket = webSocket == null ? getOrCreateWs() : webSocket;
        if (oldFormatIndex != index) {
            sendConfig(getOrCreateWs(), ttsConfig);
            oldFormatIndex = index;
        }
        SSML ssml = SSML.getInstance(request, name, ttsStyle, useDict, isPreview);
        Log.e(TAG, ssml.toString());
        //在Google Play图书之类应用会闪退，应该及时调用该方法
        callback.start(currentFormat.HZ,
                currentFormat.BitRate, 1 /* Number of channels. */);

        boolean success = getOrCreateWs().send(ssml.toString());
        //Log.e(TAG,"SSS:"+success);
        if (!success && isSynthesizing) {
            updateNotification("TTS服务-重试中", "第一次发送不成功，正在重试第二次");
            getOrCreateWs().send(ssml.toString());
        }


    }


    public static int getIsLanguageAvailable(String lang, String country, String variant) {
        Locale locale = new Locale(lang, country, variant);
        boolean isLanguage = false;
        boolean isCountry = false;
        for (String lan : Constants.supportedLanguages) {
            String[] temp = lan.split("-");
            Locale locale1 = new Locale(temp[0], temp[1]);
            if (locale.getISO3Language().equals(locale1.getISO3Language())) {
                isLanguage = true;
            }
            if (isLanguage && locale.getISO3Country().equals(locale1.getISO3Country())) {
                isCountry = true;
            }
            if (isCountry && locale.getVariant().equals(locale1.getVariant())) {
                return TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE;
            }

        }
        if (isCountry) {
            return TextToSpeech.LANG_COUNTRY_AVAILABLE;
        }
        if (isLanguage) {
            return TextToSpeech.LANG_AVAILABLE;
        }
        return TextToSpeech.LANG_NOT_SUPPORTED;
    }


    /**
     * 是否支持该语言。语言通过lang、country、variant这三个Locale的字段来表示，意思分别是语言、国家和地区，
     * 比如zh-CN表示大陆汉语。（ISO 639-1、ISO 639-2）。
     */
    @Override
    protected int onIsLanguageAvailable(String lang, String country, String variant) {
        return getIsLanguageAvailable(lang, country, variant);

    }

    /**
     * 获取当前引擎所设置的语言信息，返回值格式为{lang,country,variant}。
     *
     * @return String[] {lang,country,variant}。
     */
    @Override
    protected String[] onGetLanguage() {
        // Note that mCurrentLanguage is volatile because this can be called from
        // multiple threads.

        return mCurrentLanguage;
    }

    @Override
    public List<Voice> onGetVoices() {
        List<android.speech.tts.Voice> voices = new ArrayList<>();
        for (TtsActor voice : TtsActorManger.getInstance().getActors()) {
            int quality = Voice.QUALITY_VERY_HIGH;
            int latency = Voice.LATENCY_NORMAL;
            Locale locale = voice.getLocale();

            Set<String> features = onGetFeaturesForLanguage(locale.getLanguage(), locale.getCountry(), locale.getVariant());
            //Log.e(TAG, features.toString());
            voices.add(new android.speech.tts.Voice(voice.getShortName(), voice.getLocale(), quality, latency, true, features));
        }
        return voices;
    }

    @Override
    protected Set<String> onGetFeaturesForLanguage(String lang, String country, String variant) {
        return new HashSet<>();
    }

    public List<String> getVoiceNames(String lang, String country, String variant) {
        List<String> vos = new ArrayList<>();
        Locale locale = new Locale(lang, country, variant);
        List<TtsActor> ttsActors = TtsActorManger.getInstance().getActorsByLocale(locale);
        for (TtsActor actor : ttsActors) {
            vos.add(actor.getShortName());
        }
        return vos;
    }

    @Override
    public int onIsValidVoiceName(String voiceName) {
        for (String vn : Constants.supportVoiceNames) {
            if (voiceName.equalsIgnoreCase(vn)) {
                return TextToSpeech.SUCCESS;
            }
        }
        return TextToSpeech.ERROR;
    }

    @Override
    public int onLoadVoice(String voiceName) {
        TtsActor voice = TtsActorManger.getInstance().getByName(voiceName);
        if (voice == null) {
            return TextToSpeech.ERROR;
        }
        return TextToSpeech.SUCCESS;
    }

    /**
     * 获取对应地区的默认语音名
     *
     * @param lang    语言
     * @param country 地区
     * @param variant 分支
     * @return VoiceName
     */
    @Override
    public String onGetDefaultVoiceNameFor(String lang, String country, String variant) {
        String name = "zh-CN-XiaoxiaoNeural";
        if (variant.isEmpty()) {
            variant = "Female";
        }
        List<String> names = getVoiceNames(lang, country, variant);
        if (names.size() > 0) {
            name = names.get(0);
        }

        return name;
    }


    /**
     * 设置该语言，并返回是否是否支持该语言。
     * Note that this method is synchronized, as is onSynthesizeText because
     * onLoadLanguage can be called from multiple threads (while onSynthesizeText
     * is always called from a single thread only).
     */
    @Override
    protected int onLoadLanguage(String _lang, String _country, String _variant) {
        String lang = _lang == null ? "" : _lang;
        String country = _country == null ? "" : _country;
        String variant = _variant == null ? "" : _variant;
        int result = onIsLanguageAvailable(lang, country, variant);
        if (result == TextToSpeech.LANG_COUNTRY_AVAILABLE || TextToSpeech.LANG_AVAILABLE == result || result == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE) {
            mCurrentLanguage = new String[]{lang, country, variant};
        }

        return result;
    }

    /**
     * 停止tts播放或合成。
     */
    @Override
    protected void onStop() {
        if (TTSService.this.webSocket != null) {
            Objects.requireNonNull(webSocket).close(1000, "closed by call onStop");
            TTSService.this.webSocket = null;
        }
        isSynthesizing = false;
        mData.clear();
        updateNotification("TTS服务-停止中", "调用onStop，停止生成。");

    }


    /**
     * 将指定的文字，合成为tts音频流
     *
     * @param request  合成请求 SynthesisRequest
     * @param callback 合成callback SynthesisCallback
     */
    @Override
    protected void onSynthesizeText(SynthesisRequest request, SynthesisCallback callback) {

        int load = onLoadLanguage(request.getLanguage(), request.getCountry(),
                request.getVariant());
        if (load == TextToSpeech.LANG_NOT_SUPPORTED) {
            callback.error(TextToSpeech.ERROR_INVALID_REQUEST);
            Log.e(TAG, "语言不支持:" + request.getLanguage());
            return;
        }


        isSynthesizing = true;
        //判断是否全是不发声字符，如果是，直接跳过
        if (CommonTool.isNoVoice(request.getCharSequenceText())) {
            callback.start(16000,
                    AudioFormat.ENCODING_PCM_16BIT, 1 /* Number of channels. */);
            callback.done();
            isSynthesizing = false;
            return;
        }
        //使用System.nanoTime()来保证获得的是精准的时间间隔
        long startTime = SystemClock.elapsedRealtime();

        synchronized (TTSService.this) {

            isSynthesizing = true;
            sendText(request, callback);
            updateNotification("TTS服务-生成中", request.getCharSequenceText().toString());


            while (isSynthesizing) {
//                if(this.webSocket==null){
//                    this.webSocket=getOrCreateWs();
//                }
                try {
                    this.wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long time = SystemClock.elapsedRealtime() - startTime;
                //超时50秒后跳过,保证长句不会被跳过
                if (time > 50000) {
                    callback.error(TextToSpeech.ERROR_NETWORK_TIMEOUT);
                    isSynthesizing = false;
                }
            }

        }
        isSynthesizing = false;

        updateNotification("TTS服务-闲置中", "当前没有生成任务");


    }


}