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

package me.panpf.sketch.request;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.decode.DecodeResult;
import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.drawable.SketchGifDrawable;

@SuppressWarnings("WeakerAccess")
public class LoadResult {
    @Nullable
    private Bitmap bitmap;
    @Nullable
    private SketchGifDrawable gifDrawable;
    @NonNull
    private ImageFrom imageFrom;
    @NonNull
    private ImageAttrs imageAttrs;

    public LoadResult(@NonNull Bitmap bitmap, @NonNull DecodeResult decodeResult) {
        this.bitmap = bitmap;

        this.imageAttrs = decodeResult.getImageAttrs();
        this.imageFrom = decodeResult.getImageFrom();
    }

    public LoadResult(@NonNull SketchGifDrawable gifDrawable, @NonNull DecodeResult decodeResult) {
        this.gifDrawable = gifDrawable;

        this.imageAttrs = decodeResult.getImageAttrs();
        this.imageFrom = decodeResult.getImageFrom();
    }

    @Nullable
    public Bitmap getBitmap() {
        return bitmap;
    }

    @Nullable
    public SketchGifDrawable getGifDrawable() {
        return gifDrawable;
    }

    @NonNull
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    @NonNull
    public ImageAttrs getImageAttrs() {
        return imageAttrs;
    }
}
