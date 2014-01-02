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

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;

/**
 * 透明度渐变且缩放动画生成器
 */
public class AlphaScaleAnimationGenerator implements AnimationGenerator {
	private AlphaAnimationGenerator alphaAnimationGenerator;
	private ScaleAnimationGenerator scaleAnimationGenerator;
	
	public AlphaScaleAnimationGenerator(float fromAlpha, float toAlpha, float fromX, float toX, float fromY, float toY, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue, long durationMillis, Interpolator interpolator){
		alphaAnimationGenerator = new AlphaAnimationGenerator(fromAlpha, toAlpha, durationMillis, interpolator);
		scaleAnimationGenerator = new ScaleAnimationGenerator(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue, durationMillis, interpolator);
	}
	
	public AlphaScaleAnimationGenerator(){
		alphaAnimationGenerator = new AlphaAnimationGenerator();
		scaleAnimationGenerator = new ScaleAnimationGenerator();
	}
	
	@Override
	public Animation generateAnimation() {
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alphaAnimationGenerator.generateAnimation());
		animationSet.addAnimation(scaleAnimationGenerator.generateAnimation());
		return animationSet;
	}
}
