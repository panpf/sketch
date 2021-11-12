/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.decode

import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.request.ImageFrom

/**
 * 解码结果
 */
interface DecodeResult {
    /**
     * 获取图片的属性
     *
     * @return [ImageAttrs]
     */
    val imageAttrs: ImageAttrs

    /**
     * 获取图片的来源
     *
     * @return [ImageFrom]
     */
    val imageFrom: ImageFrom

    /**
     * 是否禁止对图片进行后期处理
     */
    var isBanProcess: Boolean

    /**
     * 是否经过了后期处理
     */
    var isProcessed: Boolean

    /**
     * 回收图片
     *
     * @param bitmapPool [BitmapPool]
     */
    fun recycle(bitmapPool: BitmapPool)
}