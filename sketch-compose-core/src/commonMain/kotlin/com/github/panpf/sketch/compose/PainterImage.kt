/*
 * Copyright (C) 2023 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.compose

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.CountingImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.ImageTransformer
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.request.internal.RequestContext
import kotlin.math.roundToInt

expect fun Image.asPainter(): Painter

fun Painter.asSketchImage(shareable: Boolean = false): Image {
    return PainterImage(this, shareable)
}

data class PainterImage(val painter: Painter, override val shareable: Boolean = false) : Image {

    override val width: Int = painter.intrinsicSize.width.roundToInt()

    override val height: Int = painter.intrinsicSize.height.roundToInt()

    override val byteCount: Int = 4 * width * height  // TODO check

    override val allocationByteCount: Int = 4 * width * height

    override fun cacheValue(
        requestContext: RequestContext,
        extras: Map<String, Any?>
    ): MemoryCache.Value? = null

    override fun checkValid(): Boolean = true

    override fun toCountingImage(requestContext: RequestContext): CountingImage? = null

    override fun transformer(): ImageTransformer? = null

    override fun toString(): String {
        return super.toString() // TODO
    }
}