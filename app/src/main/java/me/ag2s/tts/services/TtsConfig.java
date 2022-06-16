package me.ag2s.tts.services;


import android.util.Log;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class TtsConfig {
    private final boolean sentenceBoundaryEnabled;
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean wordBoundaryEnabled;
    //private final String audioFormat;
    private final int index;


    private TtsConfig(Builder builder) {
        this.sentenceBoundaryEnabled = builder.sentenceBoundaryEnabled;
        this.wordBoundaryEnabled = builder.wordBoundaryEnabled;
        //this.audioFormat = builder.audioFormat;
        this.index = builder.index;


    }

    public TtsOutputFormat getFormat() {
        return TtsFormatManger.getInstance().getFormat(index);
    }


    @NotNull
    @Override
    public String toString() {
        //X-Timestamp:Thu Jun 16 2022 19:13:55 GMT+0800 (中国标准时间)
        //Content-Type:application/json; charset=utf-8
        //Path:speech.config
        //
        //{"context":{"synthesis":{"audio":{"metadataoptions":{"sentenceBoundaryEnabled":"false","wordBoundaryEnabled":"true"},"outputFormat":"webm-24khz-16bit-mono-opus"}}}}

        String msg = "{\"context\":{\"synthesis\":{\"audio\":{\"metadataoptions\":{\"sentenceBoundaryEnabled\":\"%s\",\"wordBoundaryEnabled\":\"%s\"},\"outputFormat\":\"%s\"}}}}";
        msg = String.format(msg, sentenceBoundaryEnabled ? "true" : "false", "true", TtsFormatManger.getInstance().getFormat(index).value);
        Log.d(TTSService.class.getSimpleName(), msg);
        return msg;
    }


    public static class Builder {

        private boolean sentenceBoundaryEnabled = false;
        private boolean wordBoundaryEnabled = true;
        private final int index;

        public TtsConfig build() {
            return new TtsConfig(this);
        }

        public Builder(int index) {
            this.index = index;
        }

        public Builder sentenceBoundaryEnabled(boolean sentenceBoundaryEnabled) {
            this.sentenceBoundaryEnabled = sentenceBoundaryEnabled;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder wordBoundaryEnabled(boolean wordBoundaryEnabled) {
            this.wordBoundaryEnabled = wordBoundaryEnabled;
            return this;
        }

    }

}
