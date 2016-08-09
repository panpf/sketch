package me.xiaopan.sketchsample.zoom;

import android.widget.ImageView;

class ZoomRunner implements Runnable {

    private ImageZoomer imageZoomer;
    private final float mFocalX, mFocalY;
    private final long mStartTime;
    private final float mZoomStart, mZoomEnd;

    public ZoomRunner(ImageZoomer imageZoomer, final float currentZoom, final float targetZoom, final float focalX, final float focalY) {
        this.imageZoomer = imageZoomer;
        mFocalX = focalX;
        mFocalY = focalY;
        mStartTime = System.currentTimeMillis();
        mZoomStart = currentZoom;
        mZoomEnd = targetZoom;
    }

    @Override
    public void run() {
        ImageView imageView = imageZoomer.getImageView();
        if (imageView == null) {
            return;
        }

        float t = interpolate();
        float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
        float deltaScale = scale / imageZoomer.getScale();

        imageZoomer.onScale(deltaScale, mFocalX, mFocalY);

        // We haven't hit our target scale yet, so post ourselves again
        if (t < 1f) {
            CompatUtils.postOnAnimation(imageView, this);
        }
    }

    private float interpolate() {
        float t = 1f * (System.currentTimeMillis() - mStartTime) / imageZoomer.getZoomDuration();
        t = Math.min(1f, t);
        t = imageZoomer.getZoomInterpolator().getInterpolation(t);
        return t;
    }
}
