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

package me.xiaopan.sketch.request;

import android.content.Context;
import android.graphics.drawable.Drawable;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.MemoryCache;
import me.xiaopan.sketch.drawable.ShapeBitmapDrawable;
import me.xiaopan.sketch.drawable.RefBitmap;
import me.xiaopan.sketch.drawable.RefBitmapDrawable;
import me.xiaopan.sketch.shaper.ImageShaper;

/**
 * 从内存中获取图片作为占位图，支持ShapeSize和ImageShaper
 */
@SuppressWarnings("unused")
public class MemoryCacheStateImage implements StateImage {
    private String memoryCacheId;
    private StateImage whenEmptyImage;

    public MemoryCacheStateImage(String memoryCacheId, StateImage whenEmptyImage) {
        this.memoryCacheId = memoryCacheId;
        this.whenEmptyImage = whenEmptyImage;
    }

    @Override
    public Drawable getDrawable(Context context, DisplayOptions displayOptions) {
        MemoryCache memoryCache = Sketch.with(context).getConfiguration().getMemoryCache();
        RefBitmap cachedRefBitmap = memoryCache.get(memoryCacheId);
        if (cachedRefBitmap != null) {
            if (cachedRefBitmap.isRecycled()) {
                memoryCache.remove(memoryCacheId);
            } else {
                RefBitmapDrawable bitmapDrawable = new RefBitmapDrawable(cachedRefBitmap);
                ShapeSize shapeSize = displayOptions.getShapeSize();
                ImageShaper imageShaper = displayOptions.getImageShaper();
                if (shapeSize != null || imageShaper != null) {
                    return new ShapeBitmapDrawable(bitmapDrawable, shapeSize, imageShaper);
                } else {
                    return bitmapDrawable;
                }
            }
        }

        return whenEmptyImage != null ? whenEmptyImage.getDrawable(context, displayOptions) : null;
    }

    public String getMemoryCacheId() {
        return memoryCacheId;
    }

    public StateImage getWhenEmptyImage() {
        return whenEmptyImage;
    }
}
