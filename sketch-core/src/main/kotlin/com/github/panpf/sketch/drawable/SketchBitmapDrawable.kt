/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.logString

// TODO Deprecated SketchBitmapDrawable
class SketchBitmapDrawable constructor(
    resources: Resources,
    bitmap: Bitmap,
    override val imageUri: String,
    override val requestKey: String,
    override val requestCacheKey: String,
    override val imageInfo: ImageInfo,
    override val transformedList: List<String>?,
    override val extras: Map<String, String>?,
    override val dataFrom: DataFrom,
) : BitmapDrawable(resources, bitmap), SketchDrawable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SketchBitmapDrawable
        if (bitmap != other.bitmap) return false
        if (imageUri != other.imageUri) return false
        if (requestKey != other.requestKey) return false
        if (requestCacheKey != other.requestCacheKey) return false
        if (imageInfo != other.imageInfo) return false
        if (transformedList != other.transformedList) return false
        if (extras != other.extras) return false
        if (dataFrom != other.dataFrom) return false
        return true
    }

    override fun hashCode(): Int {
        var result = bitmap.hashCode()
        result = 31 * result + imageUri.hashCode()
        result = 31 * result + requestKey.hashCode()
        result = 31 * result + requestCacheKey.hashCode()
        result = 31 * result + imageInfo.hashCode()
        result = 31 * result + transformedList.hashCode()
        result = 31 * result + extras.hashCode()
        result = 31 * result + dataFrom.hashCode()
        return result
    }

    override fun toString(): String =
        "SketchBitmapDrawable(${bitmap.logString},${imageInfo.toShortString()},$dataFrom,$transformedList,$extras,'$requestKey')"
}