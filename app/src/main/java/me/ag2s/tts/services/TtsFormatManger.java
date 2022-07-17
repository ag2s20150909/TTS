package me.ag2s.tts.services;

import android.media.AudioFormat;

import java.util.ArrayList;
import java.util.List;

public class TtsFormatManger {

    //单例
    private static volatile TtsFormatManger instance;

    public static TtsFormatManger getInstance() {
        if (instance == null) {
            synchronized (TtsFormatManger.class) {
                if (instance == null) {
                    instance = new TtsFormatManger();
                }
            }

        }
        return instance;
    }

    private final List<TtsOutputFormat> formats;

    public TtsFormatManger() {
        formats = new ArrayList<>();
        this.formats.add(new TtsOutputFormat(TtsOutputFormat.TAG + "audio-24khz-48kbitrate-mono-mp3", 24000, AudioFormat.ENCODING_PCM_16BIT, true));
        this.formats.add(new TtsOutputFormat(TtsOutputFormat.TAG + "audio-24khz-96kbitrate-mono-mp3", 24000, AudioFormat.ENCODING_PCM_16BIT, true));
        this.formats.add(new TtsOutputFormat(TtsOutputFormat.TAG + "webm-24khz-16bit-mono-opus", 24000 * 2, AudioFormat.ENCODING_PCM_16BIT, true));

        this.formats.add(new TtsOutputFormat("raw-16khz-16bit-mono-pcm", 16000, AudioFormat.ENCODING_PCM_16BIT));
        this.formats.add(new TtsOutputFormat("raw-24khz-16bit-mono-pcm", 24000, AudioFormat.ENCODING_PCM_16BIT));
        this.formats.add(new TtsOutputFormat("raw-48khz-16bit-mono-pcm", 48000, AudioFormat.ENCODING_PCM_16BIT));
        this.formats.add(new TtsOutputFormat("raw-8khz-16bit-mono-pcm", 8000, AudioFormat.ENCODING_PCM_16BIT));
        this.formats.add(new TtsOutputFormat("raw-8khz-8bit-mono-mulaw", 8000, AudioFormat.ENCODING_PCM_8BIT));
        this.formats.add(new TtsOutputFormat("raw-8khz-8bit-mono-alaw", 8000, AudioFormat.ENCODING_PCM_8BIT));

        //audio/SILK; samplerate=24000
        //this.formats.add(new TtsOutputFormat("raw-24khz-16bit-mono-truesilk", 24000, AudioFormat.ENCODING_PCM_16BIT));//audio/SILK


        //this.formats.add(new TtsOutputFormat("audio-16khz-16kbps-mono-siren", 16000, AudioFormat.ENCODING_PCM_16BIT, true));


        this.formats.add(new TtsOutputFormat("audio-16khz-32kbitrate-mono-mp3", 16000, AudioFormat.ENCODING_PCM_16BIT, true));
        this.formats.add(new TtsOutputFormat("audio-16khz-64kbitrate-mono-mp3", 16000, AudioFormat.ENCODING_PCM_16BIT, true));
        this.formats.add(new TtsOutputFormat("audio-16khz-128kbitrate-mono-mp3", 16000, AudioFormat.ENCODING_PCM_16BIT, true));


        this.formats.add(new TtsOutputFormat("audio-24khz-160kbitrate-mono-mp3", 24000, AudioFormat.ENCODING_PCM_16BIT, true));

        this.formats.add(new TtsOutputFormat("audio-48khz-96kbitrate-mono-mp3", 48000, AudioFormat.ENCODING_PCM_16BIT, true));
        this.formats.add(new TtsOutputFormat("audio-48khz-192kbitrate-mono-mp3", 48000, AudioFormat.ENCODING_PCM_16BIT, true));


        this.formats.add(new TtsOutputFormat("riff-8khz-8bit-mono-alaw", 8000, AudioFormat.ENCODING_PCM_8BIT));
        this.formats.add(new TtsOutputFormat("riff-8khz-8bit-mono-mulaw", 8000, AudioFormat.ENCODING_PCM_8BIT));
        this.formats.add(new TtsOutputFormat("riff-8khz-16bit-mono-pcm", 8000, AudioFormat.ENCODING_PCM_16BIT));

        this.formats.add(new TtsOutputFormat("riff-16khz-16bit-mono-pcm", 16000, AudioFormat.ENCODING_PCM_16BIT));
        this.formats.add(new TtsOutputFormat("riff-24khz-16bit-mono-pcm", 24000, AudioFormat.ENCODING_PCM_16BIT));
        this.formats.add(new TtsOutputFormat("riff-48khz-16bit-mono-pcm", 48000, AudioFormat.ENCODING_PCM_16BIT));


        this.formats.add(new TtsOutputFormat("ogg-16khz-16bit-mono-opus", 16000 * 3, AudioFormat.ENCODING_PCM_16BIT, true));
        this.formats.add(new TtsOutputFormat("ogg-24khz-16bit-mono-opus", 24000 * 2, AudioFormat.ENCODING_PCM_16BIT, true));
        this.formats.add(new TtsOutputFormat("ogg-48khz-16bit-mono-opus", 48000, AudioFormat.ENCODING_PCM_16BIT, true));

        this.formats.add(new TtsOutputFormat("webm-16khz-16bit-mono-opus", 16000 * 3, AudioFormat.ENCODING_PCM_16BIT, true));

        //this.formats.add(new TtsOutputFormat("webm-48khz-16bit-mono-opus",48000,AudioFormat.ENCODING_PCM_16BIT,true));


    }

    public List<TtsOutputFormat> getFormats() {
        return formats;
    }

    public TtsOutputFormat getFormat(int index) {
        if (index > formats.size() - 1 || index < 0) {
            return formats.get(0);
        }
        return formats.get(index);
    }
}
