package me.ag2s.tts;

import static me.ag2s.tts.services.Constants.CUSTOM_VOICE;
import static me.ag2s.tts.services.Constants.USE_PREVIEW;

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

import org.json.JSONObject;

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
import me.ag2s.tts.utils.HttpTool;


public class MainActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "CheckVoiceData";
    private static final AtomicInteger mNextRequestId = new AtomicInteger(0);

    boolean connected = false;
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

        connectToText2Speech();


        binding.btnSetTts.setOnClickListener(this);
        binding.btnKillBattery.setOnClickListener(this);
        binding.ttsStyleDegreeAdd.setOnClickListener(this);
        binding.ttsStyleDegreeReduce.setOnClickListener(this);
        binding.ttsVoiceVolumeAdd.setOnClickListener(this);
        binding.ttsVoiceVolumeReduce.setOnClickListener(this);

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

        //boolean useCustomVoice = APP.getBoolean(Constants.USE_CUSTOM_VOICE, true);//sharedPreferences.getBoolean(Constants.USE_CUSTOM_VOICE, true);
        binding.switchUseCustomVoice.setChecked(APP.getBoolean(Constants.USE_CUSTOM_VOICE, true));
        binding.switchUseCustomVoice.setOnCheckedChangeListener((buttonView, isChecked) -> APP.putBoolean(Constants.USE_CUSTOM_VOICE, isChecked));


        binding.switchUseSplitSentence.setChecked(APP.getBoolean(Constants.SPLIT_SENTENCE, false));
        binding.switchUseSplitSentence.setOnCheckedChangeListener((buttonView, isChecked) -> APP.putBoolean(Constants.SPLIT_SENTENCE, isChecked));


        binding.switchUseDict.setChecked(APP.getBoolean(Constants.USE_DICT, false));
        binding.switchUseDict.setOnCheckedChangeListener((buttonView, isChecked) -> APP.putBoolean(Constants.USE_DICT, isChecked));

        binding.switchUsePreview.setChecked(APP.getBoolean(Constants.USE_PREVIEW, false));
        binding.switchUsePreview.setOnCheckedChangeListener(((buttonView, isChecked) -> APP.putBoolean(USE_PREVIEW, isChecked)));


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


            if (textToSpeech != null && !textToSpeech.isSpeaking()) {
                connectToText2Speech();
                Bundle bundle = new Bundle();
                bundle.putString(CUSTOM_VOICE, item.getShortName());
                bundle.putInt(Constants.CUSTOM_VOICE_INDEX, position);
                bundle.putString("voiceName", item.getShortName());
                bundle.putString("language", locale.getISO3Language());
                bundle.putString("country", locale.getISO3Country());
                bundle.putString("variant", item.getGender() ? "Female" : "Male");
                bundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "Sample");
                textToSpeech.speak(TtsVoiceSample.getByLocate(this, locale), TextToSpeech.QUEUE_FLUSH, bundle, MainActivity.class.getName() + mNextRequestId.getAndIncrement());
            } else {
                if (textToSpeech == null) {
                    connectToText2Speech();
                }
                Toast.makeText(MainActivity.this, "" + item.getShortName(), Toast.LENGTH_SHORT).show();
            }

        });

        Toast.makeText(this, "选择预览版语音时,如果卡住了，杀掉应用重进！！！", Toast.LENGTH_LONG).show();


        if (APP.getBoolean(Constants.USE_AUTO_UPDATE, true)) {
            checkUpdate();
        }


    }


    /**
     * 连接Text2Speech
     */
    private void connectToText2Speech() {
        if (textToSpeech == null || textToSpeech.speak("", TextToSpeech.QUEUE_FLUSH, null, null) != TextToSpeech.SUCCESS) {
            textToSpeech = new TextToSpeech(MainActivity.this, status -> {

                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    if (result != TextToSpeech.LANG_MISSING_DATA
                            && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        connected = true;
                        if (!textToSpeech.isSpeaking()) {
                            textToSpeech.speak("初始化成功。", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    }
                }
            }, this.getPackageName());
        }

    }


    @SuppressLint("SetTextI18n")
    private void updateView() {
        APP.putInt(Constants.VOICE_STYLE_DEGREE, styleDegree);
        APP.putInt(Constants.VOICE_VOLUME, volumeValue);
        binding.ttsStyleDegree.setProgress(styleDegree);

        String format = String.format(Locale.US, "强度:%01d.%02d 音量:%03d", styleDegree / TtsStyle.DEFAULT_DEGREE, styleDegree % TtsStyle.DEFAULT_DEGREE, volumeValue);
        binding.ttsStyleDegreeValue.setText(format);
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
        menu.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, R.string.check_update);
        menu.add(Menu.NONE, Menu.FIRST + 2, Menu.NONE, R.string.battery_optimizations);
        menu.add(Menu.NONE, Menu.FIRST + 3, Menu.NONE, R.string.update_dic);

        Menu aa = menu.addSubMenu(100, 100, 1, R.string.audio_format);


        List<TtsOutputFormat> formats = TtsFormatManger.getInstance().getFormats();
        for (int i = 0; i < formats.size(); i++) {
            aa.add(100, 1000 + i, Menu.NONE, formats.get(i).name);
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
        //getToken();
        HttpTool.executorService.submit(() -> {
            try {
                String url = "https://api.github.com/repos/ag2s20150909/tts/releases/latest";
                String s = HttpTool.httpGet(url);
                //Log.e(TAG, s);
                JSONObject json = new JSONObject(s);
                String tag = json.getString("tag_name");
                String downloadUrl = json.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
                String body = json.getString("body");
                Log.e(TAG, tag);


                BigDecimal versionName = new BigDecimal(tag.split("_")[1].trim());
                PackageManager pm = MainActivity.this.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(MainActivity.this.getPackageName(), 0);
                BigDecimal appVersionName = new BigDecimal(pi.versionName.split("_")[1].trim());
                Log.d(TAG, appVersionName + "\n" + versionName);
                if (appVersionName.compareTo(versionName) < 0) {
                    Log.d(TAG, "需要更新。");
                    downLoadAndInstall(body, downloadUrl, tag);
                } else {
                    //downLoadAndInstall(body,downloadUrl,tag);
                    Log.d(TAG, "不需要更新。");
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "不需要更新", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "", e);
                e.printStackTrace();
            }
        });
    }

    private void downLoadAndInstall(String body, String downloadUrl, String tag) {
        try {

            runOnUiThread(() -> new AlertDialog.Builder(MainActivity.this)
                    .setTitle("有新版本")
                    .setMessage("发现新版本:" + tag + "\n" + body)
                    .setPositiveButton("确定", (dialog, which) -> {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(downloadUrl));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                    )
                    .setNegativeButton("取消", (dialog, which) -> APP.putBoolean(Constants.USE_AUTO_UPDATE, false))
                    .create().show());
        } catch (Exception ignored) {
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

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
        } else if (id == binding.ttsVoiceVolumeReduce.getId()) {
            if (volumeValue > 1) {
                volumeValue--;
                updateView();
            }
        } else if (id == binding.ttsVoiceVolumeAdd.getId()) {
            if (volumeValue < 100) {
                volumeValue++;
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