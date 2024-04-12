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
package com.github.panpf.sketch.resize.internal

import android.graphics.Rect
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SMALLER_SIZE
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import kotlin.math.roundToInt

data class ResizeMapping constructor(
    val srcRect: Rect,
    val destRect: Rect
) {
    val newWidth: Int = destRect.width()
    val newHeight: Int = destRect.height()
}

fun calculateResizeMapping(
    imageWidth: Int,
    imageHeight: Int,
    resizeWidth: Int,
    resizeHeight: Int,
    precision: Precision,
    resizeScale: Scale,
): ResizeMapping? {
    if (imageWidth <= 0 || imageHeight <= 0 || resizeWidth <= 0 || resizeHeight <= 0) {
        return null
    }
    if (imageWidth == resizeWidth && imageHeight == resizeHeight) {
        return ResizeMapping(
            srcRect = Rect(0, 0, imageWidth, imageHeight),
            destRect = Rect(0, 0, resizeWidth, resizeHeight)
        )
    }

    if (precision == LESS_PIXELS) {
        val resizePixels = resizeWidth * resizeHeight
        var scale = 1f
        while (((imageWidth * scale).toInt() * (imageHeight * scale).toInt()) > resizePixels) {
            scale -= 0.01f
        }
        val srcRect = Rect(0, 0, imageWidth, imageHeight)
        val dstRect = Rect(0, 0, (imageWidth * scale).toInt(), (imageHeight * scale).toInt())
        return ResizeMapping(srcRect, dstRect)
    } else if (precision == SMALLER_SIZE) {
        var scale = 1f
        while ((imageWidth * scale).toInt() > resizeWidth || (imageHeight * scale).toInt() > resizeHeight) {
            scale -= 0.01f
        }
        val srcRect = Rect(0, 0, imageWidth, imageHeight)
        val dstRect = Rect(0, 0, (imageWidth * scale).toInt(), (imageHeight * scale).toInt())
        return ResizeMapping(srcRect, dstRect)
    } else {
        val newImageWidth: Int
        val newImageHeight: Int
        if (precision == EXACTLY) {
            newImageWidth = resizeWidth
            newImageHeight = resizeHeight
        } else {
            val widthRatio = resizeWidth.toFloat() / imageWidth
            val heightRatio = resizeHeight.toFloat() / imageHeight
            val scale = when {
                resizeWidth >= imageWidth && resizeHeight >= imageHeight ->
                    widthRatio.coerceAtLeast(heightRatio)

                resizeWidth >= imageWidth -> widthRatio
                resizeHeight >= imageHeight -> heightRatio
                else -> 1f
            }
            newImageWidth = (resizeWidth / scale).roundToInt()
            newImageHeight = (resizeHeight / scale).roundToInt()
        }
        val destRect = Rect(0, 0, newImageWidth, newImageHeight)
        val srcRect: Rect = when (resizeScale) {
            START_CROP -> {
                val finalScale = (imageWidth.toFloat() / newImageWidth)
                    .coerceAtMost(imageHeight.toFloat() / newImageHeight)
                val srcWidth = (newImageWidth * finalScale).toInt()
                val srcHeight = (newImageHeight * finalScale).toInt()
                val srcLeft = 0
                val srcTop = 0
                Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight)
            }

            CENTER_CROP -> {
                val finalScale = (imageWidth.toFloat() / newImageWidth)
                    .coerceAtMost(imageHeight.toFloat() / newImageHeight)
                val srcWidth = (newImageWidth * finalScale).toInt()
                val srcHeight = (newImageHeight * finalScale).toInt()
                val srcLeft = (imageWidth - srcWidth) / 2
                val srcTop = (imageHeight - srcHeight) / 2
                Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight)
            }

            END_CROP -> {
                val finalScale = (imageWidth.toFloat() / newImageWidth)
                    .coerceAtMost(imageHeight.toFloat() / newImageHeight)
                val srcWidth = (newImageWidth * finalScale).toInt()
                val srcHeight = (newImageHeight * finalScale).toInt()
                val srcLeft: Int = imageWidth - srcWidth
                val srcTop: Int = imageHeight - srcHeight
                Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight)
            }

            FILL -> {
                Rect(0, 0, imageWidth, imageHeight)
            }
        }
        return ResizeMapping(srcRect, destRect)
    }
}