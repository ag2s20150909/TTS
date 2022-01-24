package me.ag2s.tts.services;

import android.speech.tts.SynthesisRequest;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Locale;

import me.ag2s.tts.utils.CommonTool;

public class SSML {

    /**
     * 指定讲话语言。 目前，讲不同的语言是特定于语音的。
     * 如果调整神经语音的讲话语言，则为必需项。 如果使用 lang xml:lang，则必须提供区域设置。
     */
    private final String lang;


    /**
     * 请求的id
     */
    private final String id;
    /**
     * 请求的时间戳
     */
    private final String time;

    /**
     * 发音角色
     */
    private final String name;

    /**
     * 发音风格
     */
    private final TtsStyle style;

    /**
     * 发音内容
     */
    private StringBuilder content;

    private final short pitch;
    private final short rate;
    private boolean useDict;


    public SSML(SynthesisRequest request, String name, TtsStyle ttsStyle, boolean useDict) {
        this.content = new StringBuilder(request.getCharSequenceText());
        this.useDict = useDict;
        this.name = name;
        this.style = ttsStyle;
        this.time = CommonTool.getTime();
        this.pitch = (short) (request.getPitch() - 100);
        this.rate = (short) (request.getSpeechRate() - 100);
        Locale locale = Locale.getDefault();
        this.lang = locale.getLanguage() + "-" + locale.getCountry();
        this.id = CommonTool.getMD5String(request.getCharSequenceText() + "" + System.currentTimeMillis());
        this.useDict = useDict;
        handleContent();

    }


    /**
     * 处理文本
     */
    private void handleContent() {
        CommonTool.Trim(content);
        CommonTool.replace(content, "&", "&amp;");
        CommonTool.replace(content, "\"", "&quot;");
        CommonTool.replace(content, "'", "&apos;");
        CommonTool.replace(content, ">", "&lt;");
        CommonTool.replace(content, "<", "&gt;");
        content = new StringBuilder(CommonTool.p4.matcher(content).replaceAll("<phoneme alphabet='sapi' ph='chong 2'>重</phoneme>"));
        if (useDict) {
            List<TtsDict> dictList = TtsDictManger.getInstance().getDict();
            for (TtsDict dict : dictList) {
                CommonTool.replace(content, dict.getWorld(), dict.getXML());
            }
        }

    }


    @NonNull
    @Override
    public String toString() {
        String rateString = rate >= 0 ? "+" + rate + "%" : rate + "%";
        String pitchString = pitch >= 0 ? "+" + pitch + "Hz" : pitch + "Hz";
        return "X-RequestId:" + id + "\r\n" +
                "Content-Type:application/ssml+xml\r\n" +
                "X-Timestamp:" + time + "Z\r\n" +

                "Path:ssml\r\n\r\n" +
                "<speak version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xmlns:mstts=\"https://www.w3.org/2001/mstts\" xml:lang=\"" + lang + "\">" +
                "<voice  name=\"" + name + "\">" +
                "<lang xml:lang=\""+lang+"\">"+
                "<prosody pitch=\"" + pitchString + "\" " +
                "rate =\"" + rateString + "\" " +
                "volume=\"" + style.getVolume() + "\">" +
                "<mstts:express-as  style=\"" + style.value + "\" styledegree=\"" + style.getStyleDegree() + "\" >" + content.toString() + "</mstts:express-as>" +
                "</prosody></lang></voice></speak>";
    }
}
