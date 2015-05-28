/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.display;

import android.graphics.drawable.Drawable;

import me.xiaopan.sketch.SketchImageViewInterface;

/**
 * 图片显示器
 */
public interface ImageDisplayer {
	int DEFAULT_ANIMATION_DURATION = 400;
	/**
	 * 显示
	 * @param sketchImageViewInterface ImageView
	 * @param newDrawable 图片
	 */
	void display(SketchImageViewInterface sketchImageViewInterface, Drawable newDrawable);

	/**
	 * 获取标识符
	 * @return 标识符
	 */
	String getIdentifier();

	/**
	 * 追加标识符
	 */
	StringBuilder appendIdentifier(StringBuilder builder);
}