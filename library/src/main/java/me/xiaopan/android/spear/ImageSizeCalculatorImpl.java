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

package me.xiaopan.android.spear;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * 图片尺寸计算器
 */
public class ImageSizeCalculatorImpl implements ImageSizeCalculator{
    private ImageSize defaultMaxsize;

    @Override
    public ImageSize calculateImageMaxsize(ImageView imageView) {
        int width = getWidth(imageView, true, true, false);
        int height = getHeight(imageView, true, true, false);
        if (width > 0 || height > 0){
            return new ImageSize(width, height);
        }else{
            return null;
        }
    }

    @Override
    public ImageSize calculateImageResize(ImageView imageView) {
        int width = getWidth(imageView, false, false, true);
        int height = getHeight(imageView, false, false, true);
        if (width > 0 && height > 0){
            return new ImageSize(width, height);
        }else{
            return null;
        }
    }

    @Override
    public ImageSize getDefaultImageMaxsize(Context context) {
        if(defaultMaxsize == null){
            synchronized (this){
                if(defaultMaxsize == null){
                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                    defaultMaxsize = new ImageSize((int) (displayMetrics.widthPixels*1.5f), (int) (displayMetrics.heightPixels*1.5f));
                }
            }
        }
        return defaultMaxsize;
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
        // 如果目标尺寸都小于等于0，那就别计算了没意义
        if(targetWidth <= 0 && targetHeight <= 0){
            return 1;
        }

        // 如果目标尺寸都大于等于原始尺寸，也别计算了没意义
        if(targetWidth >= outWidth && targetHeight >= outHeight){
            return 1;
        }

        // 首先根据缩放后只要有任何一边小于等于目标即可的规则计算一遍inSampleSize
        int inSampleSize = 1;
        do{
            inSampleSize *= 2;
        }while ((outWidth/inSampleSize) > targetWidth && (outHeight/inSampleSize) > targetHeight);

        // 然后根据比较像素总数的原则过滤掉那些比较极端的一边特别小，一边特别大的图片
        // 比如目标尺寸是400x400，图片的尺寸是6000*600，缩放后是3000*300
        // 这样看来的确是满足了第一个条件了，但是图片的尺寸依然很大
        // 因此这一步我们根据像素总数来过滤，规则是总像素数不得大于目标尺寸像素数的两倍
        long totalPixels = (outWidth/inSampleSize) * (outHeight/inSampleSize);
        final long totalReqPixelsCap = targetWidth * targetHeight * 2;
        while (totalPixels > totalReqPixelsCap) {
            inSampleSize *= 2;
            totalPixels /= 2;
        }

        return inSampleSize;
    }

    public static int getWidth(ImageView imageView, boolean checkMaxWidth, boolean acceptWrapContent, boolean subtractPadding) {
        if(imageView == null){
            return 0;
        }

        int width = 0;
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (params != null){
            width = params.width;
            if(subtractPadding && width > 0  && (width - imageView.getPaddingLeft() - imageView.getPaddingRight()) > 0){
                width -= imageView.getPaddingLeft()+imageView.getPaddingRight();
                return width;
            }
        }
        if(width <= 0 && checkMaxWidth){
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if(width <= 0 && acceptWrapContent && params != null && params.width == ViewGroup.LayoutParams.WRAP_CONTENT){
            width = -1;
        }
        return width;
    }

    public static int getHeight(ImageView imageView, boolean checkMaxHeight, boolean acceptWrapContent, boolean subtractPadding) {
        if(imageView == null){
            return 0;
        }

        int height = 0;
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (params != null){
            height = params.height;
            if(subtractPadding && height > 0 && (height - imageView.getPaddingTop() - imageView.getPaddingBottom()) > 0){
                height -= imageView.getPaddingTop()+imageView.getPaddingBottom();
                return height;
            }
        }
        if(height <= 0 && checkMaxHeight){
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
