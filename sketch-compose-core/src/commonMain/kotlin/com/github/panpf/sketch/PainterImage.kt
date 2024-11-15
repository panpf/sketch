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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.painter.AnimatablePainter
import com.github.panpf.sketch.painter.toLogString
import kotlin.math.roundToInt

/**
 * Convert [Painter] to [PainterImage]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.PainterImageTest.testAsImage
 */
fun Painter.asImage(shareable: Boolean = this !is AnimatablePainter): PainterImage {
    return PainterImage(this, shareable)
}

/**
 * Convert the Image to a Painter, returns null if it cannot be converted
 *
 * @see com.github.panpf.sketch.compose.core.android.test.PainterImageAndroidTest.testImageAsPainterOrNull
 * @see com.github.panpf.sketch.compose.core.nonandroid.test.PainterImageNonAndroidTest.testImageAsPainterOrNull
 */
expect fun Image.asPainterOrNull(filterQuality: FilterQuality = DrawScope.DefaultFilterQuality): Painter?

/**
 * Convert the Image to a Painter, throws an exception if it cannot be converted
 *
 * @see com.github.panpf.sketch.compose.core.android.test.PainterImageAndroidTest.testImageAsPainter
 * @see com.github.panpf.sketch.compose.core.nonandroid.test.PainterImageNonAndroidTest.testImageAsPainter
 */
fun Image.asPainter(filterQuality: FilterQuality = DrawScope.DefaultFilterQuality): Painter =
    asPainterOrNull(filterQuality)
        ?: throw IllegalArgumentException("Unable to convert '$this' to Painter")

/**
 * Painter image
 *
 * @see com.github.panpf.sketch.compose.core.common.test.PainterImageTest
 */
@Stable
data class PainterImage constructor(
    val painter: Painter,
    override val shareable: Boolean = painter !is AnimatablePainter
) : Image {

    override val width: Int =
        painter.intrinsicSize.takeIf { it.isSpecified }?.width?.roundToInt() ?: -1

    override val height: Int =
        painter.intrinsicSize.takeIf { it.isSpecified }?.height?.roundToInt() ?: -1

    override val byteCount: Long by lazy {
        val width = width
        val height = height
        if (width > 0 && height > 0) {
            4L * width * height
        } else {
            0L
        }
    }

    override fun checkValid(): Boolean = true

    override fun toString(): String =
        "PainterImage(painter=${painter.toLogString()}, shareable=$shareable)"
}