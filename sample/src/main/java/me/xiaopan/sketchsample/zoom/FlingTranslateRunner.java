package me.xiaopan.sketchsample.zoom;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;
import android.widget.ImageView;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketchsample.zoom.scrollerproxy.ScrollerProxy;

class FlingTranslateRunner implements Runnable {
    private final ScrollerProxy mScroller;
    private FlingTranslateListener flingTranslateListener;
    private ImageZoomer imageZoomer;
    private int mCurrentX, mCurrentY;

    public FlingTranslateRunner(Context context, ImageZoomer imageZoomer) {
        this.mScroller = ScrollerProxy.getScroller(context);
        this.flingTranslateListener = imageZoomer;
        this.imageZoomer = imageZoomer;
    }

    public void fling(int velocityX, int velocityY) {
        ImageView imageView = imageZoomer.getImageView();
        if (imageView == null) {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, ImageZoomer.NAME + ". fling. imageView is null");
            }
            return;
        }

        final RectF rect = imageZoomer.getDisplayRect();
        if (null == rect) {
            return;
        }

        final int startX = Math.round(-rect.left);
        final int minX, maxX, minY, maxY;
        int viewWidth = imageZoomer.getImageViewWidth(imageView);
        if (viewWidth < rect.width()) {
            minX = 0;
            maxX = Math.round(rect.width() - viewWidth);
        } else {
            minX = maxX = startX;
        }

        int viewHeight = imageZoomer.getImageViewHeight(imageView);
        final int startY = Math.round(-rect.top);
        if (viewHeight < rect.height()) {
            minY = 0;
            maxY = Math.round(rect.height() - viewHeight);
        } else {
            minY = maxY = startY;
        }

        mCurrentX = startX;
        mCurrentY = startY;

        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, ImageZoomer.NAME + ". fling. StartX:" + startX + " StartY:" + startY + " MaxX:" + maxX + " MaxY:" + maxY);
        }

        // If we actually can move, fling the scroller
        if (startX != maxX || startY != maxY) {
            mScroller.fling(startX, startY, velocityX, velocityY, minX,
                    maxX, minY, maxY, 0, 0);
        }

        imageView.removeCallbacks(this);
        imageView.post(this);
    }

    @Override
    public void run() {
        if (mScroller.isFinished()) {
            return; // remaining post that should not be handled
        }

        ImageView imageView = imageZoomer.getImageView();
        if (null != imageView && mScroller.computeScrollOffset()) {

            final int newX = mScroller.getCurrX();
            final int newY = mScroller.getCurrY();

            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, ImageZoomer.NAME + ". fling run(). CurrentX:" + mCurrentX + " CurrentY:" + mCurrentY + " NewX:" + newX + " NewY:" + newY);
            }

            flingTranslateListener.onFlingTranslate(mCurrentX - newX, mCurrentY - newY);

            mCurrentX = newX;
            mCurrentY = newY;

            // Post On animation
            CompatUtils.postOnAnimation(imageView, this);
        }
    }

    public void cancelFling() {
        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, ImageZoomer.NAME + ". Cancel Fling");
        }

        if (mScroller != null) {
            mScroller.forceFinished(true);
        }
        ImageView imageView = imageZoomer.getImageView();
        if (imageView != null) {
            imageView.removeCallbacks(this);
        }
    }

    public interface FlingTranslateListener {
        void onFlingTranslate(float dx, float dy);
    }
}
