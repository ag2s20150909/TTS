package me.ag2s.tts.services;

/**
 * TTS的说话风格
 */
public class TtsStyle {
    /**
     * 展示的名称
     */
    public String name;
    /**
     * 风格的值
     */
    public String value;
    /**
     * 风格的说明
     */
    public String extra;

    public TtsStyle(String name,String value,String extra){
        this.name=name;
        this.value=value;
        this.extra=extra;
    }


}
