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

import java.lang.reflect.Field;

import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageSize {
	private int width;
	private int height;

	public ImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ImageSize copy(){
		return new ImageSize(width, height);
	}

	public static ImageSize createDecodeSize(ImageView imageView, ImageSize maxImageSize) {
		int width = getWidth(imageView, true, true);
		int height = getHeight(imageView, true, true);
		if (width > 0 && height > 0){
		    return new ImageSize(width, height);
        }
        if(maxImageSize.getWidth() > 0 && maxImageSize.getHeight() > 0){
            return maxImageSize;
        }
        return null;
	}

	public static ImageSize createProcessSize(ImageView imageView, ImageSize maxProcessSize) {
		int width = getWidth(imageView, true, false);
		int height = getHeight(imageView, true, false);
        if (width > 0 && height > 0){
            return new ImageSize(width, height);
        }
        if(maxProcessSize.getWidth() > 0 && maxProcessSize.getHeight() > 0){
            return maxProcessSize;
        }
        return null;
	}
	
	public static int getWidth(ImageView imageView, boolean checkRealViewSize, boolean checkMaxViewSize) {
		if(imageView != null){
			final ViewGroup.LayoutParams params = imageView.getLayoutParams();
			int width = 0;
			if (checkRealViewSize && params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
				width = imageView.getWidth();
			}
			if (width <= 0 && params != null){
				width = params.width;
			}
			if(width <= 0 && checkMaxViewSize){
				width = getImageViewFieldValue(imageView, "mMaxWidth");
			}
			return width;
		}else{
			return 0;
		}
	}
	
	public static int getHeight(ImageView imageView, boolean checkRealViewSize, boolean checkMaxViewSize) {
		if(imageView != null){
			final ViewGroup.LayoutParams params = imageView.getLayoutParams();
			int height = 0;
			if (checkRealViewSize && params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
				height = imageView.getHeight();
			}
			if (height <= 0 && params != null){
				height = params.height;
			}
			if(height <= 0 && checkMaxViewSize){
				height = getImageViewFieldValue(imageView, "mMaxHeight");
			}
			return height;
		}else{
			return 0;
		}
	}

	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}
