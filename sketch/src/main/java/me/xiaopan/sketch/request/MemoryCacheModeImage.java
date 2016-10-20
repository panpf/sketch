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
import me.xiaopan.sketch.drawable.FixedSizeRefBitmapDrawable;
import me.xiaopan.sketch.drawable.RefBitmap;
import me.xiaopan.sketch.drawable.RefBitmapDrawable;

@SuppressWarnings("unused")
public class MemoryCacheModeImage implements ModeImage {
    private String memoryCacheId;
    private ModeImage defaultModeImage;

    public MemoryCacheModeImage(String memoryCacheId, ModeImage defaultModeImage) {
        this.memoryCacheId = memoryCacheId;
        this.defaultModeImage = defaultModeImage;
    }

    @Override
    public Drawable getDrawable(Context context, FixedSize fixedSize) {
        MemoryCache memoryCache = Sketch.with(context).getConfiguration().getMemoryCache();
        RefBitmap cachedRefBitmap = memoryCache.get(memoryCacheId);
        if (cachedRefBitmap != null) {
            if (cachedRefBitmap.isRecycled()) {
                memoryCache.remove(memoryCacheId);
            } else {
                RefBitmapDrawable refBitmapDrawable = new RefBitmapDrawable(cachedRefBitmap);
                if (fixedSize != null) {
                    return new FixedSizeRefBitmapDrawable(refBitmapDrawable, fixedSize);
                } else {
                    return refBitmapDrawable;
                }
            }
        }

        return defaultModeImage != null ? defaultModeImage.getDrawable(context, fixedSize) : null;
    }

    public String getMemoryCacheId() {
        return memoryCacheId;
    }

    public ModeImage getDefaultModeImage() {
        return defaultModeImage;
    }
}
