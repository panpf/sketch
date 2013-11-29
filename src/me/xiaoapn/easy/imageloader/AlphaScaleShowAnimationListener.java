package me.xiaoapn.easy.imageloader;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

/**
 * 缩放显示动画，可使图片在500毫秒内从50%放大到100%
 */
public class AlphaScaleShowAnimationListener implements ShowAnimationListener {

	@Override
	public Animation onGetShowAnimation() {
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
		
		ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(500);
		scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		animationSet.addAnimation(scaleAnimation);
		
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
		alphaAnimation.setDuration(400);
		alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		animationSet.addAnimation(alphaAnimation);
		
		return animationSet;
	}
}