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
package com.github.panpf.sketch.cache

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.github.panpf.sketch.decode.internal.logString
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage

fun MemoryCache.Value.asSketchImage(resources: Resources): Image = when (this) {
    is BitmapValue -> BitmapDrawable(resources, bitmap)
    is CountBitmapValue -> SketchCountBitmapDrawable(resources, countBitmap)
    else -> throw IllegalStateException("Unknown MemoryCache.Value: $this")
}.asSketchImage()

class BitmapValue(
    val bitmap: Bitmap,
    override val extras: Map<String, Any?> = emptyMap(),
) : MemoryCache.Value {

    override val size: Int = bitmap.byteCount

    override fun setIsCached(cached: Boolean) {

    }

    override fun checkValid(): Boolean {
        return !bitmap.isRecycled
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BitmapValue
        if (bitmap != other.bitmap) return false
        return extras == other.extras
    }

    override fun hashCode(): Int {
        var result = bitmap.hashCode()
        result = 31 * result + extras.hashCode()
        return result
    }

    override fun toString(): String {
        return "BitmapValue(bitmap=${bitmap.logString}, extras=$extras)"
    }
}

class CountBitmapValue(
    val countBitmap: CountBitmap,
    override val extras: Map<String, Any?> = emptyMap(),
) : MemoryCache.Value {

    override val size: Int = countBitmap.byteCount

    override fun setIsCached(cached: Boolean) {
        countBitmap.setIsCached(cached, "BitmapValue")
    }

    override fun checkValid(): Boolean {
        return countBitmap.bitmap?.isRecycled == false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CountBitmapValue
        if (countBitmap != other.countBitmap) return false
        return extras == other.extras
    }

    override fun hashCode(): Int {
        var result = countBitmap.hashCode()
        result = 31 * result + extras.hashCode()
        return result
    }

    override fun toString(): String {
        return "BitmapValue(countBitmap=$countBitmap, extras=$extras)"
    }
}