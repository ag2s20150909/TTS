package me.ag2s.tts.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class ReadView extends RelativeLayout {
    private static final String TAG = "ReadView";

    /**
     * 翻页监听
     */
    public interface PageListener {
        void onPreChapter();

        void onPre(List<String> data, int page);

        void onNext(List<String> data, int page);

        void onNextChapter();

        void onSetting(TextView textView);

    }

    public void setPageListener(PageListener pagelistener) {
        this.pagelistener = pagelistener;
    }

    private PageListener pagelistener;
    /**
     * 未分页时的原始数据
     */
    private String text;
    /**
     * 未分页时的原始数据
     */
    private String title;
    /**
     * 分页完成后的数据列表
     */
    private List<String> pages;
    /**
     * 分页时的偏移数据
     */
    private int pageNum = 0;

    TextView titleTextView;
    LinearLayout bottomLayout;
    TextView progressTextView;
    ReadTextView contentTextView;

    @SuppressLint({"ResourceType", "ClickableViewAccessibility"})
    private ReadView(Builder builder) {
        super(builder.context);
        this.pages = new ArrayList<>();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(layoutParams);
        //标题
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        titleTextView = new TextView(builder.context);
        titleTextView.setId(135900);
        titleTextView.setLayoutParams(titleParams);
        this.addView(titleTextView);

        RelativeLayout.LayoutParams bottomLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottomLayout = new LinearLayout(builder.context);
        bottomLayout.setId(135901);
        bottomLayout.setLayoutParams(bottomLayoutParams);
        this.addView(bottomLayout);
        progressTextView=new TextView(builder.context);
        progressTextView.setPadding(100,0,100,0);
        bottomLayout.addView(progressTextView);





        RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        contentParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        contentParams.addRule(RelativeLayout.BELOW, titleTextView.getId());
        contentParams.addRule(RelativeLayout.ABOVE, bottomLayout.getId());
        contentTextView = new ReadTextView(builder.context);
        contentTextView.setLayoutParams(contentParams);
        this.addView(contentTextView);

        contentTextView.setOnTouchListener((v, event) -> {
            float x = event.getX();
            float y = event.getY();
            float vx = v.getWidth();
            float vy = v.getHeight();

            Log.d(TAG, event.toString());
            if (event.getAction() == MotionEvent.ACTION_DOWN) {


                if (x <= vx / 3.0 && y <= vy / 3.0) {
                    //AA区域
                    Log.d(TAG, "点击了AA");
                    doPre();
                } else if (x < vx * 2.0 / 3.0 && y <= vy / 3.0) {
                    //AB区域
                    Log.d(TAG, "点击了AB");
                    doPre();
                } else if (x >= vx * 2.0 / 3.0 && y <= vy / 3.0) {
                    //AC区域
                    Log.d(TAG, "点击了AC");
                    doNext();
                } else if (x <= vx / 3.0 && y <= vy * 2.0 / 3.0) {
                    //BA区域
                    Log.d(TAG, "点击了BA");
                    doPre();
                } else if (x < vx * 2.0 / 3.0 && y < vy * 2.0 / 3.0) {
                    //BB区域
                    Log.d(TAG, "点击了BB");
                    doSetting();
                } else if (x >= vx * 2.0 / 3.0 && y < vy * 2.0 / 3.0) {
                    //BC区域
                    Log.d(TAG, "点击了BC");
                    doNext();
                } else if (x <= vx / 3.0 && y >= vy * 2.0 / 3.0) {
                    //CA区域
                    Log.d(TAG, "点击了CA");
                    doPre();
                } else if (x < vx * 2.0 / 3.0 && y >= vy * 2.0 / 3.0) {
                    //CB区域
                    Log.d(TAG, "点击了CB");
                    doNext();
                } else if (x >= vx * 2.0 / 3.0 && y >= vy * 2.0 / 3.0) {
                    //CC区域
                    Log.d(TAG, "点击了CC");
                    doNext();
                }
            }
            return false;
        });


    }

    @SuppressLint("DefaultLocale")
    private void doNext() {
        if (pagelistener != null && pages.size() > 0) {
            if (pageNum < pages.size() - 1) {
                pageNum++;
                ObjectAnimator animation = ObjectAnimator.ofFloat(this.contentTextView, "translationX", 100f,0f);
                animation.setDuration(100);
                animation.start();
                this.progressTextView.setText(String.format("%d/%d",pageNum+1,pages.size()));
                this.contentTextView.setText(pages.get(pageNum));
                pagelistener.onNext(pages, pageNum);
            } else {
                pagelistener.onNextChapter();
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void doPre() {
        if (pagelistener != null && pages.size() > 0) {
            if (pageNum > 0) {
                pageNum--;
                ObjectAnimator animation = ObjectAnimator.ofFloat(this.contentTextView, "translationX", -100f,0f);
                animation.setDuration(100);
                animation.start();
                this.progressTextView.setText(String.format("%d/%d",pageNum+1,pages.size()));
                this.contentTextView.setText(pages.get(pageNum));
                pagelistener.onPre(pages, pageNum);
            } else {
                pagelistener.onPreChapter();
            }
        }
    }

    private void doSetting() {
        if (pagelistener != null) {
            pagelistener.onSetting(contentTextView);
        }
    }

    public void setPageData(String text, String title, boolean isFirst) {
        this.text = text;
        this.title = title;
        titleTextView.setText(title);

        new TextViewPagerTool(this.contentTextView, text).setListener(new TextViewPagerTool.RePageListener() {
            @Override
            public void onPage(String txt, int page) {

                // Log.d(TAG, page + "\n" + txt);
            }

            @Override
            public void onFinished(String text, List<String> data, int page) {
                ReadView.this.pages = data;
                if (isFirst) {
                    ReadView.this.pageNum = 0;
                    ReadView.this.contentTextView.setText(pages.get(pageNum));
                } else {
                    ReadView.this.pageNum = ReadView.this.pages.size() - 1;
                    ReadView.this.contentTextView.setText(pages.get(pageNum));
                }

            }
        }).start();


    }

    public static class Builder {
        Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public ReadView get() {
            return new ReadView(this);
        }
    }
}
