package me.ag2s.tts;

import static me.ag2s.tts.services.Constants.CUSTOM_VOICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import me.ag2s.tts.adapters.TtsActorAdapter;
import me.ag2s.tts.adapters.TtsStyleAdapter;
import me.ag2s.tts.services.Constants;
import me.ag2s.tts.services.TtsActorManger;
import me.ag2s.tts.services.TtsDictManger;
import me.ag2s.tts.services.TtsFormatManger;
import me.ag2s.tts.services.TtsOutputFormat;
import me.ag2s.tts.services.TtsStyle;
import me.ag2s.tts.services.TtsStyleManger;
import me.ag2s.tts.services.TtsVoiceSample;
import me.ag2s.tts.utils.ApkInstall;
import me.ag2s.tts.utils.HttpTool;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CheckVoiceData";
    private static final AtomicInteger mNextRequestId = new AtomicInteger(0);
    TextView tv_styleDegree;
    SeekBar styleDegreeSeekBar;
    SeekBar volumeBar;

    private Button btn_IgnoringBatteryOptimizations;
    TextToSpeech textToSpeech;
    int styleDegree;
    int volumeValue;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sharedPreferences = getApplicationContext().getSharedPreferences("TTS", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        Button btn_set_tts = findViewById(R.id.btn_set_tts);
        btn_IgnoringBatteryOptimizations = findViewById(R.id.btn_kill_battery);
        btn_set_tts.setOnClickListener(this);
        btn_IgnoringBatteryOptimizations.setOnClickListener(this);
        RecyclerView gv = findViewById(R.id.gv);
        Switch aSwitch = findViewById(R.id.switch_use_custom_language);
        Switch swUseDict = findViewById(R.id.switch_use_dict);
        RecyclerView rv_styles = findViewById(R.id.rv_voice_styles);

        styleDegreeSeekBar = findViewById(R.id.tts_style_degree);
        volumeBar = findViewById(R.id.tts_voice_volume);

        Button styleDegreeAdd = findViewById(R.id.tts_style_degree_add);
        Button styleDegreeReduce = findViewById(R.id.tts_style_degree_reduce);
        styleDegreeAdd.setOnClickListener(this);
        styleDegreeReduce.setOnClickListener(this);

        tv_styleDegree = findViewById(R.id.tts_style_degree_value);
        int styleIndex = APP.getInt(Constants.VOICE_STYLE_INDEX, 0);//sharedPreferences.getInt(Constants.VOICE_STYLE_INDEX, 0);


        styleDegree = APP.getInt(Constants.VOICE_STYLE_DEGREE, 100);//sharedPreferences.getInt(Constants.VOICE_STYLE_DEGREE, 100);
        volumeValue = APP.getInt(Constants.VOICE_VOLUME, 100);//sharedPreferences.getInt(Constants.VOICE_VOLUME, 100);

        updateView();
        styleDegreeSeekBar.setProgress(styleDegree);
        volumeBar.setProgress(volumeValue);
        styleDegreeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                styleDegree = progress;
                updateView();


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volumeValue = progress;
                updateView();

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
        rvadapter.setItemClickListener((position, item) -> APP.putInt(Constants.VOICE_STYLE_INDEX, position));

        boolean isFChecked = APP.getBoolean(Constants.USE_CUSTOM_VOICE, true);//sharedPreferences.getBoolean(Constants.USE_CUSTOM_VOICE, true);
        aSwitch.setChecked(isFChecked);
        //gv.setVisibility(isFChecked ? View.VISIBLE : View.INVISIBLE);
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> APP.putBoolean(Constants.USE_CUSTOM_VOICE, isChecked));

        boolean useDict = APP.getBoolean(Constants.USE_DICT, false);
        swUseDict.setChecked(useDict);
        swUseDict.setOnCheckedChangeListener((buttonView, isChecked) -> APP.putBoolean(Constants.USE_DICT, isChecked));


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


        TtsActorAdapter adapter = new TtsActorAdapter(TtsActorManger.getInstance().getActors());
        gv.setAdapter(adapter);
        gv.setLayoutManager(new GridLayoutManager(this, 3));
        adapter.setSelect(gv, APP.getInt(Constants.CUSTOM_VOICE_INDEX, 0));
        adapter.setItemClickListener((position, item) -> {
            boolean origin = APP.getBoolean(Constants.USE_CUSTOM_VOICE, true);

            if (origin) {

                APP.putString(CUSTOM_VOICE, item.getShortName());
                APP.putInt(Constants.CUSTOM_VOICE_INDEX, position);

            }

            Locale locale = item.getLocale();

            if (!textToSpeech.isSpeaking()) {
                Bundle bundle = new Bundle();
                bundle.putString("voiceName", item.getShortName());
                bundle.putString("language", locale.getISO3Language());
                bundle.putString("country", locale.getISO3Country());
                bundle.putString("variant", item.getGender() ? "Female" : "Male");
                bundle.putString("utteranceId", "Sample");
                textToSpeech.speak(TtsVoiceSample.getByLocate(this, locale), TextToSpeech.QUEUE_FLUSH, bundle, MainActivity.class.getName() + mNextRequestId.getAndIncrement());
            } else {
                Toast.makeText(MainActivity.this, "" + item.getShortName(), Toast.LENGTH_SHORT).show();
            }

        });
        if (APP.getBoolean(Constants.USE_AUTO_UPDATE, true)) {
            checkUpdate();
        }


    }


    @SuppressLint("SetTextI18n")
    private void updateView() {
        APP.putInt(Constants.VOICE_STYLE_DEGREE, styleDegree);
        APP.putInt(Constants.VOICE_VOLUME,volumeValue);
        styleDegreeSeekBar.setProgress(styleDegree);


        tv_styleDegree.setText("强度:" + styleDegree/ TtsStyle.DEFAULT_DEGREE+"."+styleDegree% TtsStyle.DEFAULT_DEGREE + "音量:" + volumeValue);
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



    @Override
    protected void onStop() {
        super.onStop();

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST + 1, 0, R.string.check_update);
        menu.add(Menu.NONE, Menu.FIRST + 2, 0, R.string.battery_optimizations);
        menu.add(Menu.NONE, Menu.FIRST + 3, 0, R.string.update_dic);

        Menu aa = menu.addSubMenu(100, 100, 1, R.string.audio_format);


        List<TtsOutputFormat> formats = TtsFormatManger.getInstance().getFormats();
        for (int i = 0; i < formats.size(); i++) {
            aa.add(100, 1000 + i, 0, formats.get(i).name);
        }

        MenuItem menuItem = menu.findItem(100);
        menuItem.getSubMenu().setGroupCheckable(menuItem.getGroupId(), true, true);
        invalidateOptionsMenu();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(APP.getInt(Constants.AUDIO_FORMAT_INDEX, 0) + 1000).setChecked(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = APP.getInt(Constants.AUDIO_FORMAT_INDEX, 0);
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                checkUpdate();
                break;
            case Menu.FIRST + 2:
                killBATTERY();
                break;
            case Menu.FIRST + 3:
                TtsDictManger.getInstance().updateDict();
                break;
            default:
                if (item.getGroupId() == 100 && item.getItemId() >= 1000 && item.getItemId() < 1100) {
                    int index = item.getItemId() - 1000;
                    boolean b = index == i;
                    item.setChecked(b);
                    Toast.makeText(this, TtsFormatManger.getInstance().getFormat(index).value, Toast.LENGTH_LONG).show();
                    APP.putInt(Constants.AUDIO_FORMAT_INDEX, index);
                } else {
                    return super.onOptionsItemSelected(item);
                }

                break;
        }
        return true;
    }

    private void checkUpdate() {
        new Thread(() -> {

            try {
                String url = "https://fastly.jsdelivr.net/gh/ag2s20150909/TTS@release/output-metadata.json";
                JSONObject json = Objects.requireNonNull(new JSONObject(HttpTool.httpGet(url)).optJSONArray("elements")).optJSONObject(0);
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
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "不需要更新", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void downLoadAndInstall(String appName) {
        try {
            String url = "https://fastly.jsdelivr.net/gh/ag2s20150909/TTS@release/" + appName;

            runOnUiThread(() -> new AlertDialog.Builder(MainActivity.this)
                    .setTitle("有新版本")
                    .setMessage("发现新版本:\n" + appName + "\n如需更新，点击确定，将跳转到浏览器下载。如不想更新，点击取消，将不再自动检查更新，直到你清除应用数据。你可以到右上角菜单手动检查更新。")
                    .setPositiveButton("确定", (dialog, which) -> HttpTool.downLoadFile(url, getExternalCacheDir().getAbsolutePath() + "/" + appName, new HttpTool.DownloadCallBack() {
                        @Override
                        public void onSuccess(String path) {
                            new ApkInstall(getApplicationContext()).installAPK(path);
                        }

                        @Override
                        public void onError(String err) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }))
                    .setNegativeButton("取消", (dialog, which) -> APP.putBoolean(Constants.USE_AUTO_UPDATE, false))
                    .create().show());
        } catch (Exception ignored) {
        }
    }

//    private void QueryApk() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (!getPackageManager().canRequestPackageInstalls()) {
//                startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
//                        .setData(Uri.parse(String.format("package:%s", getPackageName()))), 1);
//            }
//        }
//
//        //Storage Permission
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        }
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//        }
//    }


    private void setTTS() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setAction("com.android.settings.TTS_SETTINGS");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    @SuppressLint("BatteryLife")
    private void killBATTERY() {
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
            case R.id.tts_style_degree_add:
                if (styleDegree < 200) {
                    styleDegree++;
                    updateView();
                }
                break;
            case R.id.tts_style_degree_reduce:
                if (styleDegree > 1) {
                    styleDegree--;
                    updateView();
                }
                break;
        }
    }


}