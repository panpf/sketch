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

package me.xiaopan.android.imageloader.display;

import me.xiaopan.android.imageloader.task.display.DisplayRequest;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

/**
 * 位图显示器
 */
public interface BitmapDisplayer {
	static final int DEFAULT_ANIMATION_DURATION = 400;
	/**
	 * 显示
	 * @param imageView
	 * @param bitmapDrawable
	 * @param bitmapType
	 * @param displayRequest
	 */
	public void display(ImageView imageView, BitmapDrawable bitmapDrawable, BitmapType bitmapType, DisplayRequest displayRequest);
	
	/**
	 * 拷贝
	 * @return
	 */
	public BitmapDisplayer copy();
	
	public enum BitmapType {
		SUCCESS, FAILURE;
	}
}
