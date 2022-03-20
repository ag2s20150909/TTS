package me.ag2s.tts.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

/**
 * TTS的说话风格
 */
public class TtsStyle {

    public static final short DEFAULT_DEGREE = 100;
    /**
     * 展示的名称
     */
    @NonNull
    public final String name;
    /**
     * 风格的值
     */
    @NonNull
    public final String value;
    /**
     * 风格的强度(0.00-2.00)
     */
    private short styleDegree= DEFAULT_DEGREE;
    /**
     * 音量（1-100）
     */
    public byte volume= 100;
    /**
     * 风格的说明
     */
    @Nullable
    public final String extra;

    public TtsStyle(@NonNull String name, @NonNull String value,@Nullable String extra) {
        this.name = name;
        this.value = value;
        this.extra = extra;
    }

    public byte getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = (byte) volume;
    }

    public void setStyleDegree(int styleDegree) {
        this.styleDegree= (short) styleDegree;
    }
    public String getStyleDegree() {
        return String.format(Locale.US, "%01d.%02d", styleDegree / DEFAULT_DEGREE, styleDegree % DEFAULT_DEGREE);
    }
}
