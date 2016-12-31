package me.xiaopan.sketch.display;

import android.graphics.drawable.Drawable;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;

import me.xiaopan.sketch.request.ImageViewInterface;

/**
 * 渐入动画
 */
@SuppressWarnings("unused")
public class FadeInImageDisplayer implements ImageDisplayer {
    private String logName = "FadeInImageDisplayer";

    private int duration;
    private boolean alwaysUse;

    public FadeInImageDisplayer(int duration, boolean alwaysUse) {
        this.duration = duration;
        this.alwaysUse = alwaysUse;
    }

    public FadeInImageDisplayer(int duration) {
        this(duration, false);
    }

    public FadeInImageDisplayer(boolean alwaysUse) {
        this(DEFAULT_ANIMATION_DURATION, alwaysUse);
    }

    public FadeInImageDisplayer() {
        this(DEFAULT_ANIMATION_DURATION, false);
    }

    @Override
    public void display(ImageViewInterface imageViewInterface, Drawable newDrawable) {
        if (newDrawable == null) {
            return;
        }
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(duration);
        imageViewInterface.clearAnimation();
        imageViewInterface.setImageDrawable(newDrawable);
        imageViewInterface.startAnimation(animation);
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public boolean isAlwaysUse() {
        return alwaysUse;
    }

    @Override
    public String getKey() {
        return String.format("%s(duration=%d, alwaysUse=%s)", logName, duration, alwaysUse);
    }
}
