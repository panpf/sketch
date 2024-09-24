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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.resize

import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format
import kotlin.math.roundToInt

/**
 * Define how to resize the image
 *
 * @see com.github.panpf.sketch.core.common.test.resize.ResizeTest
 */
data class Resize constructor(
    /**
     * The size of the image after resizing
     */
    val size: Size,

    /**
     * The precision of the resize operation
     */
    val precision: Precision,

    /**
     * Which part of the original picture should be kept when the original topic needs to be cropped.
     * Works only when precision is [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO]
     */
    val scale: Scale,
) : Key {

    constructor(width: Int, height: Int, precision: Precision, scale: Scale)
            : this(Size(width, height), precision, scale)

    constructor(width: Int, height: Int)
            : this(width, height, Precision.LESS_PIXELS, Scale.CENTER_CROP)

    constructor(width: Int, height: Int, precision: Precision)
            : this(width, height, precision, Scale.CENTER_CROP)

    constructor(width: Int, height: Int, scale: Scale)
            : this(width, height, Precision.LESS_PIXELS, scale)

    override val key: String by lazy {
        "Resize(${size},${precision},${scale})"
    }

    /**
     * Calculate the precision according to the original image, and then decide whether to crop the image according to the precision
     */
    fun shouldClip(imageSize: Size): Boolean {
        if (size.isEmpty || imageSize.isEmpty) {
            return false
        }
        return when (precision) {
            Precision.EXACTLY -> imageSize != size
            Precision.SAME_ASPECT_RATIO -> {
                val imageAspectRatio = imageSize.width.toFloat().div(imageSize.height).format(1)
                val resizeAspectRatio = size.width.toFloat().div(size.height).format(1)
                imageAspectRatio != resizeAspectRatio
            }

            Precision.LESS_PIXELS -> false
            Precision.SMALLER_SIZE -> false
        }
    }

    /**
     * Calculate the mapping relationship between the original image and the resized image
     */
    fun calculateMapping(imageSize: Size): ResizeMapping {
        if (imageSize.isEmpty || size.isEmpty) {
            return ResizeMapping(
                srcRect = Rect(0, 0, imageSize.width, imageSize.height),
                dstRect = Rect(0, 0, size.width, size.height)
            )
        }

        val imageWidth: Int = imageSize.width
        val imageHeight: Int = imageSize.height
        val resizeWidth: Int = size.width
        val resizeHeight: Int = size.height
        if (imageWidth == resizeWidth && imageHeight == resizeHeight) {
            return ResizeMapping(
                srcRect = Rect(left = 0, top = 0, right = imageWidth, bottom = imageHeight),
                dstRect = Rect(left = 0, top = 0, right = resizeWidth, bottom = resizeHeight)
            )
        }

        @Suppress("CascadeIf")
        if (this.precision == Precision.LESS_PIXELS) {
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
        } else if (this.precision == Precision.SMALLER_SIZE) {
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
            if (this.precision == Precision.EXACTLY) {
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
            val srcRect: Rect = when (this.scale) {
                Scale.START_CROP -> {
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

                Scale.CENTER_CROP -> {
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

                Scale.END_CROP -> {
                    val finalScale = (imageWidth.toFloat() / newImageWidth)
                        .coerceAtMost(imageHeight.toFloat() / newImageHeight)
                    val srcWidth = (newImageWidth * finalScale).toInt()
                    val srcHeight = (newImageHeight * finalScale).toInt()
                    val srcLeft: Int = imageWidth - srcWidth
                    val srcTop: Int = imageHeight - srcHeight
                    Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight)
                }

                Scale.FILL -> {
                    Rect(left = 0, top = 0, right = imageWidth, bottom = imageHeight)
                }
            }
            return ResizeMapping(srcRect, destRect)
        }
    }

    override fun toString(): String {
        return "Resize(size=${size}, precision=${precision}, scale=${scale})"
    }
}