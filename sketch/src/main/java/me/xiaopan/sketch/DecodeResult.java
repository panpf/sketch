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

package me.xiaopan.sketch;

import android.graphics.Bitmap;

public class DecodeResult {
    private Bitmap resultBitmap;
    private RecycleGifDrawable resultGifDrawable;
    private String mimeType;
    private ImageFrom imageFrom;

    public DecodeResult(String mimeType, RecycleGifDrawable resultGifDrawable) {
        this.mimeType = mimeType;
        this.resultGifDrawable = resultGifDrawable;
    }

    public DecodeResult(String mimeType, Bitmap resultBitmap) {
        this.mimeType = mimeType;
        this.resultBitmap = resultBitmap;
    }

    public void setImageFrom(ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
    }

    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Bitmap getResultBitmap() {
        return resultBitmap;
    }

    public RecycleGifDrawable getResultGifDrawable() {
        return resultGifDrawable;
    }
}
