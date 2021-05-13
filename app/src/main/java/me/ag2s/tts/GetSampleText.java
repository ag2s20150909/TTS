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
        String language = i.getExtras().getString("language");
        String country = i.getExtras().getString("country");
        String variant = i.getExtras().getString("variant");
        Log.d(TAG, language + "_" + country + "_" + variant);
        Locale locale = new Locale(language, country);
        returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, TtsVoiceSample.getByLocate(this, locale));
        setResult(result, returnData);
        finish();
    }
}