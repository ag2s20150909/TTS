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
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import me.ag2s.tts.adapters.TtsActorAdapter;
import me.ag2s.tts.adapters.TtsStyleAdapter;
import me.ag2s.tts.databinding.ActivityMainBinding;
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


public class MainActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "CheckVoiceData";
    private static final AtomicInteger mNextRequestId = new AtomicInteger(0);
    ActivityMainBinding binding;

    TextToSpeech textToSpeech;
    int styleDegree;
    int volumeValue;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.btnSetTts.setOnClickListener(this);
        binding.btnKillBattery.setOnClickListener(this);
        binding.ttsStyleDegreeAdd.setOnClickListener(this);
        binding.ttsStyleDegreeReduce.setOnClickListener(this);

        int styleIndex = APP.getInt(Constants.VOICE_STYLE_INDEX, 0);//sharedPreferences.getInt(Constants.VOICE_STYLE_INDEX, 0);


        styleDegree = APP.getInt(Constants.VOICE_STYLE_DEGREE, 100);//sharedPreferences.getInt(Constants.VOICE_STYLE_DEGREE, 100);
        volumeValue = APP.getInt(Constants.VOICE_VOLUME, 100);//sharedPreferences.getInt(Constants.VOICE_VOLUME, 100);

        updateView();
        binding.ttsStyleDegree.setProgress(styleDegree);
        binding.ttsVoiceVolume.setProgress(volumeValue);
        binding.ttsStyleDegree.setOnSeekBarChangeListener(this);
        binding.ttsVoiceVolume.setOnSeekBarChangeListener(this);

        TtsStyleAdapter ttsStyleAdapter = new TtsStyleAdapter(TtsStyleManger.getInstance().getStyles());
        ttsStyleAdapter.setSelect(styleIndex);
        binding.rvVoiceStyles.setAdapter(ttsStyleAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        binding.rvVoiceStyles.setLayoutManager(linearLayoutManager);
        linearLayoutManager.scrollToPositionWithOffset(styleIndex, 0);
        ttsStyleAdapter.setItemClickListener((position, item) -> APP.putInt(Constants.VOICE_STYLE_INDEX, position));

        boolean useCustomVoice = APP.getBoolean(Constants.USE_CUSTOM_VOICE, true);//sharedPreferences.getBoolean(Constants.USE_CUSTOM_VOICE, true);
        binding.switchUseCustomVoice.setChecked(useCustomVoice);
        binding.switchUseCustomVoice.setOnCheckedChangeListener((buttonView, isChecked) -> APP.putBoolean(Constants.USE_CUSTOM_VOICE, isChecked));

        boolean useSplitSentence = APP.getBoolean(Constants.SPLIT_SENTENCE, false);//sharedPreferences.getBoolean(Constants.USE_CUSTOM_VOICE, true);
        binding.switchUseSplitSentence.setChecked(useSplitSentence);
        binding.switchUseSplitSentence.setOnCheckedChangeListener((buttonView, isChecked) -> APP.putBoolean(Constants.SPLIT_SENTENCE, isChecked));


        boolean useDict = APP.getBoolean(Constants.USE_DICT, false);
        binding.switchUseDict.setChecked(useDict);
        binding.switchUseDict.setOnCheckedChangeListener((buttonView, isChecked) -> APP.putBoolean(Constants.USE_DICT, isChecked));


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


        TtsActorAdapter actorAdapter = new TtsActorAdapter(TtsActorManger.getInstance().getActors());
        binding.rvVoiceActors.setAdapter(actorAdapter);
        binding.rvVoiceActors.setVisibility(View.VISIBLE);
        binding.rvVoiceActors.setLayoutManager(new GridLayoutManager(this, 3));
        actorAdapter.setSelect(binding.rvVoiceActors, APP.getInt(Constants.CUSTOM_VOICE_INDEX, 0));
        actorAdapter.setItemClickListener((position, item) -> {
            boolean origin = APP.getBoolean(Constants.USE_CUSTOM_VOICE, true);

            if (origin) {

                APP.putString(CUSTOM_VOICE, item.getShortName());
                APP.putInt(Constants.CUSTOM_VOICE_INDEX, position);

            }

            Locale locale = item.getLocale();

            if (!textToSpeech.isSpeaking()) {
                Bundle bundle = new Bundle();
                bundle.putString(CUSTOM_VOICE, item.getShortName());
                bundle.putInt(Constants.CUSTOM_VOICE_INDEX, position);
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
        APP.putInt(Constants.VOICE_VOLUME, volumeValue);
        binding.ttsStyleDegree.setProgress(styleDegree);
        binding.ttsStyleDegreeValue.setText("强度:" + styleDegree / TtsStyle.DEFAULT_DEGREE + "." + styleDegree % TtsStyle.DEFAULT_DEGREE + "音量:" + volumeValue);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean i = powerManager.isIgnoringBatteryOptimizations(this.getPackageName());
            if (i) {
                binding.btnKillBattery.setVisibility(View.GONE);
            } else {
                binding.btnKillBattery.setVisibility(View.VISIBLE);
            }
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                String url = "https://api.github.com/repos/ag2s20150909/TTS/tags";
                String s = HttpTool.httpGet(url);
                Log.e(TAG, s);
                String tag = new JSONArray(s).getJSONObject(0).getString("name");
                Log.e(TAG, tag);


                BigDecimal versionName = new BigDecimal(tag.split("_")[1].trim());
                PackageManager pm = MainActivity.this.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(MainActivity.this.getPackageName(), 0);
                BigDecimal appVersionName = new BigDecimal(pi.versionName.split("_")[1].trim());
                Log.d(TAG, appVersionName + "\n" + versionName);
                if (appVersionName.compareTo(versionName) < 0) {
                    Log.d(TAG, "需要更新。");
                    downLoadAndInstall(tag);
                } else {
                    //downLoadAndInstall(tag);
                    Log.d(TAG, "不需要更新。");
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "不需要更新", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "", e);
                e.printStackTrace();
            }
        }).start();
    }

    private void downLoadAndInstall(String tag) {
        try {
            //String url = "https://fastly.jsdelivr.net/gh/ag2s20150909/TTS@release/TTS_release_v" + tag+".apk";
            String downUrl = "https://github.com/ag2s20150909/TTS/releases/download/" + tag + "/TTS_release_v" + tag + ".apk";

            Log.e(TAG, downUrl);
            runOnUiThread(() -> new AlertDialog.Builder(MainActivity.this)
                    .setTitle("有新版本")
                    .setMessage("发现新版本:\n" + tag + "\n如需更新，点击确定，将跳转到浏览器下载。如不想更新，点击取消，将不再自动检查更新，直到你清除应用数据。你可以到右上角菜单手动检查更新。")
                    .setPositiveButton("确定", (dialog, which) -> HttpTool.downLoadFile(downUrl, getExternalCacheDir().getAbsolutePath() + "/TTS_release_v" + tag + ".apk", new HttpTool.DownloadCallBack() {
                        @Override
                        public void onSuccess(String path) {
                            new ApkInstall(getApplicationContext()).installAPK(path);
                        }

                        @Override
                        public void onError(String err) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(downUrl));
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


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.btnSetTts.getId()) {
            setTTS();
        } else if (id == binding.btnKillBattery.getId()) {
            killBATTERY();
        } else if (id == binding.ttsStyleDegreeAdd.getId()) {
            if (styleDegree < 200) {
                styleDegree++;
                updateView();
            }
        } else if (id == binding.ttsStyleDegreeReduce.getId()) {
            if (styleDegree > 1) {
                styleDegree--;
                updateView();
            }
        }

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();
        if (id == binding.ttsStyleDegree.getId()) {
            styleDegree = progress;
            updateView();
        } else if (id == binding.ttsVoiceVolume.getId()) {
            volumeValue = progress;
            updateView();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}