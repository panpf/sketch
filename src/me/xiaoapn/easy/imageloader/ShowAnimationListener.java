/*
 * Copyright 2013 Peng fei Pan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.xiaoapn.easy.imageloader;

import android.view.animation.Animation;

/**
 * 显示动画监听器
 */
public interface ShowAnimationListener {
	/**
	 * 当获取显示动画，将会使用此动画来显示图片
	 * @return
	 */
	public Animation onGetShowAnimation();
}
