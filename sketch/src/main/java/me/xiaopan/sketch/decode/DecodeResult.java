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

package me.xiaopan.sketch.decode;

import android.graphics.Bitmap;

import me.xiaopan.sketch.drawable.SketchGifDrawableImpl;
import me.xiaopan.sketch.request.ImageFrom;

public class DecodeResult {
    private Bitmap bitmap;
    private SketchGifDrawableImpl gifDrawable;

    private String mimeType;
    private int originWidth;
    private int originHeight;

    private ImageFrom imageFrom;
    private boolean canCacheInDiskCache;

    public DecodeResult(int originWidth, int originHeight, String mimeType, SketchGifDrawableImpl gifDrawable) {
        this.originWidth = originWidth;
        this.originHeight = originHeight;
        this.mimeType = mimeType;
        this.gifDrawable = gifDrawable;
    }

    public DecodeResult(int originWidth, int originHeight, String mimeType, Bitmap bitmap) {
        this.originWidth = originWidth;
        this.originHeight = originHeight;
        this.mimeType = mimeType;
        this.bitmap = bitmap;
    }

    public void setImageFrom(ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
    }

    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public SketchGifDrawableImpl getGifDrawable() {
        return gifDrawable;
    }

    public int getOriginHeight() {
        return originHeight;
    }

    public int getOriginWidth() {
        return originWidth;
    }

    public void setOriginHeight(int originHeight) {
        this.originHeight = originHeight;
    }

    public void setOriginWidth(int originWidth) {
        this.originWidth = originWidth;
    }

    public boolean isCanCacheInDiskCache() {
        return canCacheInDiskCache;
    }

    public DecodeResult setCanCacheInDiskCache(boolean canCacheInDiskCache) {
        this.canCacheInDiskCache = canCacheInDiskCache;
        return this;
    }
}
