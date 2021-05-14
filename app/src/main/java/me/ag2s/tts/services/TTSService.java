package me.ag2s.tts.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.speech.tts.Voice;
import android.util.Log;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import me.ag2s.tts.APP;
import me.ag2s.tts.utils.CommonTool;
import me.ag2s.tts.utils.OkHttpDns;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class TTSService extends TextToSpeechService {


    private static final String TAG = TTSService.class.getSimpleName();

    public static final String USE_CUSTOM_VOICE = "use_custom_voice";
    public static final String CUSTOM_VOICE = "custom_voice";
    public static final String CUSTOM_VOICE_INDEX = "custom_voice_index";
    public static final String VOICE_STYLE = "voice_style";
    public static final String VOICE_STYLE_DEGREE = "voice_style_degree";
    public static final String VOICE_STYLE_INDEX = "voice_style_index";
    public static final String USE_AUTO_RETRY = "use_auto_retry";
    public static final String USE_AUTO_UPDATE = "use_auto_update";
    public static final String AUDIO_FORMAT_INDEX = "audio_format_index";

    public SharedPreferences sharedPreferences;
    private OkHttpClient client;
    private WebSocket webSocket;
    private PowerManager powerManager;
    private boolean isSynthesizing;
    private static List<TtsActor> languages;
    private List<Voice> voices;

    public final static String[] supportedLanguages = {"deu-DEU", "eng-AUS", "eng-CAN", "eng-GBR", "eng-IND", "eng-USA", "spa-ESP", "spa-MEX", "fra-CAN", "fra-FRA", "hin-IND", "ita-ITA", "jpn-JPN", "kor-KOR", "nld-NLD", "pol-POL", "por-BRA", "rus-RUS", "tur-TUR", "zho-CHN", "zho-HKG", "zho-TWN"};

    public final static String[] supportVoiceNames = {"de-DE-KatjaNeural", "en-AU-NatashaNeural", "en-CA-ClaraNeural", "en-GB-MiaNeural", "en-IN-NeerjaNeural", "en-US-AriaNeural", "en-US-GuyNeural", "es-ES-ElviraNeural", "es-MX-DaliaNeural", "fr-CA-SylvieNeural", "fr-FR-DeniseNeural", "hi-IN-SwaraNeural", "it-IT-ElsaNeural", "ja-JP-NanamiNeural", "ko-KR-SunHiNeural", "nl-NL-ColetteNeural", "pl-PL-ZofiaNeural", "pt-BR-FranciscaNeural", "ru-RU-SvetlanaNeural", "tr-TR-EmelNeural", "zh-CN-XiaoxiaoNeural", "zh-CN-YunyangNeural", "zh-HK-HiuGaaiNeural", "zh-TW-HsiaoYuNeural"};

    public final static String[] supportVoiceLocales = {"de_DE", "en_AU", "en_CA", "en_GB", "en_IN", "en_US", "en_US", "es_ES", "es_MX", "fr_CA", "fr_FR", "hi_IN", "it_IT", "ja_JP", "ko_KR", "nl_NL", "pl_PL", "pt_BR", "ru_RU", "tr_TR", "zh_CN", "zh_CN", "zh_HK", "zh_TW"};

    public final static String[] supportVoiceVariants = {"Female", "Female", "Female", "Female", "Female", "Female", "Male", "Female", "Female", "Female", "Female", "Female", "Female", "Female", "Female", "Female", "Female", "Female", "Female", "Female", "Female", "Male", "Female", "Female"};
    private volatile String[] mCurrentLanguage = null;
    /*
     * This is the sampling rate of our output audio. This engine outputs
     * audio at 16khz 16bits per sample PCM audio.
     */
    private   int oldindex=0;
    SynthesisCallback callback;
    private final WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
            Log.v(TAG, "onClosed" + reason);
            TTSService.this.webSocket = null;
            callback.done();
            isSynthesizing = false;
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);

        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            Log.v(TAG, "onFailure", t);
            TTSService.this.webSocket = null;
            callback.done();
            isSynthesizing = false;

            if (sharedPreferences.getBoolean(USE_AUTO_RETRY, false)) {
                Log.d(TAG, "AAAA:使用自动重试。");
                TTSService.this.webSocket = getOrCreateWs();
            }

        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
            //Log.v(TAG, "onMessage"+text);
            String endTag = "turn.end";
            String startTag = "turn.start";
            int endIndex = text.lastIndexOf(endTag);
            int startIndex = text.lastIndexOf(startTag);
            //生成开始
            if (startIndex != -1) {
                isSynthesizing = true;
            }
            //生成结束
            if (endIndex != -1) {
                isSynthesizing = false;
                if (callback != null && !callback.hasFinished()) {
                    callback.done();
                }

            }
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
            //音频数据流标志头
            String audioTag = "Path:audio\r\n";
            int audioIndex = bytes.lastIndexOf(audioTag.getBytes(StandardCharsets.UTF_8));
            if (audioIndex != -1 && callback != null) {
                try {
                    //PCM数据
                    ByteString data = bytes.substring(audioIndex + audioTag.length());
                    int length = data.toByteArray().length;
                    //最大BufferSize
                    final int maxBufferSize = callback.getMaxBufferSize();
                    int offset = 0;
                    while (offset < data.toByteArray().length) {
                        int bytesToWrite = Math.min(maxBufferSize, length - offset);
//                            Log.d(TAG, "maxBufferSize" + maxBufferSize +
//                                    "data.length - offset" + (length - offset));
                        callback.audioAvailable(data.toByteArray(), offset, bytesToWrite);
                        offset += bytesToWrite;
                    }

                } catch (Exception e) {
                    Log.d(TAG, "onMessage Error:", e);

                    //如果出错返回错误
                    callback.error();
                    isSynthesizing = false;
                }

            }
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            Log.d(TAG, "onOpen" + response.headers().toString());
        }
    };

    public TTSService() {
    }

    /**
     * 获取或者创建WS
     * WebSocket
     *
     * @return WebSocket
     */
    public WebSocket getOrCreateWs() {
        if (this.webSocket != null) {
            return this.webSocket;
        }
        String url = "https://speech.platform.bing.com/consumer/speech/synthesize/readaloud/edge/v1?TrustedClientToken=6A5AA1D4EAFF4E9FB37E23D68491D6F4";
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36 Edg/90.0.818.56")
                .addHeader("Origin", "chrome-extension://jdiccldimpdaibmpdkjnbmckianbfold")
                .build();
        this.webSocket = client.newWebSocket(request, webSocketListener);
        sendConfig(this.webSocket, new TtsConfig.Builder(sharedPreferences.getInt(AUDIO_FORMAT_INDEX, 0)).sentenceBoundaryEnabled(true).build());
        return webSocket;
    }

    //发送合成语音配置
    private void sendConfig(WebSocket ws, TtsConfig ttsConfig) {
        String msg = "X-Timestamp:+" + getTime() + "\r\n" +
//                "Connection: Keep-Alive\r\n" +
                "Content-Type:application/json; charset=utf-8\r\n" +
                "Path:speech.config\r\n\r\n"
                + ttsConfig.toString();
        ws.send(msg);
    }

    /**
     * 获取时间戳
     *
     * @return String time
     */
    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (中国标准时间)", Locale.ENGLISH);
        Date date = new Date();
        return sdf.format(date);
    }

    /**
     * 发送合成text请求
     *
     * @param request 需要合成的txt
     */
    public void sendText(SynthesisRequest request, SynthesisCallback callback) {


        Bundle bundle = request.getParams();
        if (bundle != null) {
            Set<String> keySet = bundle.keySet();
            for (String key : keySet) {
                Object value = bundle.get(key);
                Log.d(TAG, "sendText" + key + ":" + value.toString());
            }
        }

        String text = CommonTool.FixTrim(request.getCharSequenceText().toString());
        Log.d(TAG, "源：" + text);
        text = text.replaceAll("[-.]", "");
        Log.d(TAG, "长度：" + text.length());
        if (text.length() < 1) {
            callback.done();
            isSynthesizing = false;
            return;
        }
        //text=CommonTool.encodeHtml(text);


        int pitch = request.getPitch() - 100;
        int rate = request.getSpeechRate() - 100;
        String rateString = rate >= 0 ? "+" + rate + "%" : rate + "%";
        String pitchString = pitch >= 0 ? "+" + pitch + "Hz" : pitch + "Hz";


        String style = sharedPreferences.getString(VOICE_STYLE, "cheerful");
        String styleDegreeString = CommonTool.div(sharedPreferences.getInt(VOICE_STYLE_DEGREE, 100), 100, 2) + "";

        String name = request.getVoiceName();
        String time = getTime();
        Locale locale = Locale.getDefault();
        if (sharedPreferences.getBoolean(USE_CUSTOM_VOICE, false) && request.getLanguage().equals(locale.getISO3Language())) {
            name = sharedPreferences.getString(CUSTOM_VOICE, "zh-CN-XiaoxiaoNeural");
        }

        String RequestId = CommonTool.getMD5String(text + time + request.getCallerUid());

        String xml = locale.getLanguage() + "_" + locale.getCountry();


        Log.d(TAG, "SSS:" + request.getVoiceName());
        Log.d(TAG, "SSS:" + CommonTool.getMD5String(time));
        //role='OlderAdultMale'
        String sb = "X-RequestId:" + RequestId + "\r\n" +
                "Content-Type:application/ssml+xml\r\n" +
                "X-Timestamp:" + time + "Z\r\n" +
                "Path:ssml\r\n\r\n" +
                "<speak version='1.0' xmlns='http://www.w3.org/2001/10/synthesis' xmlns:mstts='https://www.w3.org/2001/mstts' xml:lang='" + xml + "'>" +
                "<voice  name='" + name + "'>" +
                "<prosody pitch='" + pitchString + "' " +
                "rate ='" + rateString + "' " +
                "volume='+" + 0 + "%'>" +
//                text+
                "<mstts:express-as  style='" + style + "' styledegree='" + styleDegreeString + "' >" + text + "</mstts:express-as>" +
                "</prosody></voice></speak>" +
                "";
        //Log.d(TAG, "SSS:" + sb);
        int index=sharedPreferences.getInt(AUDIO_FORMAT_INDEX, 0);
        TtsConfig ttsConfig=new TtsConfig.Builder(index).build();
        TtsOutputFormat format =ttsConfig.getFormat();
        this.callback.start(format.HZ,
                format.BitRate, 1 /* Number of channels. */);
        webSocket = getOrCreateWs();


        if (oldindex!=index){
            sendConfig(webSocket,ttsConfig);
            oldindex=index;
        }
        webSocket.send(sb);
    }

    public OkHttpDns getDNS() {
        ArrayList<String> whitelist = new ArrayList<>();

        return new OkHttpDns.Builder().client(
                APP.getBootClient()
                        .newBuilder()
                        .cache(getCache("doh", 1024 * 1024 * 100))
                        //.addNetworkInterceptor(new CacheInterceptor())
                        .build()
        )

                .url(HttpUrl.get("https://doh.360.cn/dns-query"))//30ms
                .wurl(HttpUrl.get("https://1.1.1.1/dns-query"))
                .whitelist(whitelist)
                .includeIPv6(true)
                .build();
    }

    public okhttp3.Cache getCache(String name, int maxSize) {
        File file = new File(getExternalCacheDir(), name);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                Log.e(TAG, file.getAbsolutePath() + " mkdirs was not successful.");
            }
        }
        return new okhttp3.Cache(file, maxSize);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        client = APP.getBootClient().newBuilder()
                .cookieJar(new PersistentCookieJar(new SetCookieCache(),
                        new SharedPrefsCookiePersistor(getApplicationContext())))
                //.addNetworkInterceptor(new LoggingInterceptor())
                //.pingInterval(40, TimeUnit.SECONDS) // 设置 PING 帧发送间隔
                .dns(getDNS())
                .build();
        sharedPreferences = getApplicationContext().getSharedPreferences("TTS", Context.MODE_PRIVATE);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);


    }


    public static int getIsLanguageAvailable(String lang, String country, String variant) {
        Locale locale = new Locale(lang, country, variant);
        for (String lan : supportedLanguages) {
            String[] temp = lan.split("-");
            Locale locale1 = new Locale(temp[0], temp[1]);
            if (locale.getISO3Language().equals(locale1.getISO3Language())) {
                if (locale.getCountry().equals(locale1.getCountry())) {
                    if (locale.getVariant().equals(locale1.getVariant())) {
                        return TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE;
                    }
                    return TextToSpeech.LANG_COUNTRY_AVAILABLE;
                }
                return TextToSpeech.LANG_AVAILABLE;
            }
        }
        return TextToSpeech.LANG_NOT_SUPPORTED;
    }


    /**
     * 是否支持该语言。语言通过lang、country、variant这三个Locale的字段来表示，意思分别是语言、国家和地区，
     * 比如zh-CN表示大陆汉语。这个方法看着简单，但我在这里栽坑了好久，就是因为对语言编码标准（ISO 639-1、ISO 639-2）不熟悉。
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

        return super.onGetVoices();
    }


    @Override
    protected Set<String> onGetFeaturesForLanguage(String lang, String country, String variant) {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add(lang);
        hashSet.add(country);
        hashSet.add(variant);
        return hashSet;
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
        for (String vn : supportVoiceNames) {
            if (voiceName.equals(vn)) {
                return TextToSpeech.SUCCESS;
            }
        }
        return TextToSpeech.SUCCESS;
    }

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
        //name="zh-cn-XiaoyouNeural";

        return name;
    }


    @Override
    public int onLoadVoice(String voiceName) {
        return TextToSpeech.SUCCESS;
    }

    /**
     * 设置该语言，并返回是否是否支持该语言。
     * Note that this method is synchronized, as is onSynthesizeText because
     * onLoadLanguage can be called from multiple threads (while onSynthesizeText
     * is always called from a single thread only).
     */
    @Override
    protected int onLoadLanguage(String lang, String country, String variant) {
        int result = onIsLanguageAvailable(lang, country, variant);
        if (result == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
            mCurrentLanguage = new String[]{lang, country, variant};
        }

        return result;
    }

    /**
     * 停止tts播放或合成。
     */
    @Override
    protected void onStop() {
        isSynthesizing = false;
    }


    /**
     * 将指定的文字，合成为tts音频流
     *
     * @param request  合成请求 SynthesisRequest
     * @param callback 合成callback SynthesisCallback
     */
    @SuppressLint("WrongConstant")
    @Override
    protected void onSynthesizeText(SynthesisRequest request, SynthesisCallback callback) {
        // Note that we call onLoadLanguage here since there is no guarantee
        // that there would have been a prior call to this function.
        int load = onLoadLanguage(request.getLanguage(), request.getCountry(),
                request.getVariant());
        // We might get requests for a language we don't support - in which case
        // we error out early before wasting too much time.
        if (load == TextToSpeech.LANG_NOT_SUPPORTED) {
            callback.error(TextToSpeech.ERROR_INVALID_REQUEST);
            return;
        }
        // At this point, we have loaded the language we need for synthesis and
        // it is guaranteed that we support it so we proceed with synthesis.

        // We denote that we are ready to start sending audio across to the
        // framework. We use a fixed sampling rate (16khz), and send data across
        // in 16bit PCM mono.
        this.callback = callback;

        isSynthesizing = true;
        sendText(request, this.callback);

        while (isSynthesizing) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


}