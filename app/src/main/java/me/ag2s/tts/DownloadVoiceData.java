package me.ag2s.tts;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import me.ag2s.tts.view.ReadTextView;
import me.ag2s.tts.view.ReadView;
import me.ag2s.tts.view.TextViewPagerTool;

public class DownloadVoiceData extends Activity {

    private static final String TAG = DownloadVoiceData.class.getSimpleName();
    RelativeLayout rv;
    ReadTextView readTextView;
    ReadTextView readTextViewP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rv = new RelativeLayout(this);
        rv.setLayoutParams(layoutParams);
        setContentView(rv);
        ReadView readView = new ReadView.Builder(this).get();
        rv.addView(readView);
        readView.setPageData(TextViewPagerTool.test,"测试标题",true);
        readView.setPageListener(new ReadView.PageListener() {
            @Override
            public void onPreChapter() {
                readView.setPageData(TextViewPagerTool.test,"测试标题",false);
            }

            @Override
            public void onPre(List<String> data, int page) {

            }

            @Override
            public void onNext(List<String> data, int page) {

            }

            @Override
            public void onNextChapter() {
                readView.setPageData(TextViewPagerTool.test,"测试标题",true);
            }

            @Override
            public void onSetting(TextView textView) {

            }
        });
//        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        readTextViewP = new ReadTextView(this);
//        readTextViewP.setLayoutParams(layoutParams1);
//        readTextViewP.setPadding(10, 10, 10, 10);
//
//        readTextView = new ReadTextView(this);
//        readTextView.setLayoutParams(layoutParams1);
//        readTextView.setPadding(10, 10, 10, 10);
//        rv.addView(readTextViewP);
//
//        rv.addView(readTextView);
//        readTextView.setPageData(TextViewPagerTool.test,true);
//
//
//        readTextView.setPageListener(new ReadTextView.PageListener() {
//            @Override
//            public void onPreChapter() {
//                Toast.makeText(DownloadVoiceData.this,"打开前一章",Toast.LENGTH_LONG).show();
//                readTextView.setPageData(TextViewPagerTool.test,false);
//            }
//
//            @Override
//            public void onPre(List<String> data, int page) {
//                 //readTextView.setText(data.get(page));
//            }
//
//            @Override
//            public void onNext(List<String> data, int page) {
//                //readTextView.setText(data.get(page));
//            }
//
//            @Override
//            public void onNextChapter() {
//                Toast.makeText(DownloadVoiceData.this,"打开下一章",Toast.LENGTH_LONG).show();
//                readTextView.setPageData(TextViewPagerTool.test,true);
//            }
//
//            @Override
//            public void onSetting(TextView textView) {
//                //Toast.makeText(DownloadVoiceData.this,"打开设置",Toast.LENGTH_LONG).show();
//            }
//        });


    }
}