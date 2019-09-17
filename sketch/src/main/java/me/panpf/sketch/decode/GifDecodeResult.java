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

import androidx.annotation.NonNull;

import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.drawable.SketchGifDrawable;
import me.panpf.sketch.request.ImageFrom;

public class GifDecodeResult implements DecodeResult {
    @NonNull
    private SketchGifDrawable gifDrawable;
    @NonNull
    private ImageAttrs imageAttrs;
    @NonNull
    private ImageFrom imageFrom;

    private boolean banProcess;
    private boolean processed;

    public GifDecodeResult(@NonNull SketchGifDrawable gifDrawable, @NonNull ImageAttrs imageAttrs, @NonNull ImageFrom imageFrom) {
        this.gifDrawable = gifDrawable;
        this.imageAttrs = imageAttrs;
        this.imageFrom = imageFrom;
    }

    @NonNull
    @Override
    public ImageAttrs getImageAttrs() {
        return imageAttrs;
    }

    @NonNull
    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    @Override
    public boolean isBanProcess() {
        return banProcess;
    }

    @NonNull
    @Override
    public GifDecodeResult setBanProcess(boolean banProcess) {
        this.banProcess = banProcess;
        return this;
    }

    @Override
    public boolean isProcessed() {
        return processed;
    }

    @NonNull
    @Override
    public GifDecodeResult setProcessed(boolean processed) {
        this.processed = processed;
        return this;
    }

    @NonNull
    public SketchGifDrawable getGifDrawable() {
        return gifDrawable;
    }

    @Override
    public void recycle(@NonNull BitmapPool bitmapPool) {
        gifDrawable.recycle();
    }
}
