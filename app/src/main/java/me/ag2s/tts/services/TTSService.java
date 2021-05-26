package me.ag2s.tts.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.speech.tts.Voice;
import android.text.TextUtils;
import android.util.Log;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import me.ag2s.tts.APP;
import me.ag2s.tts.utils.ByteArrayMediaDataSource;
import me.ag2s.tts.utils.CommonTool;
import me.ag2s.tts.utils.OkHttpDns;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.Buffer;
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
    //当前的生成格式
    private volatile TtsOutputFormat currentFormat;
    //当前的数据
    private Buffer mData;
    public static MediaCodec mediaCodec;
    public volatile String currentMime;


    private static List<TtsActor> languages;
    private List<Voice> voices;

    public final static String[] supportedLanguages = {"zho-CHN", "zho-HKG", "zho-TWN", "eng-IRL", "bul-BGR", "nld-NLD", "lav-LVA", "est-EST", "fra-FRA", "gle-IRL", "ara-SAU", "deu-AUT", "eng-PHL", "fra-CHE", "por-BRA", "eng-GBR", "fin-FIN", "swe-SWE", "ukr-UKR", "ell-GRC", "ces-CZE", "msa-MYS", "tam-IND", "kor-KOR", "slv-SVN", "spa-MEX", "deu-DEU", "eng-CAN", "ita-ITA", "tur-TUR", "deu-CHE", "por-PRT", "tha-THA", "eng-IND", "ara-EGY", "fra-CAN", "hrv-HRV", "hun-HUN", "urd-PAK", "tel-IND", "nob-NOR", "eng-AUS", "jpn-JPN", "heb-ISR", "eng-USA", "ron-ROU", "cym-GBR", "hin-IND", "ind-IDN", "cat-ESP", "mlt-MLT", "rus-RUS", "vie-VNM", "slk-SVK", "nld-BEL", "fra-BEL", "lit-LTU", "dan-DNK"};

    public final static String[] supportVoiceNames = {"de-DE-KatjaNeural", "en-AU-NatashaNeural", "en-CA-ClaraNeural", "en-GB-MiaNeural", "en-IN-NeerjaNeural", "en-US-AriaNeural", "en-US-GuyNeural", "es-ES-ElviraNeural", "es-MX-DaliaNeural", "fr-CA-SylvieNeural", "fr-FR-DeniseNeural", "hi-IN-SwaraNeural", "it-IT-ElsaNeural", "ja-JP-NanamiNeural", "ko-KR-SunHiNeural", "nl-NL-ColetteNeural", "pl-PL-ZofiaNeural", "pt-BR-FranciscaNeural", "ru-RU-SvetlanaNeural", "tr-TR-EmelNeural", "zh-CN-XiaoxiaoNeural", "zh-CN-YunyangNeural", "zh-HK-HiuGaaiNeural", "zh-TW-HsiaoYuNeural"};

    private volatile String[] mCurrentLanguage = null;
    private PowerManager.WakeLock wakelock;
    /*
     * This is the sampling rate of our output audio. This engine outputs
     * audio at 16khz 16bits per sample PCM audio.
     */
    private int oldindex = 0;
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
                mData = new Buffer();
            }
            //生成结束
            if (endIndex != -1) {

                if (callback != null && !callback.hasFinished()) {
                    if (!currentFormat.needDecode) {
                        callback.done();
                        isSynthesizing = false;
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
            String audioTag = "Path:audio\r\n";
            String startTag = "Content-Type:";
            String endTag = "\r\nX-StreamId";

            int audioIndex = bytes.lastIndexOf(audioTag.getBytes(StandardCharsets.UTF_8)) + audioTag.length();
            int startIndex = bytes.lastIndexOf(startTag.getBytes(StandardCharsets.UTF_8)) + startTag.length();
            int endIndex = bytes.lastIndexOf(endTag.getBytes(StandardCharsets.UTF_8));
            if (audioIndex != -1 && callback != null) {
                try {
                    currentMime = bytes.substring(startIndex, endIndex).utf8();

                    if (!currentFormat.needDecode) {
                        //audioIndex=bytes.lastIndexOf("RIFF".getBytes(StandardCharsets.UTF_8))+44;
                        doUnDecode(callback, currentFormat, bytes.substring(audioIndex));
                    } else {
                        mData.write(bytes.substring(audioIndex));
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


    private void doDecode(SynthesisCallback cb, TtsOutputFormat format, ByteString data) {
        try {
            MediaExtractor mediaExtractor = new MediaExtractor();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //mediaExtractor.setDataSource("data:"+currentMime+";base64,"+data.base64());
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
                    Log.d(TAG, "找到音频流的索引为：" + trackFormat.toString());
                    break;
                }
            }
            //没有找到音频流的情况下
            if (audioTrackIndex == -1) {
                Log.e(TAG, "initAudioDecoder: 没有找到音频流");
                cb.done();
                isSynthesizing = false;
                return;
            }
            //选择此音轨
            mediaExtractor.selectTrack(audioTrackIndex);



            //创建解码器
            mediaCodec = MediaCodec.createDecoderByType(mime);

            mediaCodec.configure(trackFormat, null, null, 0);


            mediaCodec.start();
            if (trackFormat.containsKey(MediaFormat.KEY_SAMPLE_RATE)) {
                cb.start(trackFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE), AudioFormat.ENCODING_PCM_16BIT, trackFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT));
            } else {
                cb.start(currentFormat.HZ, AudioFormat.ENCODING_PCM_16BIT, trackFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT));
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            ByteBuffer inputBuffer;
            Log.d(TAG, mediaCodec.getInputFormat().toString());
            Log.d(TAG, mediaCodec.getOutputFormat().toString());
            while (mediaCodec != null) {
                //获取可用的inputBuffer，输入参数-1代表一直等到，0代表不等待，10*1000代表10秒超时
                //超时时间10秒
                long TIME_OUT_US = 10 * 1000;
                int inputIndex = mediaCodec.dequeueInputBuffer(TIME_OUT_US);
                if (inputIndex < 0) {
                    break;
                }
                inputBuffer = mediaCodec.getInputBuffer(inputIndex);
                if (inputBuffer != null) {
                    inputBuffer.clear();
                } else {
                    continue;
                }
                //从流中读取的采用数据的大小
                int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);
                if (sampleSize > 0) {
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
                //player.play();
                while (outputIndex >= 0) {
                    outputBuffer = mediaCodec.getOutputBuffer(outputIndex);
                    pcmData = new byte[bufferInfo.size];
                    if (outputBuffer != null) {
                        outputBuffer.get(pcmData);
                        outputBuffer.clear();//用完后清空，复用
                    }

                    //player.write(pcmData, 0, bufferInfo.size);
                    cb.audioAvailable(pcmData, 0, bufferInfo.size);
                    //释放
                    mediaCodec.releaseOutputBuffer(outputIndex, false);
                    //再次获取数据
                    outputIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIME_OUT_US);
                }
            }
            cb.done();
            isSynthesizing = false;

        } catch (Exception e) {
            Log.e(TAG, "doDecode", e);
            cb.error();
            isSynthesizing = false;
        }
    }

    private void doUnDecode(SynthesisCallback cb, TtsOutputFormat format, ByteString data) {
        int length = data.toByteArray().length;
        //最大BufferSize
        final int maxBufferSize = cb.getMaxBufferSize();
        int offset = 0;
        while (offset < length) {
            int bytesToWrite = Math.min(maxBufferSize, length - offset);
            cb.audioAvailable(data.toByteArray(), offset, bytesToWrite);
            offset += bytesToWrite;
        }
        //cb.done();
        //isSynthesizing = false;
    }


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
                "Content-Type:application/json; charset=utf-8\r\n" +
                "Path:speech.config\r\n\r\n"
                + ttsConfig.toString();
        this.currentFormat = ttsConfig.getFormat();
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
        int index = sharedPreferences.getInt(AUDIO_FORMAT_INDEX, 0);
        TtsConfig ttsConfig = new TtsConfig.Builder(index).build();
        TtsOutputFormat format = ttsConfig.getFormat();
        currentFormat = format;

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
        //替换一些会导致不返回数据的特殊字符
        //String temp = text.replaceAll("[-.<>*#@%$…]", "");
        //替换
        String temp = text.replaceAll("[\\s\\p{P}\\p{Z}\\p{N}\\p{S}]", "");
        Log.d(TAG, "长度：" + temp.length());
        //长度为0，直接跳过。
        if (temp.length() < 1) {
            callback.start(format.HZ,
                    format.BitRate, 1 /* Number of channels. */);
            callback.done();
            isSynthesizing = false;
            return;
        }

        text = text.replace("]]>", "");
        //"<![CDATA[" "]]>"

        //测试词典功能
        //"<phoneme alphabet='sapi' ph='cao 4 ni 3 ma 1' >艹尼M</phoneme>"
        text = "<![CDATA[" + text + "]]>";

        //text=CommonTool.encodeHtml(text);
        Log.d(TAG, "源：" + text);
        Log.d(TAG, "源：" + currentFormat.toString());


        int pitch = request.getPitch() - 100;
        int rate = request.getSpeechRate() - 100;
        String rateString = rate >= 0 ? "+" + rate + "%" : rate + "%";
        String pitchString = pitch >= 0 ? "+" + pitch + "Hz" : pitch + "Hz";


        String style = sharedPreferences.getString(VOICE_STYLE, "cheerful");
        String styleDegreeString = CommonTool.div(sharedPreferences.getInt(VOICE_STYLE_DEGREE, 100), 100, 2) + "";

        String name = request.getVoiceName();
        String time = getTime();
        Locale locale = Locale.getDefault();
        //&& request.getLanguage().equals(locale.getISO3Language())
        if (sharedPreferences.getBoolean(USE_CUSTOM_VOICE, false)) {
            name = sharedPreferences.getString(CUSTOM_VOICE, "zh-CN-XiaoxiaoNeural");
        }

        String RequestId = CommonTool.getMD5String(text + time + request.getCallerUid());
//        if(request.getLanguage().equals(Locale.CHINESE.getISO3Language())){
//
//        }

        String xml = locale.getLanguage() + "-" + locale.getCountry();


        Log.d(TAG, "SSS:" + name);
        Log.d(TAG, "SSS:" + xml);
        //role='OlderAdultMale'
        String sb = "X-RequestId:" + RequestId + "\r\n" +
                "Content-Type:application/ssml+xml\r\n" +
                "X-Timestamp:" + time + "Z\r\n" +
                "Path:ssml\r\n\r\n" +
                "<speak version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xmlns:mstts=\"https://www.w3.org/2001/mstts\" xml:lang=\"" + xml + "\">" +
                "<voice  name=\"" + name + "\">" +
                "<prosody pitch=\"" + pitchString + "\" " +
                "rate =\"" + rateString + "\" " +
                "volume=\"+" + 0 + "%\">" +
//                text+
                "<mstts:express-as  style=\"" + style + "\" styledegree=\"" + styleDegreeString + "\" >" + text + "</mstts:express-as>" +
                "</prosody></voice></speak>" +
                "";
        Log.d(TAG, "SSS:" + sb);


        webSocket = getOrCreateWs();

        if (oldindex != index) {
            sendConfig(webSocket, ttsConfig);
            oldindex = index;
        }
        if (!currentFormat.needDecode) {
            callback.start(format.HZ,
                    format.BitRate, 1 /* Number of channels. */);
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


    @SuppressLint("InvalidWakeLockTag")
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
        // 获得唤醒锁
        wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
        wakelock.acquire(100 * 60 * 1000L /*10 minutes*/);


    }


    public static int getIsLanguageAvailable(String lang, String country, String variant) {
        Locale locale = new Locale(lang, country, variant);
        boolean isLanguage = false;
        boolean isCountry = false;
        for (String lan : supportedLanguages) {
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
            if (voiceName.toLowerCase().equals(vn.toLowerCase())) {
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
        //webSocket.close(1000,"closed by call onStop");
        isSynthesizing = false;
    }


    /**
     * 将指定的文字，合成为tts音频流
     *
     * @param request  合成请求 SynthesisRequest
     * @param callback 合成callback SynthesisCallback
     */
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
            Log.e(TAG, "语言不支持:" + request.getLanguage());
            return;
        }
        // At this point, we have loaded the language we need for synthesis and
        // it is guaranteed that we support it so we proceed with synthesis.

        // We denote that we are ready to start sending audio across to the
        // framework. We use a fixed sampling rate (16khz), and send data across
        // in 16bit PCM mono.
        this.callback = callback;

        isSynthesizing = true;
        //使用System.nanoTime()来保证获得的是精准的时间间隔

        long startTime = System.nanoTime();
        sendText(request, this.callback);

        while (isSynthesizing) {
            try {
                Thread.sleep(100);
                long time = System.nanoTime() - startTime;
                //超时40秒后跳过
                if (time > 40E9) {
                    //callback.error(TextToSpeech.ERROR_NETWORK_TIMEOUT);
                    isSynthesizing = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onDestroy() {
        // 释放唤醒锁, 如果没有其它唤醒锁存在, 设备会很快进入休眠状态
        wakelock.release();
        super.onDestroy();
    }
}