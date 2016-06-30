package me.xiaopan.sketchsample.scale;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

class TapListener extends GestureDetector.SimpleOnGestureListener {

    private ImageAmplifier imageAmplifier;

    public TapListener(ImageAmplifier imageAmplifier) {
        this.imageAmplifier = imageAmplifier;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        ImageView imageView = imageAmplifier.getImageView();
        if (imageView != null && imageAmplifier.getOnViewTapListener() != null) {
            imageAmplifier.getOnViewTapListener().onViewTap(imageView, e.getX(), e.getY());
        }

        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent ev) {
        try {
            float scale = imageAmplifier.getScale();
            float x = ev.getX();
            float y = ev.getY();

            if (scale < imageAmplifier.getMediumScale()) {
                imageAmplifier.setScale(imageAmplifier.getMediumScale(), x, y, true);
            } else if (scale >= imageAmplifier.getMediumScale() && scale < imageAmplifier.getMaximumScale()) {
                imageAmplifier.setScale(imageAmplifier.getMaximumScale(), x, y, true);
            } else {
                imageAmplifier.setScale(imageAmplifier.getMinimumScale(), x, y, true);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // Can sometimes happen when getX() and getY() is called
        }

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        // Wait for the confirmed onDoubleTap() instead
        return false;
    }
}
