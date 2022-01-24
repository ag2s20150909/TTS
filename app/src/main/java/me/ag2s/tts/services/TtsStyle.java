package me.ag2s.tts.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * TTS的说话风格
 */
public class TtsStyle {

    public static final BigDecimal DEFAULT_DEGREE =new BigDecimal(BigInteger.valueOf(100),2);
    /**
     * 展示的名称
     */
    public final String name;
    /**
     * 风格的值
     */
    public final String value;
    /**
     * 风格的强度(0.00-200.00)
     */
    public BigDecimal styleDegree= DEFAULT_DEGREE;
    /**
     * 音量（1-100）
     */
    public byte volume= 100;
    /**
     * 风格的说明
     */
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

    public void setStyleDegree(BigDecimal styleDegree) {
        this.styleDegree=styleDegree;
    }
    public String getStyleDegree() {
        return styleDegree.divide(DEFAULT_DEGREE,2,BigDecimal.ROUND_HALF_UP).toString();
    }
}
