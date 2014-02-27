/*
 * Copyright 2014 Peng fei Pan
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

package me.xiaopan.android.imageloader.task;

import android.graphics.drawable.BitmapDrawable;

class DrawableHolder {
	private int resId;	//当正在加载时显示的图片
	private BitmapDrawable drawable;	//当加载地址为空时显示的图片
	
	public DrawableHolder(int resId) {
		this.resId = resId;
	}
	
	public DrawableHolder() {
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public BitmapDrawable getDrawable() {
		return drawable;
	}

	public void setDrawable(BitmapDrawable drawable) {
		this.drawable = drawable;
	}
}
