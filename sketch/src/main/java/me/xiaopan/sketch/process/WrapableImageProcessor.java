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

package me.xiaopan.sketch.process;

import android.graphics.Bitmap;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.request.Resize;

/**
 * 用于组合两个ImageProcessor一起使用
 */
public abstract class WrapableImageProcessor extends ResizeImageProcessor {
    private WrapableImageProcessor wrappedProcessor;

    public WrapableImageProcessor(WrapableImageProcessor wrappedProcessor) {
        this.wrappedProcessor = wrappedProcessor;
    }

    @Override
    public final String getIdentifier() {
        if (wrappedProcessor != null) {
            return onGetIdentifier() + "&" + wrappedProcessor.getIdentifier();
        } else {
            return onGetIdentifier();
        }
    }

    public abstract String onGetIdentifier();

    protected boolean isInterceptResize() {
        return false;
    }

    @Override
    public final Bitmap process(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize,
                                boolean lowQualityImage) {
        if (bitmap == null || bitmap.isRecycled()) {
            return bitmap;
        }

        // resize
        Bitmap newBitmap = bitmap;
        if (!isInterceptResize()) {
            newBitmap = super.process(sketch, bitmap, resize, forceUseResize, lowQualityImage);
        }

        // wrapped
        if (wrappedProcessor != null) {
            Bitmap wrappedBitmap = wrappedProcessor.process(sketch, newBitmap, resize, forceUseResize, lowQualityImage);
            if (wrappedBitmap != null && wrappedBitmap != newBitmap) {
                if (newBitmap != bitmap) {
                    BitmapPool bitmapPool = sketch.getConfiguration().getBitmapPool();
                    BitmapPoolUtils.freeBitmapToPool(newBitmap, bitmapPool);
                }
                newBitmap = wrappedBitmap;
            }
        }
        return onProcess(sketch, newBitmap, resize, forceUseResize, lowQualityImage);
    }

    public abstract Bitmap onProcess(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize,
                                     boolean lowQualityImage);
}
