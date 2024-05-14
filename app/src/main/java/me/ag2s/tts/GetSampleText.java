package me.ag2s.tts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

import me.ag2s.tts.services.TtsVoiceSample;


public class GetSampleText extends Activity {

    private static final String TAG = GetSampleText.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int result = TextToSpeech.LANG_AVAILABLE;
        Intent returnData = new Intent();

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        if (bundle == null) return;

        String language = bundle.getString("language");
        String country = bundle.getString("country");
        String variant = bundle.getString("variant");
        Log.d(TAG, language + "_" + country + "_" + variant);

        if (language == null || country == null) return;
        Locale locale = new Locale(language, country);
        returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, TtsVoiceSample.getByLocate(this, locale));
        setResult(result, returnData);
        finish();
    }
}