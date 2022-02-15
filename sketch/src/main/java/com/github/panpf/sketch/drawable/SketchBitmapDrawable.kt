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
import android.graphics.drawable.BitmapDrawable
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.util.BitmapInfo
import com.github.panpf.sketch.util.toBitmapInfo

open class SketchBitmapDrawable constructor(
    override val requestKey: String,
    override val requestUri: String,
    override val imageInfo: ImageInfo,
    override val imageExifOrientation: Int,
    override val dataFrom: DataFrom,
    override val transformedList: List<Transformed>?,
    bitmap: Bitmap,
) : BitmapDrawable(null, bitmap), SketchDrawable {

    override val bitmapInfo: BitmapInfo by lazy {
        bitmap.toBitmapInfo()
    }

    init {
        // 这一步很重要，让 BitmapDrawable 的 density 和 Bitmap 的 density 保持一致
        // 这样 getIntrinsicWidth() 和 getIntrinsicHeight() 方法得到的就是 bitmap的 真实的（未经过缩放）尺寸
        setTargetDensity(bitmap.density)
    }

    override fun toString(): String =
        "SketchBitmapDrawable(${imageInfo.toShortString()},${exifOrientationName(imageExifOrientation)},$dataFrom,${bitmapInfo.toShortString()},${transformedList},$requestKey)"
}