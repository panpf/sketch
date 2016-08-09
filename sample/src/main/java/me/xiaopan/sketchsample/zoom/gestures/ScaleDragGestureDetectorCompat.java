package me.xiaopan.sketchsample.zoom.gestures;

import android.content.Context;
import android.os.Build;

public class ScaleDragGestureDetectorCompat {
    public static ScaleDragGestureDetector newInstance(Context context, OnScaleDragGestureListener listener) {
        final int sdkVersion = Build.VERSION.SDK_INT;
        ScaleDragGestureDetector detector;

        if (sdkVersion >= Build.VERSION_CODES.FROYO) {
            detector = new FroyoScaleDragGestureDetector(context);
        } else if (sdkVersion >= Build.VERSION_CODES.ECLAIR) {
            detector = new EclairScaleDragGestureDetector(context);
        } else {
            detector = new CupcakeScaleDragGestureDetector(context);
        }

        detector.setOnGestureListener(listener);

        return detector;
    }
}
