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

class DefaultScalesFactory : ScalesFactory {

    override fun create(
        context: Context,
        viewSize: Size,
        drawableSize: Size,
        rotateDegrees: Int,
        imageSize: Size,
        scaleType: ScaleType,
        readModeDecider: ReadModeDecider?,
    ): Scales {
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
        val imageThanViewLarge =
            drawableWidth > viewSize.width || drawableHeight > viewSize.height
        val finalScaleType: ScaleType = if (scaleType == ScaleType.MATRIX) {
            ScaleType.FIT_CENTER
        } else if (scaleType == ScaleType.CENTER_INSIDE) {
            if (imageThanViewLarge) ScaleType.FIT_CENTER else ScaleType.CENTER
        } else {
            scaleType
        }


        var minZoomScale: Float
        var maxZoomScale: Float

        // 小的是完整显示比例，大的是充满比例
        val fullZoomScale = widthScale.coerceAtMost(heightScale)
        val fillZoomScale = widthScale.coerceAtLeast(heightScale)
        val originZoomScale =
            (imageWidth.toFloat() / drawableWidth).coerceAtLeast(imageHeight.toFloat() / drawableHeight)
        val initZoomScale = getInitScale(
            viewSize,
            imageSize,
            drawableSize,
            finalScaleType,
            rotateDegrees,
            readModeDecider
        )
        if (readModeDecider?.shouldUseByHeight(imageWidth, imageHeight) == true) {
            // 阅读模式下保证阅读效果最重要
            minZoomScale = fullZoomScale
            maxZoomScale = originZoomScale.coerceAtLeast(fillZoomScale)
        } else if (readModeDecider?.shouldUseByWidth(imageWidth, imageHeight) == true) {
            // 阅读模式下保证阅读效果最重要
            minZoomScale = fullZoomScale
            maxZoomScale = originZoomScale.coerceAtLeast(fillZoomScale)
        } else if (finalScaleType == ScaleType.CENTER) {
            minZoomScale = 1.0f
            maxZoomScale = originZoomScale.coerceAtLeast(fillZoomScale)
        } else if (finalScaleType == ScaleType.CENTER_CROP) {
            minZoomScale = fillZoomScale
            // 由于CENTER_CROP的时候最小缩放比例就是充满比例，所以最大缩放比例一定要比充满比例大的多
            maxZoomScale = originZoomScale.coerceAtLeast(fillZoomScale * 1.5f)
        } else if (finalScaleType == ScaleType.FIT_START || finalScaleType == ScaleType.FIT_CENTER || finalScaleType == ScaleType.FIT_END) {
            minZoomScale = fullZoomScale

            // 如果原始比例仅仅比充满比例大一点点，还是用充满比例作为最大缩放比例比较好，否则谁大用谁
            maxZoomScale =
                if (originZoomScale > fillZoomScale && fillZoomScale * 1.2f >= originZoomScale) {
                    fillZoomScale
                } else {
                    originZoomScale.coerceAtLeast(fillZoomScale)
                }

            // 最大缩放比例和最小缩放比例的差距不能太小，最小得是最小缩放比例的1.5倍
            maxZoomScale = maxZoomScale.coerceAtLeast(minZoomScale * 1.5f)
        } else if (finalScaleType == ScaleType.FIT_XY) {
            minZoomScale = fullZoomScale
            maxZoomScale = fullZoomScale
        } else {
            // 基本不会走到这儿
            minZoomScale = fullZoomScale
            maxZoomScale = fullZoomScale
        }

        // 这样的情况基本不会出现，不过还是加层保险
        if (minZoomScale > maxZoomScale) {
            minZoomScale += maxZoomScale
            maxZoomScale = minZoomScale - maxZoomScale
            minZoomScale -= maxZoomScale
        }

        // 双击缩放比例始终由最小缩放比例和最大缩放比例组成
        val zoomScales: FloatArray = floatArrayOf(minZoomScale, maxZoomScale) // 双击缩放所使用的比例
        return Scales(
            min = minZoomScale,
            max = maxZoomScale.times(2f),
            init = initZoomScale,
            full = fullZoomScale,
            fill = fillZoomScale,
            origin = originZoomScale,
            doubleClicks = zoomScales
        )
    }

    private fun getInitScale(
        viewSize: Size,
        imageSize: Size,
        drawableSize: Size,
        scaleType: ScaleType,
        rotateDegrees: Int,
        readModeDecider: ReadModeDecider?,
    ): Float {
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
        val imageThanViewLarge =
            drawableWidth > viewSize.width || drawableHeight > viewSize.height
        val finalScaleType: ScaleType = if (scaleType == ScaleType.MATRIX) {
            ScaleType.FIT_CENTER
        } else if (scaleType == ScaleType.CENTER_INSIDE) {
            if (imageThanViewLarge) ScaleType.FIT_CENTER else ScaleType.CENTER
        } else {
            scaleType
        }
        return when {
            readModeDecider?.shouldUseByHeight(imageWidth, imageHeight) == true -> widthScale
            readModeDecider?.shouldUseByWidth(imageWidth, imageHeight) == true -> heightScale
            finalScaleType == ScaleType.CENTER -> 1.0f
            finalScaleType == ScaleType.CENTER_CROP -> widthScale.coerceAtLeast(heightScale)
            finalScaleType == ScaleType.FIT_START -> widthScale.coerceAtMost(heightScale)
            finalScaleType == ScaleType.FIT_END -> widthScale.coerceAtMost(heightScale)
            finalScaleType == ScaleType.FIT_CENTER -> widthScale.coerceAtMost(heightScale)
            finalScaleType == ScaleType.FIT_XY -> 1.0f
            else -> 1.0f
        }
    }
}