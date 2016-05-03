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

package me.xiaopan.sketch.decode;

import android.graphics.Bitmap;

import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.drawable.RecycleGifDrawable;

public class DecodeResult {
    private Bitmap bitmap;
    private RecycleGifDrawable gifDrawable;
    private String mimeType;
    private ImageFrom imageFrom;

    public DecodeResult(String mimeType, RecycleGifDrawable gifDrawable) {
        this.mimeType = mimeType;
        this.gifDrawable = gifDrawable;
    }

    public DecodeResult(String mimeType, Bitmap bitmap) {
        this.mimeType = mimeType;
        this.bitmap = bitmap;
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

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public RecycleGifDrawable getGifDrawable() {
        return gifDrawable;
    }
}
