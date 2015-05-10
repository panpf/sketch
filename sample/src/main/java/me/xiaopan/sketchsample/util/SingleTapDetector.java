package me.xiaopan.sketchsample.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * 单机手势识别器
 */
public class SingleTapDetector extends GestureDetector.SimpleOnGestureListener {
    private GestureDetector gestureDetector;
    private OnSingleTapListener onSingleTapListener;

    public SingleTapDetector(Context context, OnSingleTapListener onSingleTapListener) {
        this.onSingleTapListener = onSingleTapListener;
        this.gestureDetector = new GestureDetector(context, this);
    }

    public boolean onTouchEvent(MotionEvent ev){
        return gestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if(onSingleTapListener != null){
            onSingleTapListener.onSingleTapUp(e);
            return true;
        }else{
            return super.onSingleTapConfirmed(e);
        }
    }

    public interface OnSingleTapListener{
        public boolean onSingleTapUp(MotionEvent e);
    }
}
