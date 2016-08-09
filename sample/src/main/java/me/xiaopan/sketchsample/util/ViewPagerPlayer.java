package me.xiaopan.sketchsample.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * 播放器
 */
public class ViewPagerPlayer implements Runnable {
    private ViewPager viewPager;
    private boolean playing;
    private Handler handler;
    private long delayMillis = 4000;
    private boolean leftToRight = true;
    private FixedSpeedScroller fixedSpeedScroller;

    public ViewPagerPlayer(ViewPager viewPager) {
        this.viewPager = viewPager;
        this.handler = new Handler(Looper.getMainLooper());
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            field.set(viewPager, fixedSpeedScroller = new FixedSpeedScroller(viewPager.getContext(), new AccelerateDecelerateInterpolator()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean start() {
        if (playing) {
            return false;
        }

        fixedSpeedScroller.setAnimationDuration(1000);
        leftToRight = true;
        playing = true;
        handler.post(this);
        return true;
    }

    public boolean stop() {
        if (!playing) {
            return false;
        }

        fixedSpeedScroller.setAnimationDuration(300);
        playing = false;
        handler.removeCallbacks(this);
        return true;
    }

    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void run() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem == 0) {
            leftToRight = true;
        } else if (currentItem == viewPager.getAdapter().getCount() - 1) {
            leftToRight = false;
        }

        int nextItem;
        if (leftToRight) {
            nextItem = currentItem + 1;
        } else {
            nextItem = currentItem - 1;
        }
        viewPager.setCurrentItem(nextItem, true);
        if (playing) {
            handler.postDelayed(this, delayMillis);
        }
    }

    private class FixedSpeedScroller extends Scroller {
        private int animationDuration = 300;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, animationDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, animationDuration);
        }

        public void setAnimationDuration(int animationDuration) {
            this.animationDuration = animationDuration;
        }
    }
}
