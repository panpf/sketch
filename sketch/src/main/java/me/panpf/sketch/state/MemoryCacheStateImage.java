/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.state;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchView;
import me.panpf.sketch.cache.MemoryCache;
import me.panpf.sketch.drawable.SketchBitmapDrawable;
import me.panpf.sketch.drawable.SketchRefBitmap;
import me.panpf.sketch.drawable.SketchShapeBitmapDrawable;
import me.panpf.sketch.request.DisplayOptions;
import me.panpf.sketch.request.ImageFrom;
import me.panpf.sketch.request.ShapeSize;
import me.panpf.sketch.shaper.ImageShaper;

/**
 * 从内存缓存中获取图片作为状态图片，支持 {@link ShapeSize} 和 {@link ImageShaper}
 */
@SuppressWarnings("unused")
public class MemoryCacheStateImage implements StateImage {
    private String memoryCacheKey;
    private StateImage whenEmptyImage;

    public MemoryCacheStateImage(String memoryCacheKey, StateImage whenEmptyImage) {
        this.memoryCacheKey = memoryCacheKey;
        this.whenEmptyImage = whenEmptyImage;
    }

    @Nullable
    @Override
    public Drawable getDrawable(@NonNull Context context, @NonNull SketchView sketchView, @NonNull DisplayOptions displayOptions) {
        MemoryCache memoryCache = Sketch.with(context).getConfiguration().getMemoryCache();
        SketchRefBitmap cachedRefBitmap = memoryCache.get(memoryCacheKey);
        if (cachedRefBitmap != null) {
            if (cachedRefBitmap.isRecycled()) {
                memoryCache.remove(memoryCacheKey);
            } else {
                SketchBitmapDrawable bitmapDrawable = new SketchBitmapDrawable(cachedRefBitmap, ImageFrom.MEMORY_CACHE);
                ShapeSize shapeSize = displayOptions.getShapeSize();
                ImageShaper imageShaper = displayOptions.getShaper();
                if (shapeSize != null || imageShaper != null) {
                    return new SketchShapeBitmapDrawable(context, bitmapDrawable, shapeSize, imageShaper);
                } else {
                    return bitmapDrawable;
                }
            }
        }

        return whenEmptyImage != null ? whenEmptyImage.getDrawable(context, sketchView, displayOptions) : null;
    }

    public String getMemoryCacheKey() {
        return memoryCacheKey;
    }

    public StateImage getWhenEmptyImage() {
        return whenEmptyImage;
    }
}
