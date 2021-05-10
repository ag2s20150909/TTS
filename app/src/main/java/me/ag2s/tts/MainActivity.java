package me.ag2s.tts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Locale;

import me.ag2s.tts.adapters.TtsActorAdapter;
import me.ag2s.tts.services.TTSService;
import me.ag2s.tts.services.TtsActor;
import me.ag2s.tts.services.TtsActorManger;


public class MainActivity extends Activity {
    private static final String TAG = "CheckVoiceData";
    private Switch aSwitch;
    public SharedPreferences sharedPreferences;

    private static final String[] SUPPORTED_LANGUAGES = {"eng-GBR", "eng-USA"};
    private GridView gv;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getApplicationContext().getSharedPreferences("TTS", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        gv = findViewById(R.id.gv);
        aSwitch = findViewById(R.id.switch_use_custom_language);
        boolean isFChecked = sharedPreferences.getBoolean(TTSService.USE_CUSTOM_LANGUAGE, false);
        aSwitch.setChecked(isFChecked);
        gv.setVisibility(isFChecked ? View.VISIBLE : View.INVISIBLE);
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(TTSService.USE_CUSTOM_LANGUAGE, isChecked);
            gv.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            editor.apply();
        });
        textToSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    //int result = SetLanguage(Locale.CHINA.getLanguage());
                    //如果打印为-2，说明不支持这种语言
                    Toast.makeText(MainActivity.this, "-------------result = " + result, Toast.LENGTH_LONG).show();
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    } else {
                        textToSpeech.speak("初始化成功。", TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }
        }, this.getPackageName());

        TtsActorAdapter adapter = new TtsActorAdapter(this);
        gv.setAdapter(adapter);
        adapter.upgrade(TtsActorManger.getInstance().getActors());
        gv.setOnItemClickListener((parent, view, position, id) -> {

            TtsActor actor = (TtsActor) adapter.getItem(position);
            boolean origin = sharedPreferences.getBoolean(TTSService.USE_CUSTOM_LANGUAGE, false);
            //TTSService.setActor(actor);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(TTSService.USE_CUSTOM_LANGUAGE, true);
            editor.putString(TTSService.CUSTOM_LANGUAGE, actor.getShortName());
            editor.apply();

            textToSpeech.speak(getResources().getString(R.string.tts_sample_zh), TextToSpeech.QUEUE_FLUSH, null, null);
            editor.putBoolean(TTSService.USE_CUSTOM_LANGUAGE, origin);
            editor.apply();

        });


    }

    private int SetLanguage(String lang) {
        int result = 0;
        if (lang.equals("CANADA")) {
            result = textToSpeech.setLanguage(Locale.CANADA);
        } else if (lang.equals("CANADA_FRENCH")) {
            result = textToSpeech.setLanguage(Locale.CANADA_FRENCH);
        } else if (lang.equals("CHINA")) {
            result = textToSpeech.setLanguage(Locale.CHINA);
        } else if (lang.equals("CHINESE")) {
            result = textToSpeech.setLanguage(Locale.CHINESE);
        } else if (lang.equals("ENGLISH")) {
            result = textToSpeech.setLanguage(Locale.ENGLISH);
        } else if (lang.equals("FRANCE")) {
            result = textToSpeech.setLanguage(Locale.FRANCE);
        } else if (lang.equals("FRENCH")) {
            result = textToSpeech.setLanguage(Locale.FRENCH);
        } else if (lang.equals("GERMAN")) {
            result = textToSpeech.setLanguage(Locale.GERMAN);
        } else if (lang.equals("GERMANY")) {
            result = textToSpeech.setLanguage(Locale.GERMANY);
        } else if (lang.equals("ITALIAN")) {
            result = textToSpeech.setLanguage(Locale.ITALIAN);
        } else if (lang.equals("ITALY")) {
            result = textToSpeech.setLanguage(Locale.ITALY);
        } else if (lang.equals("JAPAN")) {
            result = textToSpeech.setLanguage(Locale.JAPAN);
        } else if (lang.equals("JAPANESE")) {
            result = textToSpeech.setLanguage(Locale.JAPANESE);
        } else if (lang.equals("KOREA")) {
            result = textToSpeech.setLanguage(Locale.KOREA);
        } else if (lang.equals("KOREAN")) {
            result = textToSpeech.setLanguage(Locale.KOREAN);
        } else if (lang.equals("PRC")) {
            result = textToSpeech.setLanguage(Locale.PRC);
        } else if (lang.equals("ROOT")) {
            result = textToSpeech.setLanguage(Locale.ROOT);
        } else if (lang.equals("SIMPLIFIED_CHINESE")) {
            result = textToSpeech.setLanguage(Locale.SIMPLIFIED_CHINESE);
        } else if (lang.equals("TAIWAN")) {
            result = textToSpeech.setLanguage(Locale.TAIWAN);
        } else if (lang.equals("TRADITIONAL_CHINESE")) {
            result = textToSpeech.setLanguage(Locale.TRADITIONAL_CHINESE);
        } else if (lang.equals("UK")) {
            result = textToSpeech.setLanguage(Locale.UK);
        } else if (lang.equals("US")) {
            result = textToSpeech.setLanguage(Locale.US);
        }
        return result;
    }


    public void setTTS(View view) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    public void killBATTERY(View view) {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        //data为应用包名
        i.setData(Uri.parse("package:" + this.getPackageName()));
        startActivity(i);
    }
}