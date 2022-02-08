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
package com.github.panpf.sketch.decode.resize

import android.graphics.Rect
import kotlin.math.roundToInt

data class ResizeMapping(
    val newWidth: Int,
    val newHeight: Int,
    val srcRect: Rect,
    val destRect: Rect
) {

    companion object {
        /**
         * 计算
         *
         * @param imageWidth   图片原始宽
         * @param imageHeight  图片原始高
         * @param resizeWidth  目标宽
         * @param resizeHeight 目标高
         * @param resizeScale    缩放类型
         * @param exactlySize  强制使新图片的尺寸和 resizeWidth、resizeHeight 一致
         * @return 计算结果
         */
        fun calculator(
            imageWidth: Int,
            imageHeight: Int,
            resizeWidth: Int,
            resizeHeight: Int,
            resizeScale: Scale,
            exactlySize: Boolean
        ): ResizeMapping {
            if (imageWidth == resizeWidth && imageHeight == resizeHeight) {
                return ResizeMapping(
                    newWidth = imageWidth,
                    newHeight = imageHeight,
                    srcRect = Rect(0, 0, imageWidth, imageHeight),
                    destRect = Rect(0, 0, resizeWidth, resizeHeight)
                )
            }

            val newImageWidth: Int
            val newImageHeight: Int
            if (exactlySize) {
                newImageWidth = resizeWidth
                newImageHeight = resizeHeight
            } else {
                val widthRatio = resizeWidth.toFloat() / imageWidth
                val heightRatio = resizeHeight.toFloat() / imageHeight
                when {
                    resizeWidth <= imageWidth && resizeHeight <= imageHeight -> {
                        val scale = widthRatio.coerceAtLeast(heightRatio)
                        newImageWidth = (imageWidth * scale).roundToInt()
                        newImageHeight = (imageHeight * scale).roundToInt()
                    }
                    resizeWidth >= imageWidth && resizeHeight >= imageHeight -> {
                        val scale = widthRatio.coerceAtLeast(heightRatio)
                        newImageWidth = (resizeWidth / scale).roundToInt()
                        newImageHeight = (resizeHeight / scale).roundToInt()
                    }
                    resizeWidth >= imageWidth -> {
                        @Suppress("UnnecessaryVariable") val scale = widthRatio
                        newImageWidth = (resizeWidth / scale).roundToInt()
                        newImageHeight = (resizeHeight / scale).roundToInt()
                    }
                    resizeHeight >= imageHeight -> {
                        @Suppress("UnnecessaryVariable") val scale = heightRatio
                        newImageWidth = (resizeWidth / scale).roundToInt()
                        newImageHeight = (resizeHeight / scale).roundToInt()
                    }
                    else -> throw IllegalArgumentException(
                        "Unsupported size, " +
                                "imageSize=${imageWidth}x${imageHeight}, " +
                                "resizeSize=${resizeWidth}x${resizeHeight}"
                    )
                }
            }
            val destRect = Rect(0, 0, newImageWidth, newImageHeight)
            val srcRect: Rect = when (resizeScale) {
                Scale.START_CROP -> {
                    val finalScale = (imageWidth.toFloat() / newImageWidth)
                        .coerceAtMost(imageHeight.toFloat() / newImageHeight)
                    val srcWidth = (newImageWidth * finalScale).toInt()
                    val srcHeight = (newImageHeight * finalScale).toInt()
                    val srcLeft = 0
                    val srcTop = 0
                    Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight)
                }
                Scale.CENTER_CROP -> {
                    val finalScale = (imageWidth.toFloat() / newImageWidth)
                        .coerceAtMost(imageHeight.toFloat() / newImageHeight)
                    val srcWidth = (newImageWidth * finalScale).toInt()
                    val srcHeight = (newImageHeight * finalScale).toInt()
                    val srcLeft = (imageWidth - srcWidth) / 2
                    val srcTop = (imageHeight - srcHeight) / 2
                    Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight)
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
                    Rect(0, 0, imageWidth, imageHeight)
                }
            }
            return ResizeMapping(newImageWidth, newImageHeight, srcRect, destRect)
        }
    }
}