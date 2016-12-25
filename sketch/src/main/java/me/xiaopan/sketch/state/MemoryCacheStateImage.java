/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.state;

import android.content.Context;
import android.graphics.drawable.Drawable;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.MemoryCache;
import me.xiaopan.sketch.drawable.RefBitmap;
import me.xiaopan.sketch.drawable.RefBitmapDrawable;
import me.xiaopan.sketch.drawable.ShapeBitmapDrawable;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.ImageViewInterface;
import me.xiaopan.sketch.request.ShapeSize;
import me.xiaopan.sketch.shaper.ImageShaper;

/**
 * 从内存中获取图片作为状态图片，支持ShapeSize和ImageShaper
 */
@SuppressWarnings("unused")
public class MemoryCacheStateImage implements StateImage {
    private String memoryCacheKey;
    private StateImage whenEmptyImage;

    public MemoryCacheStateImage(String memoryCacheKey, StateImage whenEmptyImage) {
        this.memoryCacheKey = memoryCacheKey;
        this.whenEmptyImage = whenEmptyImage;
    }

    @Override
    public Drawable getDrawable(Context context, ImageViewInterface imageViewInterface, DisplayOptions displayOptions) {
        MemoryCache memoryCache = Sketch.with(context).getConfiguration().getMemoryCache();
        RefBitmap cachedRefBitmap = memoryCache.get(memoryCacheKey);
        if (cachedRefBitmap != null) {
            if (cachedRefBitmap.isRecycled()) {
                memoryCache.remove(memoryCacheKey);
            } else {
                RefBitmapDrawable bitmapDrawable = new RefBitmapDrawable(cachedRefBitmap);
                ShapeSize shapeSize = displayOptions.getShapeSize();
                ImageShaper imageShaper = displayOptions.getImageShaper();
                if (shapeSize != null || imageShaper != null) {
                    return new ShapeBitmapDrawable(context, bitmapDrawable, shapeSize, imageShaper);
                } else {
                    return bitmapDrawable;
                }
            }
        }

        return whenEmptyImage != null ? whenEmptyImage.getDrawable(context, imageViewInterface, displayOptions) : null;
    }

    public String getMemoryCacheKey() {
        return memoryCacheKey;
    }

    public StateImage getWhenEmptyImage() {
        return whenEmptyImage;
    }
}
