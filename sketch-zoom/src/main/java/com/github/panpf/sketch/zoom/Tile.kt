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
package com.github.panpf.sketch.zoom

import android.graphics.Bitmap
import android.graphics.Rect
import com.github.panpf.sketch.cache.CountBitmap
import kotlinx.coroutines.Job

class Tile constructor(val srcRect: Rect, val inSampleSize: Int) {

    internal var countBitmap: CountBitmap? = null
        set(value) {
            field?.setIsDisplayed(false, "Tile")
            field = value
            value?.setIsDisplayed(true, "Tile")
        }

    val bitmap: Bitmap?
        get() = countBitmap?.bitmap
    var loadJob: Job? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Tile) return false

        if (srcRect != other.srcRect) return false
        if (inSampleSize != other.inSampleSize) return false
        if (bitmap != other.bitmap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = srcRect.hashCode()
        result = 31 * result + inSampleSize
        result = 31 * result + (bitmap?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Tile(srcRect=$srcRect, inSampleSize=$inSampleSize, bitmap=${bitmap?.run { "Bitmap(${width}x${height},$config)" }})"
    }
}