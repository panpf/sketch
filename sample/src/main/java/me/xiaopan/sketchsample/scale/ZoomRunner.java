package me.xiaopan.sketchsample.scale;

import android.widget.ImageView;

class ZoomRunner implements Runnable {

    private ImageAmplifier imageAmplifier;
    private final float mFocalX, mFocalY;
    private final long mStartTime;
    private final float mZoomStart, mZoomEnd;

    public ZoomRunner(ImageAmplifier imageAmplifier, final float currentZoom, final float targetZoom, final float focalX, final float focalY) {
        this.imageAmplifier = imageAmplifier;
        mFocalX = focalX;
        mFocalY = focalY;
        mStartTime = System.currentTimeMillis();
        mZoomStart = currentZoom;
        mZoomEnd = targetZoom;
    }

    @Override
    public void run() {
        ImageView imageView = imageAmplifier.getImageView();
        if (imageView == null) {
            return;
        }

        float t = interpolate();
        float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
        float deltaScale = scale / imageAmplifier.getScale();

        imageAmplifier.onScale(deltaScale, mFocalX, mFocalY);

        // We haven't hit our target scale yet, so post ourselves again
        if (t < 1f) {
            CompatUtils.postOnAnimation(imageView, this);
        }
    }

    private float interpolate() {
        float t = 1f * (System.currentTimeMillis() - mStartTime) / imageAmplifier.getZoomDuration();
        t = Math.min(1f, t);
        t = imageAmplifier.getZoomInterpolator().getInterpolation(t);
        return t;
    }
}
