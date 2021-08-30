package me.ag2s.tts;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class DownloadVoiceData extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }
}