package me.ag2s.tts.view;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ReadTextView extends TextView {
    private static final String TAG = ReadTextView.class.getSimpleName();


    public ReadTextView(Context context) {
        this(context, null);
    }

    public ReadTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ReadTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);


    }





    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        //Log.d(TAG,event.toString());
//        float x = event.getX();
//        float y = event.getY();
//        float vx = getWidth();
//        float vy = getHeight();
//
//        if (x <= vx / 3.0 && y <= vy / 3.0) {
//            //AA区域
//            Log.d(TAG, "点击了AA");
//            doPre();
//        } else if (x < vx * 2.0 / 3.0 && y <= vy / 3.0) {
//            //AB区域
//            Log.d(TAG, "点击了AB");
//            doPre();
//        } else if (x >= vx * 2.0 / 3.0 && y <= vy / 3.0) {
//            //AC区域
//            Log.d(TAG, "点击了AC");
//            doNext();
//        } else if (x <= vx / 3.0 && y <= vy * 2.0 / 3.0) {
//            //BA区域
//            Log.d(TAG, "点击了BA");
//            doPre();
//        } else if (x < vx * 2.0 / 3.0 && y < vy * 2.0 / 3.0) {
//            //BB区域
//            Log.d(TAG, "点击了BB");
//            doSetting();
//        } else if (x >= vx * 2.0 / 3.0 && y < vy * 2.0 / 3.0) {
//            //BC区域
//            Log.d(TAG, "点击了BC");
//            doNext();
//        } else if (x <= vx / 3.0 && y >= vy * 2.0 / 3.0) {
//            //CA区域
//            Log.d(TAG, "点击了CA");
//            doPre();
//        } else if (x < vx * 2.0 / 3.0 && y >= vy * 2.0 / 3.0) {
//            //CB区域
//            Log.d(TAG, "点击了CB");
//            doNext();
//        } else if (x >= vx * 2.0 / 3.0 && y >= vy * 2.0 / 3.0) {
//            //CC区域
//            Log.d(TAG, "点击了CC");
//            doNext();
//        }
//
////        Log.d(TAG, "x=" + x + "y=" + y);
////        Log.d(TAG, "vx=" + vx + "vy=" + vy);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void doSetting() {
    }

    private void doNext() {
    }

    private void doPre() {
    }

    /**
     * 去除当前页无法显示的字
     *
     * @return 去掉的字数
     */
    public int resize() {
        CharSequence oldContent = getText();
        CharSequence newContent = oldContent.subSequence(0, getCharNum());
        setText(newContent);
        return oldContent.length() - newContent.length();
    }

    /**
     * 获取当前页总字数
     */
    public int getCharNum() {
        return getLayout().getLineEnd(getLineNum());
    }

    /**
     * 获取当前页总行数
     */
    public int getLineNum() {
        Layout layout = getLayout();
        int topOfLastLine = getHeight() - getPaddingTop() - getPaddingBottom() - getLineHeight();
        return layout.getLineForVertical(topOfLastLine);
    }
}
