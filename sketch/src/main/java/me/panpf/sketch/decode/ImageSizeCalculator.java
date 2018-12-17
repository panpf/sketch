/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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


package me.panpf.sketch.decode;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;

import me.panpf.sketch.SketchView;
import me.panpf.sketch.request.DisplayRequest;
import me.panpf.sketch.request.FixedSize;
import me.panpf.sketch.request.LoadRequest;
import me.panpf.sketch.request.MaxSize;
import me.panpf.sketch.util.SketchUtils;

/**
 * 和图片尺寸相关的需求的计算器
 */
public class ImageSizeCalculator {
    private static final String KEY = "ImageSizeCalculator";

    private int openGLMaxTextureSize = -1;
    private float targetSizeScale = 1.1f;

    private static int getWidth(SketchView sketchView, boolean checkMaxWidth, boolean acceptWrapContent, boolean subtractPadding) {
        if (sketchView == null) {
            return 0;
        }

        int width = 0;
        final ViewGroup.LayoutParams params = sketchView.getLayoutParams();
        if (params != null) {
            width = params.width;
            if (subtractPadding && width > 0 && (width - sketchView.getPaddingLeft() - sketchView.getPaddingRight()) > 0) {
                width -= sketchView.getPaddingLeft() + sketchView.getPaddingRight();
                return width;
            }
        }
        if (width <= 0 && checkMaxWidth) {
            width = getViewFieldValue(sketchView, "mMaxWidth");
        }
        if (width <= 0 && acceptWrapContent && params != null && params.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = -1;
        }
        return width;
    }

    private static int getHeight(SketchView sketchView, boolean checkMaxHeight, boolean acceptWrapContent, boolean subtractPadding) {
        if (sketchView == null) {
            return 0;
        }

        int height = 0;
        final ViewGroup.LayoutParams params = sketchView.getLayoutParams();
        if (params != null) {
            height = params.height;
            if (subtractPadding && height > 0 && (height - sketchView.getPaddingTop() - sketchView.getPaddingBottom()) > 0) {
                height -= sketchView.getPaddingTop() + sketchView.getPaddingBottom();
                return height;
            }
        }
        if (height <= 0 && checkMaxHeight) {
            height = getViewFieldValue(sketchView, "mMaxHeight");
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
     * 获取 OpenGL 所允许的最大尺寸
     */
    @SuppressWarnings("WeakerAccess")
    public int getOpenGLMaxTextureSize() {
        if (openGLMaxTextureSize == -1) {
            openGLMaxTextureSize = SketchUtils.getOpenGLMaxTextureSize();
        }
        return openGLMaxTextureSize;
    }

    /**
     * 设置 OpenGL 所允许的最大尺寸,用来计算 inSampleSize
     */
    @SuppressWarnings("unused")
    public void setOpenGLMaxTextureSize(int openGLMaxTextureSize) {
        this.openGLMaxTextureSize = openGLMaxTextureSize;
    }

    /**
     * 计算 {@link MaxSize}
     *
     * @param sketchView 你需要根据 {@link ImageView} 的宽高来计算
     * @return {@link MaxSize}
     */
    public MaxSize calculateImageMaxSize(SketchView sketchView) {
        int width = getWidth(sketchView, true, true, false);
        int height = getHeight(sketchView, true, true, false);

        if (width <= 0 && height <= 0) {
            return null;
        }

        // 因为OpenGL对图片的宽高有上限，因此要限制一下，这里就严格一点不能大于屏幕宽高的1.5倍
        DisplayMetrics displayMetrics = sketchView.getResources().getDisplayMetrics();
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
    }

    /**
     * 获取默认的 {@link MaxSize}，默认 {@link MaxSize} 是屏幕宽高的 70%
     *
     * @param context 上下文
     * @return {@link MaxSize}
     */
    public MaxSize getDefaultImageMaxSize(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return new MaxSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    /**
     * 计算 {@link FixedSize}
     *
     * @param sketchView 你需要根据 {@link ImageView} 的宽高来计算
     * @return {@link FixedSize}
     */
    public FixedSize calculateImageFixedSize(SketchView sketchView) {
        ViewGroup.LayoutParams layoutParams = sketchView.getLayoutParams();
        if (layoutParams == null || layoutParams.width <= 0 || layoutParams.height <= 0) {
            return null;
        }

        int fixedWidth = layoutParams.width - (sketchView.getPaddingLeft() + sketchView.getPaddingRight());
        int fixedHeight = layoutParams.height - (sketchView.getPaddingTop() + sketchView.getPaddingBottom());

        // 限制不能超过OpenGL所允许的最大尺寸
        int maxSize = getOpenGLMaxTextureSize();
        if (fixedWidth > maxSize || fixedHeight > maxSize) {
            float finalScale = Math.max((float) fixedWidth / maxSize, (float) fixedHeight / maxSize);

            fixedWidth /= finalScale;
            fixedHeight /= finalScale;
        }
        return new FixedSize(fixedWidth, fixedHeight);
    }

    /**
     * 计算 inSampleSize
     *
     * @param outWidth          原始宽
     * @param outHeight         原始高
     * @param targetWidth       目标宽
     * @param targetHeight      目标高
     * @param smallerThumbnails 是否使用较小的缩略图，当 inSampleSize 为 2 时，强制改为 4
     * @return 合适的 inSampleSize
     */
    public int calculateInSampleSize(int outWidth, int outHeight, int targetWidth, int targetHeight, boolean smallerThumbnails) {
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
            while (SketchUtils.calculateSamplingSize(outHeight, inSampleSize) > targetHeight) {
                inSampleSize *= 2;
            }
        } else if (targetHeight <= 0) {
            // 目标高小于等于0时，只要宽度满足要求即可
            while (SketchUtils.calculateSamplingSize(outWidth, inSampleSize) > targetWidth) {
                inSampleSize *= 2;
            }
        } else {
            // 首先限制像素数不能超过目标宽高的像素数
            final long maxPixels = targetWidth * targetHeight;
            while ((SketchUtils.calculateSamplingSize(outWidth, inSampleSize)) * (SketchUtils.calculateSamplingSize(outHeight, inSampleSize)) > maxPixels) {
                inSampleSize *= 2;
            }

            // 然后限制宽高不能大于OpenGL所允许的最大尺寸
            while (SketchUtils.calculateSamplingSize(outWidth, inSampleSize) > maxSize || SketchUtils.calculateSamplingSize(outHeight, inSampleSize) > maxSize) {
                inSampleSize *= 2;
            }

            // 想要较小的缩略图就将 2 改为 4
            if (smallerThumbnails && inSampleSize == 2) {
                inSampleSize = 4;
            }
        }

        return inSampleSize;
    }

    /**
     * 根据高度计算是否可以使用阅读模式
     */
    public boolean canUseReadModeByHeight(int imageWidth, int imageHeight) {
        return imageHeight > imageWidth * 2;
    }

    /**
     * 根据宽度度计算是否可以使用阅读模式
     */
    public boolean canUseReadModeByWidth(int imageWidth, int imageHeight) {
        return imageWidth > imageHeight * 3;
    }

    /**
     * 是否可以使用缩略图模式
     */
    public boolean canUseThumbnailMode(int outWidth, int outHeight, int resizeWidth, int resizeHeight) {
        if (resizeWidth > outWidth && resizeHeight > outHeight) {
            return false;
        }

        float resizeScale = (float) resizeWidth / resizeHeight;
        float imageScale = (float) outWidth / outHeight;
        return Math.max(resizeScale, imageScale) > Math.min(resizeScale, imageScale) * 1.5f;
    }

    /**
     * 根据请求和图片类型判断是否使用更小的缩略图
     */
    public boolean canUseSmallerThumbnails(LoadRequest loadRequest, ImageType imageType) {
        return loadRequest instanceof DisplayRequest &&
                ((DisplayRequest) loadRequest).getViewInfo().isUseSmallerThumbnails() &&
                SketchUtils.formatSupportBitmapRegionDecoder(imageType);
    }

    @SuppressWarnings("unused")
    public float getTargetSizeScale() {
        return targetSizeScale;
    }

    /**
     * 计算 inSampleSize 的时候将 targetSize 稍微放大一点儿，就是乘以这个倍数，默认值是 1.25f
     *
     * @param targetSizeScale 将 targetSize 稍微放大一点儿
     */
    @SuppressWarnings("unused")
    public void setTargetSizeScale(float targetSizeScale) {
        this.targetSizeScale = targetSizeScale;
    }

    @NonNull
    @Override
    public String toString() {
        return KEY;
    }
}
