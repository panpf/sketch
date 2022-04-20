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
package com.github.panpf.sketch.zoom.internal

import android.content.Context
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.zoom.ReadModeDecider
import com.github.panpf.sketch.zoom.Scales
import com.github.panpf.sketch.zoom.ScalesFactory
import kotlin.math.max
import kotlin.math.min

class ScalesFactoryImpl : ScalesFactory {

    override fun create(
        context: Context,
        viewSize: Size,
        drawableSize: Size,
        rotateDegrees: Int,
        imageSize: Size,
        scaleType: ScaleType,
        readModeDecider: ReadModeDecider?,
    ): Scales {
        if (imageSize.isEmpty || drawableSize.isEmpty || viewSize.isEmpty) {
            return Scales(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, floatArrayOf(1.0f, 1.0f))
        }

        val drawableWidth =
            if (rotateDegrees % 180 == 0) drawableSize.width else drawableSize.height
        val drawableHeight =
            if (rotateDegrees % 180 == 0) drawableSize.height else drawableSize.width
        val imageWidth =
            if (rotateDegrees % 180 == 0) imageSize.width else imageSize.height
        val imageHeight =
            if (rotateDegrees % 180 == 0) imageSize.height else imageSize.width
        val widthScale = viewSize.width.toFloat() / drawableWidth
        val heightScale = viewSize.height.toFloat() / drawableHeight
        val drawableThanViewLarge =
            drawableWidth > viewSize.width || drawableHeight > viewSize.height

        // The width or height of the drawable fills the view
        val fullZoomScale = min(widthScale, heightScale)
        // The width and height of drawable fill the view at the same time
        val fillZoomScale = max(widthScale, heightScale)
        // Enlarge drawable to the same size as its original image
        val originZoomScale =
            max(imageWidth.toFloat() / drawableWidth, imageHeight.toFloat() / drawableHeight)
        // The drawable size remains the same
        val keepZoomScale = 1.0f

        val minZoomScale: Float
        val initZoomScale: Float
        val doubleClickBigZoomScale: Float
        when {
            readModeDecider?.should(
                context, imageWidth, imageHeight, viewSize.width, viewSize.height
            ) == true -> {
                initZoomScale = fillZoomScale
                minZoomScale = fullZoomScale
                doubleClickBigZoomScale = max(originZoomScale, initZoomScale)
            }
            scaleType == ScaleType.CENTER
                    || (scaleType == ScaleType.CENTER_INSIDE && !drawableThanViewLarge) -> {
                initZoomScale = keepZoomScale
                minZoomScale = keepZoomScale
                doubleClickBigZoomScale =
                    floatArrayOf(originZoomScale, fullZoomScale, initZoomScale * 2f).maxOrNull()!!
            }
            scaleType == ScaleType.CENTER_CROP -> {
                initZoomScale = fillZoomScale
                minZoomScale = fillZoomScale
                doubleClickBigZoomScale = max(originZoomScale, initZoomScale * 2f)
            }
            scaleType == ScaleType.FIT_START
                    || scaleType == ScaleType.FIT_CENTER
                    || scaleType == ScaleType.FIT_END
                    || (scaleType == ScaleType.CENTER_INSIDE && drawableThanViewLarge) -> {
                initZoomScale = fullZoomScale
                minZoomScale = fullZoomScale
                doubleClickBigZoomScale = max(originZoomScale, initZoomScale * 2f)
            }
            else -> {
                initZoomScale = keepZoomScale
                minZoomScale = keepZoomScale
                doubleClickBigZoomScale = max(originZoomScale, initZoomScale * 2f)
            }
        }
        val doubleClicks = floatArrayOf(minZoomScale, doubleClickBigZoomScale)
        val maxZoomScale = doubleClickBigZoomScale * 2f
        return Scales(
            min = minZoomScale,
            max = maxZoomScale,
            init = initZoomScale,
            full = fullZoomScale,
            fill = fillZoomScale,
            origin = originZoomScale,
            doubleClicks = doubleClicks
        )
    }
}