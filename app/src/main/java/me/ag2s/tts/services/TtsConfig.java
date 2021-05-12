package me.ag2s.tts.services;


import android.util.Log;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class TtsConfig {
    private final boolean sentenceBoundaryEnabled;
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean wordBoundaryEnabled;
    private final String audioFormat;


    private TtsConfig(Builder builder) {
        this.sentenceBoundaryEnabled = builder.sentenceBoundaryEnabled;
        this.wordBoundaryEnabled = builder.wordBoundaryEnabled;
        this.audioFormat = builder.audioFormat;

    }

    @NotNull
    @Override
    public String toString() {
        String msg = "{\"context\":{\"synthesis\":{\"audio\":{\"metadataoptions\":{\"sentenceBoundaryEnabled\":\"%s\",\"wordBoundaryEnabled\":\"%s\"},\"outputFormat\":\"%s\"}}}}";
        msg = String.format(msg, sentenceBoundaryEnabled ? "true" : "false", "true", this.audioFormat);
        Log.d(TTSService.class.getSimpleName(), msg);
        return msg;
    }

    public static class Builder {
        private final String MP3 = "audio-24khz-48kbitrate-mono-mp3";
        private final String PCM16 = "raw-16khz-16bit-mono-pcm";
        public String audioFormat = PCM16;
        private boolean sentenceBoundaryEnabled = false;
        private boolean wordBoundaryEnabled = true;

        public TtsConfig build() {
            return new TtsConfig(this);
        }

        public Builder() {

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
