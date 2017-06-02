package me.xiaopan.sketch.display;

import android.graphics.drawable.Drawable;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;

import me.xiaopan.sketch.SketchView;

/**
 * 渐入动画
 */
@SuppressWarnings("unused")
public class FadeInImageDisplayer implements ImageDisplayer {
    private static final String KEY = "FadeInImageDisplayer";

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
    public void display(SketchView sketchView, Drawable newDrawable) {
        if (newDrawable == null) {
            return;
        }
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(duration);
        sketchView.clearAnimation();
        sketchView.setImageDrawable(newDrawable);
        sketchView.startAnimation(animation);
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
        return String.format("%s(duration=%d, alwaysUse=%s)", KEY, duration, alwaysUse);
    }
}
