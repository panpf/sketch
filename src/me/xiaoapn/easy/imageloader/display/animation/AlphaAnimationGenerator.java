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

package me.xiaoapn.easy.imageloader.display.animation;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;

/**
 * 透明度渐变动画生成器
 */
public class AlphaAnimationGenerator implements AnimationGenerator {
	private float fromAlpha, toAlpha;
	private long durationMillis;
	private Interpolator interpolator;
	
	public AlphaAnimationGenerator(float fromAlpha, float toAlpha, long durationMillis, Interpolator interpolator) {
		this.fromAlpha = fromAlpha;
		this.toAlpha = toAlpha;
		this.durationMillis = durationMillis;
		this.interpolator = interpolator;
	}
	
	public AlphaAnimationGenerator() {
		this(0.5f, 1.0f, 300, new AccelerateDecelerateInterpolator());
	}

	@Override
	public Animation generateAnimation() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
		alphaAnimation.setInterpolator(interpolator);
		alphaAnimation.setDuration(durationMillis);
		return alphaAnimation;
	}
}

