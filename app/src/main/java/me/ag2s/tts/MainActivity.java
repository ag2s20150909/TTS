package me.ag2s.tts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;


public class MainActivity extends Activity {
    private static final String TAG = "CheckVoiceData";

    private static final String[] SUPPORTED_LANGUAGES = { "eng-GBR", "eng-USA" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




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