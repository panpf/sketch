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

package me.panpf.sketch.decode;

import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.request.ImageFrom;

/**
 * 解码结果
 */
public interface DecodeResult {

    /**
     * 获取图片的属性
     *
     * @return {@link ImageAttrs}
     */
    ImageAttrs getImageAttrs();

    /**
     * 获取图片的来源
     *
     * @return {@link ImageFrom}
     */
    ImageFrom getImageFrom();

    /**
     * 设置图片的来源
     *
     * @param imageFrom {@link ImageFrom}
     */
    void setImageFrom(ImageFrom imageFrom);

    /**
     * 是否禁止对图片进行后期处理
     */
    boolean isBanProcess();

    /**
     * 设置是否禁止对图片进行后期处理
     *
     * @param banProcess 是否禁止对图片进行后期处理
     * @return {@link DecodeResult}
     */
    DecodeResult setBanProcess(boolean banProcess);

    /**
     * 是否经过了后期处理
     */
    boolean isProcessed();

    /**
     * 设置是否经过了后期处理
     *
     * @param processed 否经过了后期处理
     * @return {@link DecodeResult}
     */
    DecodeResult setProcessed(boolean processed);

    /**
     * 回收图片
     *
     * @param bitmapPool {@link BitmapPool}
     */
    void recycle(BitmapPool bitmapPool);
}
