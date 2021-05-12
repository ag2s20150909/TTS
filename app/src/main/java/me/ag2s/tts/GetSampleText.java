package me.ag2s.tts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;


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

        if (language != null) {
            switch (language) {
                case "en":
                    returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_en));
                    break;
                case "zh":
                    returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_zh));
                    break;
                case "ru":
                    returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_ru));
                    break;
                case "tr":
                    returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_tr));
                    break;
                case "de":
                    returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_de));
                    break;
                case "it":
                    returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_it));
                    break;
                case "ja":
                    returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_ja));
                    break;
                case "fr":
                    returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_fr));
                    break;
                case "nl":
                    returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_nl));
                    break;
                case "pt":
                    returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_pt));
                    break;
                case "es":
                    returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_es));
                    break;
                case "ko":
                    returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_ko));
                    break;
                default:
                    returnData.putExtra(TextToSpeech.Engine.EXTRA_SAMPLE_TEXT, getString(R.string.tts_sample_default));
                    break;
            }
        }


        setResult(result, returnData);
        finish();
    }
}