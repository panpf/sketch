/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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
import androidx.annotation.Nullable;

import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.cache.BitmapPoolUtils;
import me.panpf.sketch.request.ImageFrom;

public class BitmapDecodeResult implements DecodeResult {

    @NonNull
    private Bitmap bitmap;
    @NonNull
    private ImageAttrs imageAttrs;
    @Nullable
    private ImageFrom imageFrom;

    private boolean banProcess;
    private boolean processed;

    BitmapDecodeResult(@NonNull ImageAttrs imageAttrs, @NonNull Bitmap bitmap) {
        this.imageAttrs = imageAttrs;
        this.bitmap = bitmap;
    }

    @NonNull
    @Override
    public ImageAttrs getImageAttrs() {
        return imageAttrs;
    }

    @Nullable
    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    @Override
    public void setImageFrom(@NonNull ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
    }

    @Override
    public boolean isBanProcess() {
        return banProcess;
    }

    @NonNull
    @Override
    public BitmapDecodeResult setBanProcess(boolean banProcess) {
        this.banProcess = banProcess;
        return this;
    }

    @Override
    public boolean isProcessed() {
        return processed;
    }

    @NonNull
    @Override
    public BitmapDecodeResult setProcessed(boolean processed) {
        this.processed = processed;
        return this;
    }

    @Override
    public void recycle(@NonNull BitmapPool bitmapPool) {
        BitmapPoolUtils.freeBitmapToPool(bitmap, bitmapPool);
    }

    @NonNull
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(@NonNull Bitmap bitmap) {
        //noinspection ConstantConditions
        if (bitmap != null) {
            this.bitmap = bitmap;
        }
    }
}
