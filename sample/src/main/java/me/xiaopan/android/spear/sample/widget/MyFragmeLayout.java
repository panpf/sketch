package me.xiaopan.android.spear.sample.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MyFragmeLayout extends FrameLayout{
    public MyFragmeLayout(Context context) {
        super(context);
    }

    public MyFragmeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean use = super.dispatchTouchEvent(ev);
//        System.out.println("MyFrameLayout dispatchTouchEvent "+use);
        return use;
    }
}
