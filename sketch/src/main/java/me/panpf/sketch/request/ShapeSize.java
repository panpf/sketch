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

package me.panpf.sketch.request;

import androidx.annotation.NonNull;
import android.widget.ImageView;

import me.panpf.sketch.drawable.SketchShapeBitmapDrawable;

/**
 * 用来搭配 {@link SketchShapeBitmapDrawable} 在绘制时修改图片的尺寸，用来替代大多数情况下对 {@link Resize} 的依赖
 * <p>
 * 当多张图片的 inSampleSize 一样，那么读到内存里的 {@link android.graphics.Bitmap} 尺寸就一样，但是因为 {@link Resize} 不一样，导致会产生多个差别很小的 {@link android.graphics.Bitmap}，这样就降低了内存缓存利用率
 * <p>
 * 当使用 {@link ShapeSize} 时，就可以使用同一个 {@link android.graphics.Bitmap} 在绘制时显示出不同的尺寸，避免了产生多个差别很小的 {@link android.graphics.Bitmap}，提高了内存缓存利用率
 */
public class ShapeSize {
    private int width;
    private int height;
    private ImageView.ScaleType scaleType;

    public ShapeSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public ShapeSize(int width, int height, ImageView.ScaleType scaleType) {
        this.width = width;
        this.height = height;
        this.scaleType = scaleType;
    }

    private ShapeSize() {
    }

    /**
     * 使用 {@link ImageView} 的固定尺寸作为 {@link ShapeSize}
     */
    @SuppressWarnings("unused")
    public static ShapeSize byViewFixedSize() {
        return ByViewFixedSizeShapeSize.INSTANCE;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    void setScaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof ShapeSize) {
            ShapeSize other = (ShapeSize) obj;
            return width == other.width && height == other.height;
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("ShapeSize(%dx%d)", width, height);
    }

    /**
     * 使用 {@link ImageView} 的固定尺寸作为 {@link ShapeSize}
     */
    static class ByViewFixedSizeShapeSize extends ShapeSize {
        static final ByViewFixedSizeShapeSize INSTANCE = new ByViewFixedSizeShapeSize();
    }
}
