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

package me.xiaopan.android.spear.util;

public class ImageSize {
	private int width;
	private int height;

	public ImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

    /**
     * 获取宽度
     * @return 宽度
     */
	public int getWidth() {
		return width;
	}

    /**
     * 获取高度
     * @return 高度
     */
	public int getHeight() {
		return height;
	}
}
