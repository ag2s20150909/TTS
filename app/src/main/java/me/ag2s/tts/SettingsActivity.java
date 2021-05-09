package me.ag2s.tts;

import android.app.Activity;
import android.os.Bundle;


public class SettingsActivity extends Activity {

    private static final String TITLE_TAG = "settingsActivityTitle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

    }


}