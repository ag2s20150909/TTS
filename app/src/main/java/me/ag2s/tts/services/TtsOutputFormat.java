package me.ag2s.tts.services;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class TtsOutputFormat {
    public static final String TAG = "\uD83D\uDC96";
    @NonNull
    public final String name;
    @NonNull
    public final String value;
    public final int HZ;
    public final byte BitRate;
    /**
     * 是否需要解码
     */
    public boolean needDecode = false;

    public TtsOutputFormat(@NonNull String name, int hz, int bitRate) {
        this.name = name;
        this.value = name;
        this.HZ = hz;
        this.BitRate = (byte) bitRate;
    }

    public TtsOutputFormat(@NonNull String name, int hz, int bitRate, boolean needDecode) {
        this.name = name;
        if (name.contains(TAG)) {
            this.value = name.substring(TAG.length());
        } else {
            this.value = name;
        }


        this.HZ = hz;
        this.BitRate = (byte) bitRate;
        this.needDecode = needDecode;
    }

    @SuppressWarnings("unused")
    public void setNeedDecode(boolean needDecode) {
        this.needDecode = needDecode;
    }

    @NotNull
    @Override
    public String toString() {
        return "TtsOutputFormat{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", HZ=" + HZ +
                ", BitRate=" + BitRate +
                ", needDecode=" + needDecode +
                '}';
    }
}
