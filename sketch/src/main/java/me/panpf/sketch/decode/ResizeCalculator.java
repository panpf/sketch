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

import android.graphics.Rect;
import androidx.annotation.NonNull;
import android.widget.ImageView;

import me.panpf.sketch.request.Resize;

/**
 * 用来计算 {@link Resize}
 */
public class ResizeCalculator {
    private static final String KEY = "ResizeCalculator";

    public static Rect srcMappingStartRect(int originalImageWidth, int originalImageHeight, int targetImageWidth, int targetImageHeight) {
        float widthScale = (float) originalImageWidth / targetImageWidth;
        float heightScale = (float) originalImageHeight / targetImageHeight;
        float finalScale = widthScale < heightScale ? widthScale : heightScale;
        int srcWidth = (int) (targetImageWidth * finalScale);
        int srcHeight = (int) (targetImageHeight * finalScale);
        int srcLeft = 0;
        int srcTop = 0;
        return new Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight);
    }

    public static Rect srcMappingCenterRect(int originalImageWidth, int originalImageHeight, int targetImageWidth, int targetImageHeight) {
        float widthScale = (float) originalImageWidth / targetImageWidth;
        float heightScale = (float) originalImageHeight / targetImageHeight;
        float finalScale = widthScale < heightScale ? widthScale : heightScale;
        int srcWidth = (int) (targetImageWidth * finalScale);
        int srcHeight = (int) (targetImageHeight * finalScale);
        int srcLeft = (originalImageWidth - srcWidth) / 2;
        int srcTop = (originalImageHeight - srcHeight) / 2;
        return new Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight);
    }

    public static Rect srcMappingEndRect(int originalImageWidth, int originalImageHeight, int targetImageWidth, int targetImageHeight) {
        float widthScale = (float) originalImageWidth / targetImageWidth;
        float heightScale = (float) originalImageHeight / targetImageHeight;
        float finalScale = widthScale < heightScale ? widthScale : heightScale;
        int srcWidth = (int) (targetImageWidth * finalScale);
        int srcHeight = (int) (targetImageHeight * finalScale);

        int srcLeft;
        int srcTop;
        if (originalImageWidth > originalImageHeight) {
            srcLeft = originalImageWidth - srcWidth;
            srcTop = originalImageHeight - srcHeight;
        } else {
            srcLeft = originalImageWidth - srcWidth;
            srcTop = originalImageHeight - srcHeight;
        }
        return new Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight);
    }

    public static Rect srcMatrixRect(int originalImageWidth, int originalImageHeight, int targetImageWidth, int targetImageHeight) {
        if (originalImageWidth > targetImageWidth && originalImageHeight > targetImageHeight) {
            return new Rect(0, 0, targetImageWidth, targetImageHeight);
        } else {
            float scale = targetImageWidth - originalImageWidth > targetImageHeight - originalImageHeight ? (float) targetImageWidth / originalImageWidth : (float) targetImageHeight / originalImageHeight;
            int srcWidth = (int) (targetImageWidth / scale);
            int srcHeight = (int) (targetImageHeight / scale);
            int srcLeft = 0;
            int srcTop = 0;
            return new Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight);
        }
    }

    public static int[] scaleTargetSize(int originalImageWidth, int originalImageHeight, int targetImageWidth, int targetImageHeight) {
        if (targetImageWidth > originalImageWidth || targetImageHeight > originalImageHeight) {
            float scale = Math.abs(targetImageWidth - originalImageWidth) < Math.abs(targetImageHeight - originalImageHeight)
                    ? (float) targetImageWidth / originalImageWidth : (float) targetImageHeight / originalImageHeight;
            targetImageWidth = Math.round(targetImageWidth / scale);
            targetImageHeight = Math.round(targetImageHeight / scale);
        }

        return new int[]{targetImageWidth, targetImageHeight};
    }

    @NonNull
    @Override
    public String toString() {
        return KEY;
    }

    /**
     * 计算
     *
     * @param imageWidth   图片原始宽
     * @param imageHeight  图片原始高
     * @param resizeWidth  目标宽
     * @param resizeHeight 目标高
     * @param scaleType    缩放类型
     * @param exactlySame  强制使新图片的尺寸和 resizeWidth、resizeHeight 一致
     * @return 计算结果
     */
    public Mapping calculator(int imageWidth, int imageHeight, int resizeWidth, int resizeHeight,
                              ImageView.ScaleType scaleType, boolean exactlySame) {
        if (imageWidth == resizeWidth && imageHeight == resizeHeight) {
            Mapping mapping = new Mapping();
            mapping.imageWidth = imageWidth;
            mapping.imageHeight = imageHeight;
            mapping.srcRect = new Rect(0, 0, imageWidth, imageHeight);
            mapping.destRect = mapping.srcRect;
            return mapping;
        }

        if (scaleType == null) {
            scaleType = ImageView.ScaleType.FIT_CENTER;
        }

        int newImageWidth;
        int newImageHeight;
        if (exactlySame) {
            newImageWidth = resizeWidth;
            newImageHeight = resizeHeight;
        } else {
            int[] finalImageSize = scaleTargetSize(imageWidth, imageHeight, resizeWidth, resizeHeight);
            newImageWidth = finalImageSize[0];
            newImageHeight = finalImageSize[1];
        }
        Rect srcRect;
        Rect destRect = new Rect(0, 0, newImageWidth, newImageHeight);
        if (scaleType == ImageView.ScaleType.CENTER || scaleType == ImageView.ScaleType.CENTER_CROP || scaleType == ImageView.ScaleType.CENTER_INSIDE) {
            srcRect = srcMappingCenterRect(imageWidth, imageHeight, newImageWidth, newImageHeight);
        } else if (scaleType == ImageView.ScaleType.FIT_START) {
            srcRect = srcMappingStartRect(imageWidth, imageHeight, newImageWidth, newImageHeight);
        } else if (scaleType == ImageView.ScaleType.FIT_CENTER) {
            srcRect = srcMappingCenterRect(imageWidth, imageHeight, newImageWidth, newImageHeight);
        } else if (scaleType == ImageView.ScaleType.FIT_END) {
            srcRect = srcMappingEndRect(imageWidth, imageHeight, newImageWidth, newImageHeight);
        } else if (scaleType == ImageView.ScaleType.FIT_XY) {
            srcRect = new Rect(0, 0, imageWidth, imageHeight);
        } else if (scaleType == ImageView.ScaleType.MATRIX) {
            srcRect = srcMatrixRect(imageWidth, imageHeight, newImageWidth, newImageHeight);
        } else {
            srcRect = srcMappingCenterRect(imageWidth, imageHeight, newImageWidth, newImageHeight);
        }

        Mapping mapping = new Mapping();
        mapping.imageWidth = newImageWidth;
        mapping.imageHeight = newImageHeight;
        mapping.srcRect = srcRect;
        mapping.destRect = destRect;
        return mapping;
    }

    public static class Mapping {
        public int imageWidth;
        public int imageHeight;
        public Rect srcRect;
        public Rect destRect;
    }
}
