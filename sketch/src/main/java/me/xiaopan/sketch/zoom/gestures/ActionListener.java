package me.xiaopan.sketch.zoom.gestures;

import android.view.MotionEvent;

public interface ActionListener {
    void onActionDown(MotionEvent ev);

    void onActionUp(MotionEvent ev);

    void onActionCancel(MotionEvent ev);
}
