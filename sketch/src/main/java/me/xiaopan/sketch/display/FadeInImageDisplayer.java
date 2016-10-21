package me.xiaopan.sketch.display;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;

import me.xiaopan.sketch.request.ImageViewInterface;

/**
 * 渐入动画
 */
@SuppressWarnings("unused")
public class FadeInImageDisplayer implements ImageDisplayer {

    private int duration;

    public FadeInImageDisplayer(int duration) {
        this.duration = duration;
    }

    public FadeInImageDisplayer() {
        this(400);
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
    public String getIdentifier() {
        return appendIdentifier(null, new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(String join, StringBuilder builder) {
        if (!TextUtils.isEmpty(join)) {
            builder.append(join);
        }
        return builder.append("FadeInImageDisplayer")
                .append("(")
                .append("duration=").append(duration)
                .append(")");
    }
}
