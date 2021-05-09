package me.ag2s.tts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;


public class GetSampleText extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int result = TextToSpeech.LANG_AVAILABLE;
        Intent returnData = new Intent();

        Intent i = getIntent();
        String language = i.getExtras().getString("language");
        String country = i.getExtras().getString("country");
        String variant = i.getExtras().getString("variant");

        if (language != null) {
            if (language.equals("en")) {
                returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_en));
            } else if (language.equals("zh")) {
                returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_zh));
            } else if (language.equals("ru")) {
                returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_ru));
            } else if (language.equals("tr")) {
                returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_tr));
            } else if (language.equals("de")) {
                returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_de));
            } else if (language.equals("it")) {
                returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_it));
            } else if (language.equals("ja")) {
                returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_ja));
            } else if (language.equals("fr")) {
                returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_fr));
            } else if (language.equals("nl")) {
                returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_nl));
            } else if (language.equals("pt")) {
                returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_pt));
            } else if (language.equals("es")) {
                returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_es));
            } else if (language.equals("ko")) {
                returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_ko));
            } else {
                returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_default));
            }
        } else {
            result = TextToSpeech.LANG_NOT_SUPPORTED;
            //returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_default));
        }

        setResult(result, returnData);
        finish();
    }
}