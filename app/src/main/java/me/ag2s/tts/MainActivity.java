package me.ag2s.tts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import me.ag2s.tts.adapters.TtsActorAdapter;
import me.ag2s.tts.adapters.TtsStyleAdapter;
import me.ag2s.tts.services.TTSService;
import me.ag2s.tts.services.TtsActorManger;
import me.ag2s.tts.services.TtsStyle;
import me.ag2s.tts.services.TtsStyleManger;
import me.ag2s.tts.services.TtsVoiceSample;
import me.ag2s.tts.utils.HttpTool;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CheckVoiceData";
    private static final AtomicInteger mNextRequestId = new AtomicInteger(0);
    private Switch aSwitch;
    private Switch bSwitch;
    private RecyclerView rv_styles;
    private SeekBar seekBar;
    TextView tv_styleDegree;
    public SharedPreferences sharedPreferences;

    private RecyclerView gv;
    private Button btn_set_tts;
    private Button btn_IgnoringBatteryOptimizations;
    TextToSpeech textToSpeech;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getApplicationContext().getSharedPreferences("TTS", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        btn_set_tts = findViewById(R.id.btn_set_tts);
        btn_IgnoringBatteryOptimizations = findViewById(R.id.btn_kill_battery);
        btn_set_tts.setOnClickListener(this);
        btn_IgnoringBatteryOptimizations.setOnClickListener(this);
        gv = findViewById(R.id.gv);
        aSwitch = findViewById(R.id.switch_use_custom_language);
        bSwitch = findViewById(R.id.switch_use_auto_retry);
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

        boolean useAutoRetry = sharedPreferences.getBoolean(TTSService.USE_AUTO_RETRY, false);
        bSwitch.setChecked(useAutoRetry);
        bSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(TTSService.USE_AUTO_RETRY, isChecked);
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
        adapter.upgrade(TtsActorManger.getInstance().getActorsByLocale(Locale.getDefault()));//getActorsByLocale(Locale.getDefault()));
        GridLayoutManager gvm = new GridLayoutManager(this, 3);
        gv.setLayoutManager(gvm);

        adapter.setSelect(gv, sharedPreferences.getInt(TTSService.CUSTOM_VOICE_INDEX, 0));
        adapter.setItemClickListener((position, item) -> {
            boolean origin = sharedPreferences.getBoolean(TTSService.USE_CUSTOM_VOICE, false);

            if (origin) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(TTSService.CUSTOM_VOICE, item.getShortName());
                editor.putInt(TTSService.CUSTOM_VOICE_INDEX, position);
                adapter.setSelect(gv, position);
                editor.apply();
            }

            Locale locale = item.getLocale();

            if (!textToSpeech.isSpeaking()) {
                Bundle bundle = new Bundle();
                bundle.putString("voiceName", item.getShortName());
                bundle.putString("language", locale.getISO3Language());
                bundle.putString("country", locale.getISO3Country());
                bundle.putString("variant", item.getGender() ? "Female" : "Male");
                textToSpeech.speak(TtsVoiceSample.getByLocate(this, locale), TextToSpeech.QUEUE_FLUSH, bundle, MainActivity.class.getName() + mNextRequestId.getAndIncrement());
            }

        });
        if (sharedPreferences.getBoolean(TTSService.USE_AUTO_UPDATE,true)){
            checkUpdate();
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean i = powerManager.isIgnoringBatteryOptimizations(this.getPackageName());
            if (i) {
                btn_IgnoringBatteryOptimizations.setVisibility(View.GONE);
            } else {
                btn_IgnoringBatteryOptimizations.setVisibility(View.VISIBLE);
            }
        }


    }

    public void checkUpdate() {
        new Thread(() -> {

            try {
                JSONObject json = new JSONObject(HttpTool.httpGet("https://ghproxy.com/https://raw.githubusercontent.com/ag2s20150909/TTS/master/release/output-metadata.json")).optJSONArray("elements").optJSONObject(0);
                String fileName = json.optString("outputFile");
                BigDecimal versionName = new BigDecimal(json.optString("versionName").split("_")[1].trim());
                PackageManager pm = MainActivity.this.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(MainActivity.this.getPackageName(), 0);
                BigDecimal appVersionName = new BigDecimal(pi.versionName.split("_")[1].trim());
                Log.d(TAG, appVersionName.toString() + "\n" + versionName.toString());
                if (appVersionName.compareTo(versionName) < 0) {
                    Log.d(TAG, "需要更新。");
                    downLoadAndInstall(fileName);
                } else {
                    //downLoadAndInstall(fileName);
                    Log.d(TAG, "不需要更新。");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void downLoadAndInstall(String appName) {
        try {
            String url = "https://ghproxy.com/https://raw.githubusercontent.com/ag2s20150909/TTS/master/release/" + appName;

            runOnUiThread(() -> new AlertDialog.Builder(MainActivity.this)
                    .setTitle("有新版本")
                    .setMessage("发现新版本:\n" + appName+"\n如需更新，点击确定，将跳转到浏览器下载。如不想更新，点击取消，将不再检查更新，直到你清除应用数据。")
                    .setPositiveButton("确定", (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(url),
                                "application/vnd.android.package-archive");
                        startActivity(intent);
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(TTSService.USE_AUTO_UPDATE,false);
                        editor.apply();
                    })
                    .create().show());
        } catch (Exception ignored) {
        }
    }

    public void setTTS() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        //startActivity(intent);
        //Intent intent = new Intent();
        intent.setAction("com.android.settings.TTS_SETTINGS");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    @SuppressLint("BatteryLife")
    public void killBATTERY() {
        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm.isIgnoringBatteryOptimizations(packageName))
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            else {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

    }

    public void test(View view) {
        Intent i = new Intent(this, DownloadVoiceData.class);
        startActivity(i);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_tts:
                setTTS();
                break;
            case R.id.btn_kill_battery:
                killBATTERY();
                break;
        }
    }
}