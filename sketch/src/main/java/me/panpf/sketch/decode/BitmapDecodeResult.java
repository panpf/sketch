/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.decode;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;

import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.cache.BitmapPoolUtils;
import me.panpf.sketch.request.ImageFrom;

public class BitmapDecodeResult implements DecodeResult {
    private Bitmap bitmap;
    private ImageAttrs imageAttrs;
    private ImageFrom imageFrom;

    private boolean banProcess;
    private boolean processed;

    public BitmapDecodeResult(@NonNull ImageAttrs imageAttrs, @NonNull Bitmap bitmap) {
        this.imageAttrs = imageAttrs;
        this.bitmap = bitmap;
    }

    @Override
    public ImageAttrs getImageAttrs() {
        return imageAttrs;
    }

    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    @Override
    public void setImageFrom(ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
    }

    @Override
    public boolean isBanProcess() {
        return banProcess;
    }

    @Override
    public BitmapDecodeResult setBanProcess(boolean banProcess) {
        this.banProcess = banProcess;
        return this;
    }

    @Override
    public boolean isProcessed() {
        return processed;
    }

    @Override
    public BitmapDecodeResult setProcessed(boolean processed) {
        this.processed = processed;
        return this;
    }

    @Override
    public void recycle(BitmapPool bitmapPool) {
        if (bitmap != null) {
            BitmapPoolUtils.freeBitmapToPool(bitmap, bitmapPool);
        }
    }

    @NonNull
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
