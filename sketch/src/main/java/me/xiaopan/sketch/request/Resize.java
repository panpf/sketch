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
 * 将图片加载到内存中之后根据resize进行修正
 * <p>
 * 修正的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸小于等于resize，如果需要必须同resize一致可以设置 {@link LoadOptions#forceUseResize}
 */
public class Resize implements Identifier {
    private int width;
    private int height;

    /**
     * 裁剪图片时scaleType将决定如何裁剪，原理同ImageView的scaleType相同
     */
    private ImageView.ScaleType scaleType;

    public Resize(Resize sourceResize) {
        this.width = sourceResize.width;
        this.height = sourceResize.height;
        this.scaleType = sourceResize.scaleType;
    }

    public Resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Resize(int width, int height, ImageView.ScaleType scaleType) {
        this(width, height);
        this.scaleType = scaleType;
    }

    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Resize) {
            Resize other = (Resize) obj;
            return width == other.width && height == other.height && scaleType == other.scaleType;
        }
        return false;
    }

    @Override
    public String getKey() {
        return String.format("Resize(%dx%d-%s)", width, height, scaleType != null ? scaleType.name() : "null");
    }
}
