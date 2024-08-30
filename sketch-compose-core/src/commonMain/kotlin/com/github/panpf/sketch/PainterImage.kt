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
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.painter.toLogString
import kotlin.math.roundToInt

/**
 * Convert [Painter] to [PainterImage]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.PainterImageTest.testAsSketchImage
 */
fun Painter.asSketchImage(shareable: Boolean = false): PainterImage {
    return PainterImage(this, shareable)
}

/**
 * Painter image
 *
 * @see com.github.panpf.sketch.compose.core.common.test.PainterImageTest
 */
@Stable
data class PainterImage(
    val painter: Painter,
    override val shareable: Boolean = false
) : Image {

    override val width: Int =
        painter.intrinsicSize.takeIf { it.isSpecified }?.width?.roundToInt() ?: -1

    override val height: Int =
        painter.intrinsicSize.takeIf { it.isSpecified }?.height?.roundToInt() ?: -1

    override val byteCount: Long = 4L * width * height

    override val allocationByteCount: Long = 4L * width * height

    override fun cacheValue(extras: Map<String, Any?>?): MemoryCache.Value? = null

    override fun checkValid(): Boolean = true

    override fun transformer(): ImageTransformer? = null

    override fun getPixels(): IntArray? = null

    override fun toString(): String =
        "PainterImage(painter=${painter.toLogString()}, shareable=$shareable)"
}