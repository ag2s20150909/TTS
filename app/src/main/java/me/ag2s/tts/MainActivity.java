package me.ag2s.tts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import me.ag2s.tts.adapters.TtsActorAdapter;
import me.ag2s.tts.adapters.TtsStyleAdapter;
import me.ag2s.tts.services.TTSService;
import me.ag2s.tts.services.TtsActorManger;
import me.ag2s.tts.services.TtsStyle;
import me.ag2s.tts.services.TtsStyleManger;


public class MainActivity extends Activity {
    private static final String TAG = "CheckVoiceData";
    private Switch aSwitch;
    private RecyclerView rv_styles;
    SeekBar seekBar;
    TextView tv_styleDegree;
    public SharedPreferences sharedPreferences;

    private RecyclerView gv;
    TextToSpeech textToSpeech;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getApplicationContext().getSharedPreferences("TTS", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        gv = findViewById(R.id.gv);
        aSwitch = findViewById(R.id.switch_use_custom_language);
        rv_styles = findViewById(R.id.rv_voice_styles);
        seekBar = findViewById(R.id.tts_style_degree);
        tv_styleDegree = findViewById(R.id.tts_style_degree_value);
        int styleIndex = sharedPreferences.getInt(TTSService.VOICE_STYLE_INDEX, 0);
        int styleDegree = sharedPreferences.getInt(TTSService.VOICE_STYLE_DEGREE, 100);
        tv_styleDegree.setText(styleDegree + "");
        seekBar.setProgress(styleDegree);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_styleDegree.setText(progress + "");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(TTSService.VOICE_STYLE_DEGREE, progress);
                editor.apply();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        List<TtsStyle> styles = TtsStyleManger.getInstance().getStyles();
        TtsStyleAdapter rvadapter = new TtsStyleAdapter(styles);
        rvadapter.setSelect(styleIndex);
        rv_styles.setAdapter(rvadapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rv_styles.setLayoutManager(linearLayoutManager);
        linearLayoutManager.scrollToPositionWithOffset(styleIndex, 0);
        rvadapter.setItemClickListener((position, item) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TTSService.VOICE_STYLE, item.value);
            editor.putInt(TTSService.VOICE_STYLE_INDEX, position);
            editor.apply();
        });

        boolean isFChecked = sharedPreferences.getBoolean(TTSService.USE_CUSTOM_VOICE, false);
        aSwitch.setChecked(isFChecked);
        //gv.setVisibility(isFChecked ? View.VISIBLE : View.INVISIBLE);
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(TTSService.USE_CUSTOM_VOICE, isChecked);
            //gv.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            editor.apply();
        });


        textToSpeech = new TextToSpeech(MainActivity.this, status -> {
            // TODO Auto-generated method stub
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.CHINA);
                if (result != TextToSpeech.LANG_MISSING_DATA
                        && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    if (!textToSpeech.isSpeaking()) {
                        textToSpeech.speak("初始化成功。", TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }
        }, this.getPackageName());

        TtsActorAdapter adapter = new TtsActorAdapter(this);
        gv.setAdapter(adapter);
        adapter.upgrade(TtsActorManger.getInstance().getActors());//getActorsByLocale(Locale.getDefault()));
        GridLayoutManager gvm = new GridLayoutManager(this, 3);
        gv.setLayoutManager(gvm);
        adapter.setItemClickListener((position, item) -> {
            boolean origin = sharedPreferences.getBoolean(TTSService.USE_CUSTOM_VOICE, false);

            if (origin) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(TTSService.CUSTOM_VOICE, item.getShortName());
                editor.apply();
            }

            Locale locale = item.getLocale();
            Toast.makeText(this, locale.getLanguage() + "" + locale.getCountry() + "" + locale.getVariant(), Toast.LENGTH_LONG).show();
            if (!textToSpeech.isSpeaking()) {
                Bundle bundle = new Bundle();
                bundle.putString("voiceName", item.getShortName());
                bundle.putString("language", locale.getISO3Language());
                bundle.putString("country", locale.getISO3Country());
                bundle.putString("variant", item.getGender() ? "Female" : "Male");
                textToSpeech.speak(getResources().getString(R.string.tts_sample_zh), TextToSpeech.QUEUE_FLUSH, bundle, null);
            }

        });


    }


    public void setTTS(View view) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void killBATTERY(View view) {
        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(packageName))
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        else {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
        }
        startActivity(intent);
    }

    public void test(View view) {
        Intent i = new Intent(this, DownloadVoiceData.class);
        startActivity(i);
    }
}