package me.ag2s.tts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.HashMap;

import me.ag2s.tts.services.TTSService;

public class CheckVoiceData extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int result = TextToSpeech.Engine.CHECK_VOICE_DATA_PASS;

        ArrayList<String> available = new ArrayList<>();
        ArrayList<String> unavailable = new ArrayList<>();

        HashMap<String, Boolean> languageCountry = new HashMap<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ArrayList<String> langCountryVars = bundle.getStringArrayList(
                    TextToSpeech.Engine.EXTRA_CHECK_VOICE_DATA_FOR);
            if (langCountryVars != null) {
                for (int i = 0; i < langCountryVars.size(); i++) {
                    if (langCountryVars.get(i).length() > 0) {
                        languageCountry.put(langCountryVars.get(i), true);
                    }
                }
            }
        }

        // Check for files
        for (int i = 0; i < TTSService.supportedLanguages.length; i++) {
            if ((languageCountry.size() < 1) ||
                    (languageCountry.containsKey(TTSService.supportedLanguages[i]))) {
                available.add(TTSService.supportedLanguages[i]);
            }
        }

        if (languageCountry.size() > 0) {
            result = TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL;
        }

        Intent returnData = new Intent();
        returnData.putStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES, available);
        returnData.putStringArrayListExtra(TextToSpeech.Engine.EXTRA_UNAVAILABLE_VOICES, unavailable);
        setResult(result, returnData);
        finish();
    }
}