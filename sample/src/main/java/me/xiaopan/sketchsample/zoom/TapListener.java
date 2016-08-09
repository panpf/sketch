package me.xiaopan.sketchsample.zoom;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

class TapListener extends GestureDetector.SimpleOnGestureListener {

    private ImageZoomer imageZoomer;

    public TapListener(ImageZoomer imageZoomer) {
        this.imageZoomer = imageZoomer;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        ImageView imageView = imageZoomer.getImageView();
        if (imageView != null && imageZoomer.getOnViewTapListener() != null) {
            imageZoomer.getOnViewTapListener().onViewTap(imageView, e.getX(), e.getY());
        }

        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent ev) {
        try {
            float scale = imageZoomer.getScale();
            float x = ev.getX();
            float y = ev.getY();

            if (scale < imageZoomer.getMediumScale()) {
                imageZoomer.setScale(imageZoomer.getMediumScale(), x, y, true);
            } else if (scale >= imageZoomer.getMediumScale() && scale < imageZoomer.getMaximumScale()) {
                imageZoomer.setScale(imageZoomer.getMaximumScale(), x, y, true);
            } else {
                imageZoomer.setScale(imageZoomer.getMinimumScale(), x, y, true);
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
