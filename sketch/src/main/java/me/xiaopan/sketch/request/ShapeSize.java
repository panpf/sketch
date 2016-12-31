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

package me.xiaopan.sketch.request;

import android.widget.ImageView;

import me.xiaopan.sketch.Identifier;

/**
 * 用来搭配 {@link me.xiaopan.sketch.drawable.ShapeBitmapDrawable} 在绘制时修改图片的尺寸，用来替代大多数情况下对resize的依赖
 * <p>
 * 当多张图片的inSampleSize一样，那么读到内存里的bitmap尺寸就一样，但是因为resize不一样，导致会产生多个差别很小的bitmap，这样就降低了内存缓存利用率
 * <p>
 * 当使用shape size时，就可以使用同一个bitmap在绘制时显示出不同的尺寸，避免了产生多个差别很小的bitmap，提高了内存缓存利用率
 */
public class ShapeSize implements Identifier {
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
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

    @Override
    public String getKey() {
        return String.format("ShapeSize(%dx%d)", width, height);
    }
}
