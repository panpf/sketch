package me.xiaoapn.easy.imageloader;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * 透明度渐变显示动画监听器，可使图片在500毫秒内透明度从0.5到1.0变化
 */
public class AlphaShowAnimationListener implements ShowAnimationListener {

	@Override
	public Animation onGetShowAnimation() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
		alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		alphaAnimation.setDuration(400);
		return alphaAnimation;
	}
}