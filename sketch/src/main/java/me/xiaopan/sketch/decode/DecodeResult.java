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

import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.request.ImageFrom;

// TODO: 2017/5/6 结果抽象化
public class DecodeResult {
    private Bitmap bitmap;
    private SketchGifDrawable gifDrawable;

    private ImageAttrs imageAttrs;

    private ImageFrom imageFrom;
    private boolean processed;
    private boolean banProcess;

    public DecodeResult(ImageAttrs imageAttrs, SketchGifDrawable gifDrawable) {
        this.imageAttrs = imageAttrs;
        this.gifDrawable = gifDrawable;
    }

    public DecodeResult(ImageAttrs imageAttrs, Bitmap bitmap) {
        this.imageAttrs = imageAttrs;
        this.bitmap = bitmap;
    }

    public boolean isBanProcess() {
        return banProcess;
    }

    public DecodeResult setBanProcess(boolean banProcess) {
        this.banProcess = banProcess;
        return this;
    }

    public ImageAttrs getImageAttrs() {
        return imageAttrs;
    }

    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    public void setImageFrom(ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public SketchGifDrawable getGifDrawable() {
        return gifDrawable;
    }

    public boolean isProcessed() {
        return processed;
    }

    public DecodeResult setProcessed(boolean processed) {
        this.processed = processed;
        return this;
    }

    public void recycle(BitmapPool bitmapPool) {
        if (bitmap != null) {
            BitmapPoolUtils.freeBitmapToPool(bitmap, bitmapPool);
        }

        if (gifDrawable != null) {
            gifDrawable.recycle();
        }
    }
}
