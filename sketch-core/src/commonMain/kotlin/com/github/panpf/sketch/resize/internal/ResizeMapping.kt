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

import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SMALLER_SIZE
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import kotlin.math.roundToInt

data class ResizeMapping constructor(
    val srcRect: Rect,
    val destRect: Rect
) {
    val newWidth: Int = destRect.width()
    val newHeight: Int = destRect.height()
}

fun calculateResizeMapping(
    imageSize: Size,
    resizeSize: Size,
    precision: Precision,
    scale: Scale,
): ResizeMapping? {
    if (imageSize.isEmpty || resizeSize.isEmpty) {
        return null
    }
    val imageWidth: Int = imageSize.width
    val imageHeight: Int = imageSize.height
    val resizeWidth: Int = resizeSize.width
    val resizeHeight: Int = resizeSize.height
    if (imageWidth == resizeWidth && imageHeight == resizeHeight) {
        return ResizeMapping(
            srcRect = Rect(left = 0, top = 0, right = imageWidth, bottom = imageHeight),
            destRect = Rect(left = 0, top = 0, right = resizeWidth, bottom = resizeHeight)
        )
    }

    if (precision == LESS_PIXELS) {
        val resizePixels = resizeWidth * resizeHeight
        var scaleFactor = 1f
        while (((imageWidth * scaleFactor).toInt() * (imageHeight * scaleFactor).toInt()) > resizePixels) {
            scaleFactor -= 0.01f
        }
        val srcRect = Rect(left = 0, top = 0, right = imageWidth, bottom = imageHeight)
        val dstRect = Rect(
            left = 0,
            top = 0,
            right = (imageWidth * scaleFactor).toInt(),
            bottom = (imageHeight * scaleFactor).toInt()
        )
        return ResizeMapping(srcRect, dstRect)
    } else if (precision == SMALLER_SIZE) {
        var scaleFactor = 1f
        while ((imageWidth * scaleFactor).toInt() > resizeWidth || (imageHeight * scaleFactor).toInt() > resizeHeight) {
            scaleFactor -= 0.01f
        }
        val srcRect = Rect(left = 0, top = 0, right = imageWidth, bottom = imageHeight)
        val dstRect = Rect(
            left = 0,
            top = 0,
            right = (imageWidth * scaleFactor).toInt(),
            bottom = (imageHeight * scaleFactor).toInt()
        )
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
            val scaleFactor = when {
                resizeWidth >= imageWidth && resizeHeight >= imageHeight ->
                    widthRatio.coerceAtLeast(heightRatio)

                resizeWidth >= imageWidth -> widthRatio
                resizeHeight >= imageHeight -> heightRatio
                else -> 1f
            }
            newImageWidth = (resizeWidth / scaleFactor).roundToInt()
            newImageHeight = (resizeHeight / scaleFactor).roundToInt()
        }
        val destRect = Rect(left = 0, top = 0, right = newImageWidth, bottom = newImageHeight)
        val srcRect: Rect = when (scale) {
            START_CROP -> {
                val finalScale = (imageWidth.toFloat() / newImageWidth)
                    .coerceAtMost(imageHeight.toFloat() / newImageHeight)
                val srcWidth = (newImageWidth * finalScale).toInt()
                val srcHeight = (newImageHeight * finalScale).toInt()
                val srcLeft = 0
                val srcTop = 0
                Rect(
                    left = srcLeft,
                    top = srcTop,
                    right = srcLeft + srcWidth,
                    bottom = srcTop + srcHeight
                )
            }

            CENTER_CROP -> {
                val finalScale = (imageWidth.toFloat() / newImageWidth)
                    .coerceAtMost(imageHeight.toFloat() / newImageHeight)
                val srcWidth = (newImageWidth * finalScale).toInt()
                val srcHeight = (newImageHeight * finalScale).toInt()
                val srcLeft = (imageWidth - srcWidth) / 2
                val srcTop = (imageHeight - srcHeight) / 2
                Rect(
                    left = srcLeft,
                    top = srcTop,
                    right = srcLeft + srcWidth,
                    bottom = srcTop + srcHeight
                )
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
                Rect(left = 0, top = 0, right = imageWidth, bottom = imageHeight)
            }
        }
        return ResizeMapping(srcRect, destRect)
    }
}

fun calculateResizeMapping(
    imageWidth: Int,
    imageHeight: Int,
    resizeWidth: Int,
    resizeHeight: Int,
    precision: Precision,
    scale: Scale,
): ResizeMapping? = calculateResizeMapping(
    imageSize = Size(width = imageWidth, height = imageHeight),
    resizeSize = Size(width = resizeWidth, height = resizeHeight),
    precision = precision,
    scale = scale
)