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