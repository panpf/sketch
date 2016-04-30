package me.xiaopan.sketch;

import android.graphics.Canvas;
import android.view.MotionEvent;

public class DisplayFunction implements ImageViewFunction{
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public boolean onDisplayStarted() {
        return false;
    }

    @Override
    public boolean onDisplayCompleted(ImageFrom imageFrom, String mimeType) {
        return false;
    }

    @Override
    public boolean onDisplayFailed(FailedCause failedCause) {
        return false;
    }
}
