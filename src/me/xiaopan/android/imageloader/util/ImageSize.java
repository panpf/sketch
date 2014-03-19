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

package me.xiaopan.android.imageloader.util;

import me.xiaopan.android.imageloader.task.display.ImageViewHolder;

/**
 * Present width and height values
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class ImageSize {

	private static final int TO_STRING_MAX_LENGHT = 9; // "9999x9999".length()
	private static final String SEPARATOR = "x";

	private final int width;
	private final int height;

	public ImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public ImageSize(int width, int height, int rotation) {
		if (rotation % 180 == 0) {
			this.width = width;
			this.height = height;
		} else {
			this.width = height;
			this.height = width;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/** Scales down dimensions in <b>sampleSize</b> times. Returns new object. */
	public ImageSize scaleDown(int sampleSize) {
		return new ImageSize(width / sampleSize, height / sampleSize);
	}

	/** Scales dimensions according to incoming scale. Returns new object. */
	public ImageSize scale(float scale) {
		return new ImageSize((int) (width * scale), (int) (height * scale));
	}

	@Override
	public String toString() {
		return new StringBuilder(TO_STRING_MAX_LENGHT).append(width).append(SEPARATOR).append(height).toString();
	}
	
	public ImageSize copy(){
		return new ImageSize(width, height);
	}

	/**
	 * Defines target size for image aware view. Size is defined by target
	 * {@link com.nostra13.universalimageloader.core.imageaware.ImageAware view} parameters, configuration
	 * parameters or device display dimensions.<br />
	 */
	public static ImageSize defineTargetSizeForView(ImageViewHolder imageAware, ImageSize maxImageSize) {
		int width = imageAware.getWidth();
		if (width <= 0) width = maxImageSize.getWidth();

		int height = imageAware.getHeight();
		if (height <= 0) height = maxImageSize.getHeight();

		return new ImageSize(width, height);
	}
}
