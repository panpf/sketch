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

package me.panpf.sketch.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.decode.ResizeCalculator;
import me.panpf.sketch.request.Resize;

public class ResizeImageProcessor implements ImageProcessor {

    @NonNull
    @Override
    public Bitmap process(@NonNull Sketch sketch, @NonNull Bitmap bitmap, @Nullable Resize resize, boolean lowQualityImage) {
        if (bitmap.isRecycled()) {
            return bitmap;
        }

        if (resize == null || resize.getWidth() == 0 || resize.getHeight() == 0 ||
                (bitmap.getWidth() == resize.getWidth() && bitmap.getHeight() == resize.getHeight())) {
            return bitmap;
        }

        ResizeCalculator resizeCalculator = sketch.getConfiguration().getResizeCalculator();
        ResizeCalculator.Mapping mapping = resizeCalculator.calculator(bitmap.getWidth(), bitmap.getHeight(),
                resize.getWidth(), resize.getHeight(), resize.getScaleType(), resize.getMode() == Resize.Mode.EXACTLY_SAME);
        if (mapping == null) {
            return bitmap;
        }

        Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            config = lowQualityImage ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ARGB_8888;
        }
        BitmapPool bitmapPool = sketch.getConfiguration().getBitmapPool();

        Bitmap resizeBitmap = bitmapPool.getOrMake(mapping.imageWidth, mapping.imageHeight, config);

        Canvas canvas = new Canvas(resizeBitmap);
        canvas.drawBitmap(bitmap, mapping.srcRect, mapping.destRect, null);

        return resizeBitmap;
    }

    @NonNull
    @Override
    public String toString() {
        return "ResizeImageProcessor";
    }

    @Nullable
    @Override
    public String getKey() {
        return "Resize";
    }
}
