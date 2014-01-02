/*
 * Copyright 2013 Peng fei Pan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaoapn.easy.imageloader.display;

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