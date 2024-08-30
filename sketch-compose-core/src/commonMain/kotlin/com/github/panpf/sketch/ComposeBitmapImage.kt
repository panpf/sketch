/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch

import androidx.compose.runtime.Stable
import com.github.panpf.sketch.cache.ComposeBitmapImageValue
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.painter.toLogString

/**
 * Convert [ComposeBitmap] to [Image]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.ComposeBitmapImageTest.testAsSketchImage
 */
fun ComposeBitmap.asSketchImage(shareable: Boolean = true): ComposeBitmapImage {
    return ComposeBitmapImage(this, shareable)
}

/**
 * ComposeBitmap Image
 *
 * @see com.github.panpf.sketch.compose.core.common.test.ComposeBitmapImageTest
 */
@Stable
data class ComposeBitmapImage(
    val bitmap: ComposeBitmap,
    override val shareable: Boolean = true
) : Image {

    override val width: Int = bitmap.width

    override val height: Int = bitmap.height

    override val byteCount: Long = 4L * width * height

    override val allocationByteCount: Long = 4L * width * height

    override fun cacheValue(
        extras: Map<String, Any?>?
    ): Value = ComposeBitmapImageValue(this, extras)

    override fun checkValid(): Boolean = true

    override fun transformer(): ImageTransformer? = null

    override fun getPixels(): IntArray {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.readPixels(pixels)
        return pixels
    }

    override fun toString(): String =
        "ComposeBitmapImage(bitmap=${bitmap.toLogString()}, shareable=$shareable)"
}