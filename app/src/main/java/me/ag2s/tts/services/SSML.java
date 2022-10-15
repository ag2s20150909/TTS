package me.ag2s.tts.services;

import android.speech.tts.SynthesisRequest;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import me.ag2s.tts.APP;
import me.ag2s.tts.utils.CommonTool;
import me.ag2s.tts.utils.GcManger;

public class SSML {

    /**
     * 把常用的影响分句的重复符号合并
     */
    static final Pattern p0 = Pattern.compile("([\\s　！。？?])+");
    /**
     * 单字符断句符,排除后面有引号的情况
     */
    static final Pattern p1 = Pattern.compile("([;。：！？?])([^”’])");
    /**
     * 中英文省略号处理
     */
    static final Pattern p2 = Pattern.compile("(\\.{6}|…{2})([^”’])");
    /**
     * 多字符断句符，后面有引号的的情况
     */
    static final Pattern p3 = Pattern.compile("([。！？?][”’])([^，。！？?])");
//
//
//    /**
//     * 修复在小说中“重”作为量词时(读chong 2)的错误读音。这在修仙类小说中很常见.
//     */
    //static final Pattern p4 = Pattern.compile("重(?=[一二三四五六七八九十])|(?<=[一二三四五六七八九十])重");


    /**
     * 指定讲话语言。 目前，讲不同的语言是特定于语音的。
     * 如果调整神经语音的讲话语言，则为必需项。 如果使用 lang xml:lang，则必须提供区域设置。
     */
    private final String lang;


    /**
     * 请求的id
     */
    private String id;
    /**
     * 请求的时间戳
     */
    private String time;

    /**
     * 发音角色
     */
    private String name;

    /**
     * 发音风格
     */
    private WeakReference<TtsStyle> style;

    /**
     * 发音内容
     */
    private StringBuilder content;

    private short pitch;
    private short rate;
    private boolean useDict;
    //是否使用预览版
    private boolean usePre;

    private static SSML instance;


    private SSML(SynthesisRequest request, String name, TtsStyle ttsStyle, boolean useDict, boolean usePre) {
        this.content = new StringBuilder(request.getCharSequenceText());
        this.useDict = useDict;
        this.usePre = usePre;
        this.name = name;
        this.style = new WeakReference<>(ttsStyle);
        this.time = CommonTool.getTime();
        this.pitch = (short) (request.getPitch() - 100);
        this.rate = (short) (request.getSpeechRate());
        Locale locale = Locale.getDefault();
        if (name.contains("Multilingual")) {
            locale = Locale.CHINA;
        }

        this.lang = locale.getLanguage() + "-" + locale.getCountry();
        this.id = CommonTool.getMD5String(request.getCharSequenceText() + "" + System.currentTimeMillis());
        this.useDict = useDict;
        handleContent();

    }

    public static SSML getInstance(SynthesisRequest request, String name, TtsStyle ttsStyle, boolean useDict, boolean usePre) {
        if (instance == null) {
            instance = new SSML(request, name, ttsStyle, useDict, usePre);
        } else {
            instance.content = new StringBuilder(request.getCharSequenceText());
            instance.useDict = useDict;
            instance.usePre = usePre;
            instance.name = name;
            instance.style = new WeakReference<>(ttsStyle);
            instance.time = CommonTool.getTime();
            instance.pitch = (short) (request.getPitch() - 100);
            instance.rate = (short) (request.getSpeechRate());
            instance.id = CommonTool.getMD5String(request.getCharSequenceText() + "" + System.currentTimeMillis());
            instance.handleContent();
        }
        return instance;
    }


    /**
     * 处理文本
     */
    private void handleContent() {
        CommonTool.replace(content, "\n", " ");
        CommonTool.Trim(content);
        CommonTool.replace(content, "&", "&amp;");
        CommonTool.replace(content, "\"", "&quot;");
        CommonTool.replace(content, "'", "&apos;");
        CommonTool.replace(content, ">", "&lt;");
        CommonTool.replace(content, "<", "&gt;");
        //是否分段
        if (APP.getBoolean(Constants.SPLIT_SENTENCE, false) && usePre) {
            String temp = content.toString();
            temp = p0.matcher(temp).replaceAll("$1");//把常用的影响分句的重复符号合并
            temp = p1.matcher(temp).replaceAll("$1</p><p>$2");//单字符断句符,排除后面有引号的情况
            temp = p2.matcher(temp).replaceAll("<break strength='strong' />$2");//中英文省略号停顿处理
            temp = p3.matcher(temp).replaceAll("$1</p><p>$2");//多字符断句符，后面有引号的的情况
//            if (name.startsWith("zh-CN")) {
//                //temp = p4.matcher(temp).replaceAll("<phoneme alphabet='sapi' ph='chong 2'>重</phoneme>");
//            }
            content = new StringBuilder(temp);
            GcManger.getInstance().doGC();
        }

        //是否使用字典
        if (useDict) {
            List<TtsDict> dictList = TtsDictManger.getInstance().getDict();
            for (TtsDict dict : dictList) {
                if (dict.isRegex()) {
                    CommonTool.replaceAll(content, dict.getWorld(), dict.getXML(name));
                } else {
                    CommonTool.replace(content, dict.getWorld(), dict.getXML(name));
                }

            }
        }


    }


    @NonNull
    @Override
    public String toString() {
        String rateString = rate / 100 + "." + rate % 100;
//        if (!usePre) {
//            return "Path: ssml" + "\r\n" +
//                    "X-RequestId: " + id + "\r\n" +
//                    "X-Timestamp: " + time + "Z" + "\r\n" +
//                    "Content-Type: application/ssml+xml" + "\r\n\r\n" +
//                    "<speak xmlns=\"http://www.w3.org/2001/10/synthesis\" xmlns:mstts=\"http://www.w3.org/2001/mstts\" xmlns:emo=\"http://www.w3.org/2009/10/emotionml\" version=\"1.0\" xml:lang=\"en-US\"><voice name=\"" + name + "\"><prosody rate=\"" + rateString + "%\" pitch=\"" + pitch + "%\">" + content.toString() + "\r\n" +
//                    "</prosody></voice></speak>";
//        }
        //String pitchString = pitch >= 0 ? "+" + pitch + "Hz" : pitch + "Hz";
        StringBuilder sb = new StringBuilder()
                .append("Path:ssml\r\n")
                .append("X-RequestId:").append(id).append("\r\n")
                .append("X-Timestamp:").append(time).append("Z\r\n")
                .append("Content-Type:application/ssml+xml\r\n\r\n");


        sb.append("<speak version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xmlns:emo=\"http://www.w3.org/2009/10/emotionml\"  xmlns:mstts=\"https://www.w3.org/2001/mstts\" xml:lang=\"").append(lang).append("\">");
        sb.append("<voice  name=\"").append(name).append("\">");
        if (usePre) {
            sb.append("<lang xml:lang=\"").append(lang).append("\">");
        }
        sb.append("<prosody pitch=\"").append(pitch).append("%\" ").append("rate=\"").append(rateString).append("\" ").append("volume=\"").append(style.get().getVolume()).append("\">");

        if (usePre) {
            sb.append("<mstts:express-as  style=\"").append(style.get().value).append("\" styledegree=\"").append(style.get().getStyleDegree()).append("\" ><p>").append(content.toString()).append("</p></mstts:express-as>");
        } else {
            sb.append("").append(content.toString()).append("");

        }


        sb.append("</prosody>");
        if (usePre) {
            sb.append("</lang>");
        }

        sb.append("</voice></speak>");

        return sb.toString();
    }
}
