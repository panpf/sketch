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

package me.panpf.sketch.request;

import android.graphics.Bitmap;

import me.panpf.sketch.decode.DecodeResult;
import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.drawable.SketchGifDrawable;

public class LoadResult {
    private Bitmap bitmap;
    private SketchGifDrawable gifDrawable;
    private ImageFrom imageFrom;
    private ImageAttrs imageAttrs;

    public LoadResult(Bitmap bitmap, DecodeResult decodeResult) {
        this.bitmap = bitmap;

        this.imageAttrs = decodeResult.getImageAttrs();
        this.imageFrom = decodeResult.getImageFrom();
    }

    public LoadResult(SketchGifDrawable gifDrawable, DecodeResult decodeResult) {
        this.gifDrawable = gifDrawable;

        this.imageAttrs = decodeResult.getImageAttrs();
        this.imageFrom = decodeResult.getImageFrom();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public SketchGifDrawable getGifDrawable() {
        return gifDrawable;
    }

    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    public ImageAttrs getImageAttrs() {
        return imageAttrs;
    }
}
