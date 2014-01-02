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
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;

/**
 * 缩放动画生成器
 */
public class ScaleAnimationGenerator implements AnimationGenerator {
	private float fromX, toX, fromY, toY, pivotXValue, pivotYValue;
	private int pivotXType, pivotYType;
	private long durationMillis;
	private Interpolator interpolator;
	
	public ScaleAnimationGenerator(float fromX, float toX, float fromY, float toY, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue, long durationMillis, Interpolator interpolator){
		this.fromX = fromX;
		this.toX = toX;
		this.fromY = fromY;
		this.toY = toY;
		this.pivotXType = pivotXType;
		this.pivotXValue = pivotXValue;
		this.pivotYType = pivotYType;
		this.pivotYValue = pivotYValue;
		this.durationMillis = durationMillis;
		this.interpolator = interpolator;
	}
	
	public ScaleAnimationGenerator(){
		this(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, 300, new AccelerateDecelerateInterpolator());
	}
	
	@Override
	public Animation generateAnimation() {
		ScaleAnimation scaleAnimation = new ScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
		scaleAnimation.setInterpolator(interpolator);
		scaleAnimation.setDuration(durationMillis);
		return scaleAnimation;
	}
}