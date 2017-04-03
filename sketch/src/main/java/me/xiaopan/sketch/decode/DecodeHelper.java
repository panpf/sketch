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
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.io.IOException;
import java.io.InputStream;

import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.drawable.SketchGifDrawable;

/**
 * 解码监听器
 */
public interface DecodeHelper {
    /**
     * 解码
     *
     * @param options 解码选项
     */
    Bitmap decode(BitmapFactory.Options options);

    /**
     * 解码碎片
     *
     * @param srcRect 解码区域
     * @param options 解码选项
     */
    Bitmap decodeRegion(Rect srcRect, BitmapFactory.Options options);

    /**
     * 解码成功
     */
    void onDecodeSuccess(Bitmap bitmap, int outWidth, int outHeight, String outMimeType, int inSampleSize);

    /**
     * 解码失败
     */
    void onDecodeError();

    /**
     * 创建GifDrawable
     */
    SketchGifDrawable makeGifDrawable(String key, String uri, ImageAttrs imageAttrs, BitmapPool bitmapPool);

    /**
     * 获取输入流
     */
    InputStream getInputStream() throws IOException;
}
