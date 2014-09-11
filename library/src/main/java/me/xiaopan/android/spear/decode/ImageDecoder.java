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

package me.xiaopan.android.spear.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import java.io.IOException;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.util.ImageSize;

/**
 * 位图解码器
 */
public interface ImageDecoder {
	/**
	 * 解码
	 * @param spear Spear
     * @param maxsize 最大尺寸
	 * @param decodeListener 回调解码
	 */
	public Bitmap decode(Spear spear, ImageSize maxsize,  DecodeListener decodeListener) throws IOException;

    /**
     * 解码监听器
     */
    public interface DecodeListener {
        /**
         * 解码
         * @param options 解码选项
         */
        public Bitmap onDecode(BitmapFactory.Options options);

        /**
         * 解码成功
         */
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize);

        /**
         * 解码失败
         */
        public void onDecodeFailure();
    }
}
