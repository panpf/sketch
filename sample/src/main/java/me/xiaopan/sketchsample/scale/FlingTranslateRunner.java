package me.xiaopan.sketchsample.scale;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;
import android.widget.ImageView;

import me.xiaopan.sketchsample.scale.scrollerproxy.ScrollerProxy;

class FlingTranslateRunner implements Runnable {
    private final ScrollerProxy mScroller;
    private FlingTranslateListener flingTranslateListener;
    private ImageAmplifier imageAmplifier;
    private int mCurrentX, mCurrentY;

    public FlingTranslateRunner(Context context, ImageAmplifier imageAmplifier) {
        this.mScroller = ScrollerProxy.getScroller(context);
        this.flingTranslateListener = imageAmplifier;
        this.imageAmplifier = imageAmplifier;
    }

    public void fling(int velocityX, int velocityY) {
        ImageView imageView = imageAmplifier.getImageView();
        if (imageView == null) {
            if (ImageAmplifier.DEBUG) {
                Log.d(ImageAmplifier.LOG_TAG, "fling. imageView is null");
            }
            return;
        }

        final RectF rect = imageAmplifier.getDisplayRect();
        if (null == rect) {
            return;
        }

        final int startX = Math.round(-rect.left);
        final int minX, maxX, minY, maxY;
        int viewWidth = imageAmplifier.getImageViewWidth(imageView);
        if (viewWidth < rect.width()) {
            minX = 0;
            maxX = Math.round(rect.width() - viewWidth);
        } else {
            minX = maxX = startX;
        }

        int viewHeight = imageAmplifier.getImageViewHeight(imageView);
        final int startY = Math.round(-rect.top);
        if (viewHeight < rect.height()) {
            minY = 0;
            maxY = Math.round(rect.height() - viewHeight);
        } else {
            minY = maxY = startY;
        }

        mCurrentX = startX;
        mCurrentY = startY;

        if (ImageAmplifier.DEBUG) {
            Log.d(ImageAmplifier.LOG_TAG, "fling. StartX:" + startX + " StartY:" + startY + " MaxX:" + maxX + " MaxY:" + maxY);
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

        ImageView imageView = imageAmplifier.getImageView();
        if (null != imageView && mScroller.computeScrollOffset()) {

            final int newX = mScroller.getCurrX();
            final int newY = mScroller.getCurrY();

            if (ImageAmplifier.DEBUG) {
                Log.d(ImageAmplifier.LOG_TAG, "fling run(). CurrentX:" + mCurrentX + " CurrentY:" + mCurrentY + " NewX:" + newX + " NewY:" + newY);
            }

            flingTranslateListener.onFlingTranslate(mCurrentX - newX, mCurrentY - newY);

            mCurrentX = newX;
            mCurrentY = newY;

            // Post On animation
            CompatUtils.postOnAnimation(imageView, this);
        }
    }

    public void cancelFling() {
        if (ImageAmplifier.DEBUG) {
            Log.d(ImageAmplifier.LOG_TAG, "Cancel Fling");
        }

        if (mScroller != null) {
            mScroller.forceFinished(true);
        }
        ImageView imageView = imageAmplifier.getImageView();
        if (imageView != null) {
            imageView.removeCallbacks(this);
        }
    }

    public interface FlingTranslateListener {
        void onFlingTranslate(float dx, float dy);
    }
}
