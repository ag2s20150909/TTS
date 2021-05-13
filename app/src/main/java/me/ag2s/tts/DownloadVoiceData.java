package me.ag2s.tts;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;

public class DownloadVoiceData extends Activity {

    private static final String TAG = DownloadVoiceData.class.getSimpleName();
    RelativeLayout rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }
}