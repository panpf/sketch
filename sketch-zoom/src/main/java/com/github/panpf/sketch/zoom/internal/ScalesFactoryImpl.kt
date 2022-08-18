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
package com.github.panpf.sketch.zoom.internal

import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.zoom.ReadModeDecider
import com.github.panpf.sketch.zoom.Scales
import com.github.panpf.sketch.zoom.ScalesFactory
import kotlin.math.max
import kotlin.math.min

class ScalesFactoryImpl : ScalesFactory {

    override fun create(
        sketch: Sketch,
        viewSize: Size,
        imageSize: Size,
        drawableSize: Size,
        rotateDegrees: Int,
        scaleType: ScaleType,
        readModeDecider: ReadModeDecider?,
    ): Scales {
        if (drawableSize.isEmpty || viewSize.isEmpty) {
            return Scales.EMPTY
        }

        val drawableWidth =
            if (rotateDegrees % 180 == 0) drawableSize.width else drawableSize.height
        val drawableHeight =
            if (rotateDegrees % 180 == 0) drawableSize.height else drawableSize.width
        val imageWidth = if (!imageSize.isEmpty) {
            if (rotateDegrees % 180 == 0) imageSize.width else imageSize.height
        } else {
            drawableWidth
        }
        val imageHeight = if (!imageSize.isEmpty) {
            if (rotateDegrees % 180 == 0) imageSize.height else imageSize.width
        } else {
            drawableHeight
        }
        val widthScale = viewSize.width.toFloat() / drawableWidth
        val heightScale = viewSize.height.toFloat() / drawableHeight
        val drawableThanViewLarge =
            drawableWidth > viewSize.width || drawableHeight > viewSize.height

        // The width or height of the drawable fills the view
        val fullScale = min(widthScale, heightScale)
        // The width and height of drawable fill the view at the same time
        val fillScale = max(widthScale, heightScale)
        // Enlarge drawable to the same size as its original image
        val originScale =
            max(imageWidth.toFloat() / drawableWidth, imageHeight.toFloat() / drawableHeight)
        // The drawable size remains the same
        val keepScale = 1.0f

        val minScale: Float
        val initScale: Float
        val stepsBigScale: Float
        when {
            readModeDecider?.should(
                sketch, imageWidth, imageHeight, viewSize.width, viewSize.height
            ) == true -> {
                initScale = fillScale
                minScale = fullScale
                stepsBigScale = max(originScale, initScale)
            }
            scaleType == ScaleType.CENTER
                    || (scaleType == ScaleType.CENTER_INSIDE && !drawableThanViewLarge) -> {
                initScale = keepScale
                minScale = keepScale
                stepsBigScale =
                    floatArrayOf(originScale, fullScale, initScale * 2f).maxOrNull()!!
            }
            scaleType == ScaleType.CENTER_CROP -> {
                initScale = fillScale
                minScale = fillScale
                stepsBigScale = max(originScale, initScale * 2f)
            }
            scaleType == ScaleType.FIT_START
                    || scaleType == ScaleType.FIT_CENTER
                    || scaleType == ScaleType.FIT_END
                    || (scaleType == ScaleType.CENTER_INSIDE && drawableThanViewLarge) -> {
                initScale = fullScale
                minScale = fullScale
                stepsBigScale = max(originScale, initScale * 2f)
            }
            else -> {
                initScale = keepScale
                minScale = keepScale
                stepsBigScale = max(originScale, initScale * 2f)
            }
        }
        val steps = floatArrayOf(minScale, stepsBigScale)
        val maxScale = stepsBigScale * 2f
        return Scales(
            min = minScale,
            max = maxScale,
            init = initScale,
            full = fullScale,
            fill = fillScale,
            origin = originScale,
            steps = steps
        )
    }
}