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
package com.github.panpf.sketch.drawable

import android.graphics.Bitmap
import com.github.panpf.sketch.request.ImageFrom

interface SketchDrawable {
    /**
     * 获取图片 ID
     */
    val key: String?

    /**
     * 获取图片 uri
     */
    val uri: String?

    /**
     * 获取图片原始宽
     */
    val originWidth: Int

    /**
     * 获取图片原始高
     */
    val originHeight: Int

    /**
     * 获取图片类型
     */
    val mimeType: String?

    /**
     * 获取图片方向
     */
    val exifOrientation: Int

    /**
     * 获取占用内存，单位字节
     */
    val byteCount: Int

    /**
     * 获取 [Bitmap] 配置
     */
    val bitmapConfig: Bitmap.Config?

    /**
     * 获取图片来源
     */
    val imageFrom: ImageFrom?

    /**
     * 获取一些信息
     */
    val info: String?
}