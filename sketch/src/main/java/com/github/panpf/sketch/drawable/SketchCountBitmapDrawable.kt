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

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.util.BitmapInfo
import com.github.panpf.sketch.util.toBitmapInfo

class SketchCountBitmapDrawable constructor(
    resources: Resources,
    val countBitmap: CountBitmap,
    override val dataFrom: DataFrom,
) : BitmapDrawable(resources, countBitmap.bitmap!!), SketchDrawable {

    override val imageUri: String
        get() = countBitmap.imageUri

    override val requestKey: String
        get() = countBitmap.requestKey

    override val requestCacheKey: String
        get() = countBitmap.requestCacheKey

    override val imageInfo: ImageInfo
        get() = countBitmap.imageInfo

    override val transformedList: List<String>?
        get() = countBitmap.transformedList

    override val bitmapInfo: BitmapInfo by lazy {
        bitmap.toBitmapInfo()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SketchCountBitmapDrawable) return false
        if (countBitmap != other.countBitmap) return false
        if (dataFrom != other.dataFrom) return false
        if (imageUri != other.imageUri) return false
        if (requestKey != other.requestKey) return false
        if (requestCacheKey != other.requestCacheKey) return false
        if (imageInfo != other.imageInfo) return false
        if (transformedList != other.transformedList) return false
        return true
    }

    override fun hashCode(): Int {
        var result = countBitmap.hashCode()
        result = 31 * result + dataFrom.hashCode()
        result = 31 * result + imageUri.hashCode()
        result = 31 * result + requestKey.hashCode()
        result = 31 * result + requestCacheKey.hashCode()
        result = 31 * result + imageInfo.hashCode()
        result = 31 * result + (transformedList?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "SketchCountBitmapDrawable(" +
                imageInfo.toShortString() +
                "," + dataFrom +
                "," + bitmapInfo.toShortString() +
                "," + transformedList +
                "," + requestKey +
                ")"
}