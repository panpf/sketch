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


package me.xiaopan.sketch.feature;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;

import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.request.FixedSize;
import me.xiaopan.sketch.request.ImageViewInterface;
import me.xiaopan.sketch.request.MaxSize;
import me.xiaopan.sketch.request.Resize;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 图片最大尺寸和修正尺寸计算器
 */
public class ImageSizeCalculator implements Identifier {
    protected String logName = "ImageSizeCalculator";

    private int openGLMaxTextureSize = -1;
    private float targetSizeScale = 1.1f;

    public static int getWidth(View imageView, boolean checkMaxWidth, boolean acceptWrapContent, boolean subtractPadding) {
        if (imageView == null) {
            return 0;
        }

        int width = 0;
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (params != null) {
            width = params.width;
            if (subtractPadding && width > 0 && (width - imageView.getPaddingLeft() - imageView.getPaddingRight()) > 0) {
                width -= imageView.getPaddingLeft() + imageView.getPaddingRight();
                return width;
            }
        }
        if (width <= 0 && checkMaxWidth) {
            width = getViewFieldValue(imageView, "mMaxWidth");
        }
        if (width <= 0 && acceptWrapContent && params != null && params.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = -1;
        }
        return width;
    }

    public static int getHeight(View imageView, boolean checkMaxHeight, boolean acceptWrapContent, boolean subtractPadding) {
        if (imageView == null) {
            return 0;
        }

        int height = 0;
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (params != null) {
            height = params.height;
            if (subtractPadding && height > 0 && (height - imageView.getPaddingTop() - imageView.getPaddingBottom()) > 0) {
                height -= imageView.getPaddingTop() + imageView.getPaddingBottom();
                return height;
            }
        }
        if (height <= 0 && checkMaxHeight) {
            height = getViewFieldValue(imageView, "mMaxHeight");
        }
        if (height <= 0 && acceptWrapContent && params != null && params.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = -1;
        }
        return height;
    }

    private static int getViewFieldValue(Object object, String fieldName) {
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

    /**
     * 获取OpenGL所允许的最大尺寸
     */
    @SuppressWarnings("WeakerAccess")
    public int getOpenGLMaxTextureSize() {
        if (openGLMaxTextureSize == -1) {
            openGLMaxTextureSize = SketchUtils.getOpenGLMaxTextureSize();
        }
        return openGLMaxTextureSize;
    }

    /**
     * 设置OpenGL所允许的最大尺寸,用来计算inSampleSize
     */
    @SuppressWarnings("unused")
    public void setOpenGLMaxTextureSize(int openGLMaxTextureSize) {
        this.openGLMaxTextureSize = openGLMaxTextureSize;
    }

    /**
     * 计算MaxSize
     *
     * @param imageViewInterface 你需要根据ImageView的宽高来计算
     * @return MaxSize
     */
    public MaxSize calculateImageMaxSize(ImageViewInterface imageViewInterface) {
        View imageView = imageViewInterface.getSelf();
        if (imageView == null) {
            return null;
        }

        int width = getWidth(imageView, true, true, false);
        int height = getHeight(imageView, true, true, false);
        if (width > 0 || height > 0) {
            // 因为OpenGL对图片的宽高有上限，因此要限制一下，这里就严格一点不能大于屏幕宽高的1.5倍
            DisplayMetrics displayMetrics = imageViewInterface.getSelf().getResources().getDisplayMetrics();
            int maxWidth = (int) (displayMetrics.widthPixels * 1.5f);
            int maxHeight = (int) (displayMetrics.heightPixels * 1.5f);
            if (width > maxWidth || height > maxHeight) {
                float widthScale = (float) width / maxWidth;
                float heightScale = (float) height / maxHeight;
                float finalScale = widthScale > heightScale ? widthScale : heightScale;

                width /= finalScale;
                height /= finalScale;
            }
            return new MaxSize(width, height);
        } else {
            return null;
        }
    }

    /**
     * 获取默认的maxSize，默认maxSize是屏幕宽高的70%
     *
     * @param context 上下文
     * @return maxSize
     */
    public MaxSize getDefaultImageMaxSize(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return new MaxSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    /**
     * 计算Resize
     *
     * @param imageViewInterface 你需要根据ImageView的宽高来计算
     * @return Resize
     */
    @Deprecated
    public Resize calculateImageResize(ImageViewInterface imageViewInterface) {
        View imageView = imageViewInterface.getSelf();
        if (imageView == null) {
            return null;
        }

        int width = getWidth(imageView, false, false, true);
        int height = getHeight(imageView, false, false, true);
        if (width > 0 && height > 0) {
            return new Resize(width, height, imageViewInterface.getScaleType());
        } else {
            return null;
        }
    }

    /**
     * 计算FixedSize
     *
     * @param imageViewInterface 你需要根据ImageView的宽高来计算
     * @return FixedSize
     */
    public FixedSize calculateImageFixedSize(ImageViewInterface imageViewInterface) {
        View imageView = imageViewInterface.getSelf();
        if (imageView == null) {
            return null;
        }

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
            int fixedWidth = layoutParams.width - (imageView.getPaddingLeft() + imageView.getPaddingRight());
            int fixedHeight = layoutParams.height - (imageView.getPaddingTop() + imageView.getPaddingBottom());

            // 限制不能超过OpenGL所允许的最大尺寸
            int maxSize = getOpenGLMaxTextureSize();
            if (fixedWidth > maxSize || fixedHeight > maxSize) {
                float finalScale = Math.max((float) fixedWidth / maxSize, (float) fixedHeight / maxSize);

                fixedWidth /= finalScale;
                fixedHeight /= finalScale;
            }
            return new FixedSize(fixedWidth, fixedHeight);
        }
        return null;
    }

    /**
     * 比较两个maxSize的大小，在使用options()方法批量设置属性的时候会使用此方法比较Options的maxSize和已有的maxSize，如果前者小于后者就会使用前者代替后者
     *
     * @param maxSize1 maxSize1
     * @param maxSize2 maxSize2
     * @return 等于0：两者相等；小于0：maxSize1小于maxSize2；大于0：maxSize1大于maxSize2
     */
    @Deprecated
    public int compareMaxSize(MaxSize maxSize1, MaxSize maxSize2) {
        if (maxSize1 == null || maxSize2 == null) {
            return 0;
        }
        return (maxSize1.getWidth() * maxSize1.getHeight()) - (maxSize2.getWidth() - maxSize2.getHeight());
    }

    /**
     * 计算InSampleSize
     *
     * @param outWidth     原始宽
     * @param outHeight    原始高
     * @param targetWidth  目标宽
     * @param targetHeight 目标高
     * @param supportLargeImage 是否支持大图，大图时会有特殊处理
     * @return 合适的InSampleSize
     */
    public int calculateInSampleSize(int outWidth, int outHeight, int targetWidth, int targetHeight, boolean supportLargeImage) {
        targetWidth *= targetSizeScale;
        targetHeight *= targetSizeScale;

        // 限制target宽高不能大于OpenGL所允许的最大尺寸
        int maxSize = getOpenGLMaxTextureSize();
        if (targetWidth > maxSize) {
            targetWidth = maxSize;
        }
        if (targetHeight > maxSize) {
            targetHeight = maxSize;
        }

        int inSampleSize = 1;

        // 如果目标宽高都小于等于0，就别计算了
        if (targetWidth <= 0 && targetHeight <= 0) {
            return inSampleSize;
        }

        // 如果目标宽高都大于等于原始尺寸，也别计算了
        if (targetWidth >= outWidth && targetHeight >= outHeight) {
            return inSampleSize;
        }

        if (targetWidth <= 0) {
            // 目标宽小于等于0时，只要高度满足要求即可
            while (SketchUtils.ceil(outHeight, inSampleSize) > targetHeight) {
                inSampleSize *= 2;
            }
        } else if (targetHeight <= 0) {
            // 目标高小于等于0时，只要宽度满足要求即可
            while (SketchUtils.ceil(outWidth, inSampleSize) > targetWidth) {
                inSampleSize *= 2;
            }
        } else {
            // 首先限制像素数不能超过目标宽高的像素数
            final long maxPixels = targetWidth * targetHeight;
            while ((SketchUtils.ceil(outWidth, inSampleSize)) * (SketchUtils.ceil(outHeight, inSampleSize)) > maxPixels) {
                inSampleSize *= 2;
            }

            // 然后限制宽高不能大于OpenGL所允许的最大尺寸
            while (SketchUtils.ceil(outWidth, inSampleSize) > maxSize || SketchUtils.ceil(outHeight, inSampleSize) > maxSize) {
                inSampleSize *= 2;
            }

            // 最后如果是为大图功能加载预览图的话，当缩小2倍的话为了节省内存考虑还不如缩小4倍（缩小1倍时不会启用大图功能，因此无需处理）
            if (supportLargeImage && inSampleSize == 2) {
                inSampleSize = 4;
            }
        }

        return inSampleSize;
    }

    /**
     * 根据高度计算是否可以使用阅读模式
     */
    public boolean canUseReadModeByHeight(int imageWidth, int imageHeight){
        return imageHeight > imageWidth * 2;
    }

    /**
     * 根据宽度度计算是否可以使用阅读模式
     */
    public boolean canUseReadModeByWidth(int imageWidth, int imageHeight){
        return imageWidth > imageHeight * 3;
    }

    /**
     * 是否可以使用缩略图模式
     */
    public boolean canUseThumbnailMode(int outWidth, int outHeight, int resizeWidth, int resizeHeight){
        if (resizeWidth > outWidth && resizeHeight > outHeight) {
            return false;
        }

        float resizeScale = (float) resizeWidth / resizeHeight;
        float imageScale = (float) outWidth / outHeight;
        return Math.max(resizeScale, imageScale) > Math.min(resizeScale, imageScale) * 1.5f;
    }

    @SuppressWarnings("unused")
    public float getTargetSizeScale() {
        return targetSizeScale;
    }

    /**
     * 计算inSampleSize的时候将targetSize稍微放大一点儿，就是乘以这个倍数，默认值是1.25f
     *
     * @param targetSizeScale 将targetSize稍微放大一点儿
     */
    @SuppressWarnings("unused")
    public void setTargetSizeScale(float targetSizeScale) {
        this.targetSizeScale = targetSizeScale;
    }

    @Override
    public String getKey() {
        return logName;
    }
}
