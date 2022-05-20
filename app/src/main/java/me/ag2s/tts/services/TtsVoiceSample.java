package me.ag2s.tts.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.MissingResourceException;

import me.ag2s.tts.R;

public final class TtsVoiceSample {

    public static String getByLocate(@NonNull Context context, @NonNull Locale locale) throws MissingResourceException {
        String language = locale.getISO3Language();
        if (language.equals(new Locale("en").getISO3Language())) {
            return context.getString(R.string.tts_sample_en);
        } else if (language.equals(new Locale("zh").getISO3Language())) {
            return context.getString(R.string.tts_sample_zh);
        } else if (language.equals(new Locale("ru").getISO3Language())) {
            return context.getString(R.string.tts_sample_ru);
        } else if (language.equals(new Locale("tr").getISO3Language())) {
            return context.getString(R.string.tts_sample_tr);
        } else if (language.equals(new Locale("de").getISO3Language())) {
            return context.getString(R.string.tts_sample_de);
        } else if (language.equals(new Locale("it").getISO3Language())) {
            return context.getString(R.string.tts_sample_it);
        } else if (language.equals(new Locale("ja").getISO3Language())) {
            return context.getString(R.string.tts_sample_ja);
        } else if (language.equals(new Locale("fr").getISO3Language())) {
            return context.getString(R.string.tts_sample_fr);
        } else if (language.equals(new Locale("nl").getISO3Language())) {
            return context.getString(R.string.tts_sample_nl);
        } else if (language.equals(new Locale("pt").getISO3Language())) {
            return context.getString(R.string.tts_sample_pt);
        } else if (language.equals(new Locale("es").getISO3Language())) {
            return context.getString(R.string.tts_sample_es);
        } else if (language.equals(new Locale("ko").getISO3Language())) {
            return context.getString(R.string.tts_sample_ko);
        } else if (language.equals(new Locale("ar").getISO3Language())) {
            return context.getString(R.string.tts_sample_ar);
        } else {
            Log.d("TTS", locale.toString());
            return context.getString(R.string.tts_sample_default);
        }
    }
}
