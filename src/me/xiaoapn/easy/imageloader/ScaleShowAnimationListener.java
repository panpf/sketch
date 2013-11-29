package me.xiaoapn.easy.imageloader;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * 缩放显示动画，可使图片在500毫秒内从50%放大到100%
 */
public class ScaleShowAnimationListener implements ShowAnimationListener {

	@Override
	public Animation onGetShowAnimation() {
		ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		scaleAnimation.setDuration(500);
		return scaleAnimation;
	}
}