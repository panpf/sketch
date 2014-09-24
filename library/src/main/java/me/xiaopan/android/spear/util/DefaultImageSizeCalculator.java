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

import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * 图片尺寸计算器
 */
public class DefaultImageSizeCalculator implements ImageSizeCalculator{
    @Override
    public ImageSize calculateImageMaxsize(ImageView imageView) {
        int width = getWidth(imageView, true, true, true);
        int height = getHeight(imageView, true, true, true);
        if (width <= 0 && height <= 0){
            return null;
        }else{
            return new ImageSize(width, height);
        }
    }

    @Override
    public ImageSize calculateImageResize(ImageView imageView) {
        int width = getWidth(imageView, true, false, false);
        int height = getHeight(imageView, true, false, false);
        if (width > 0 && height > 0){
            return new ImageSize(width, height);
        }
        return null;
    }

    @Override
    public int compareMaxsize(ImageSize maxsize1, ImageSize maxsize2) {
        if(maxsize1 == null || maxsize2 == null){
            return 0;
        }
        return (maxsize1.getWidth() * maxsize1.getHeight()) - (maxsize2.getWidth() - maxsize2.getHeight());
    }

    @Override
    public int calculateInSampleSize(int outWidth, int outHeight, int targetWidth, int targetHeight) {
        if(targetWidth <= 0 && targetHeight <= 0){
            return 1;
        }

        if(targetWidth >= outWidth && targetHeight >= outHeight){
            return 1;
        }

        int inSampleSize = 1;
        do{
            inSampleSize *= 2;
        }while ((outWidth/inSampleSize) > targetWidth && (outHeight/inSampleSize) > targetHeight);
        return inSampleSize;
    }

    public static int getWidth(ImageView imageView, boolean checkRealViewSize, boolean checkMaxViewSize, boolean acceptWrapContent) {
        if(imageView == null){
            return 0;
        }

        int width = 0;
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (checkRealViewSize && params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = imageView.getWidth();
        }
        if (width <= 0 && params != null){
            width = params.width;
        }
        if(width <= 0 && checkMaxViewSize){
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if(width <= 0 && acceptWrapContent && params != null && params.width == ViewGroup.LayoutParams.WRAP_CONTENT){
            width = -1;
        }
        return width;
    }

    public static int getHeight(ImageView imageView, boolean checkRealViewSize, boolean checkMaxViewSize, boolean acceptWrapContent) {
        if(imageView == null){
            return 0;
        }

        int height = 0;
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (checkRealViewSize && params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = imageView.getHeight();
        }
        if (height <= 0 && params != null){
            height = params.height;
        }
        if(height <= 0 && checkMaxViewSize){
            height = getImageViewFieldValue(imageView, "mMaxHeight");
        }
        if(height <= 0 && acceptWrapContent && params != null && params.height == ViewGroup.LayoutParams.WRAP_CONTENT){
            height = -1;
        }
        return height;
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
