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
package com.github.panpf.sketch.common.decode.internal

import android.graphics.Rect
import android.widget.ImageView.ScaleType
import kotlin.math.abs
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
         * @param scaleType    缩放类型
         * @param exactlySame  强制使新图片的尺寸和 resizeWidth、resizeHeight 一致
         * @return 计算结果
         */
        fun calculator(
            imageWidth: Int,
            imageHeight: Int,
            resizeWidth: Int,
            resizeHeight: Int,
            scaleType: ScaleType?,
            exactlySame: Boolean
        ): ResizeMapping {
            val newScaleType = scaleType ?: ScaleType.FIT_CENTER
            if (imageWidth == resizeWidth && imageHeight == resizeHeight) {
                return ResizeMapping(
                    imageWidth,
                    imageHeight,
                    Rect(0, 0, imageWidth, imageHeight),
                    Rect(0, 0, imageWidth, imageHeight)
                )
            }
            val newImageWidth: Int
            val newImageHeight: Int
            if (exactlySame) {
                newImageWidth = resizeWidth
                newImageHeight = resizeHeight
            } else {
                val finalImageSize =
                    scaleTargetSize(imageWidth, imageHeight, resizeWidth, resizeHeight)
                newImageWidth = finalImageSize[0]
                newImageHeight = finalImageSize[1]
            }
            val destRect = Rect(0, 0, newImageWidth, newImageHeight)
            val srcRect: Rect =
                if (newScaleType == ScaleType.CENTER || newScaleType == ScaleType.CENTER_CROP || newScaleType == ScaleType.CENTER_INSIDE) {
                    srcMappingCenterRect(
                        imageWidth,
                        imageHeight,
                        newImageWidth,
                        newImageHeight
                    )
                } else if (newScaleType == ScaleType.FIT_START) {
                    srcMappingStartRect(
                        imageWidth,
                        imageHeight,
                        newImageWidth,
                        newImageHeight
                    )
                } else if (newScaleType == ScaleType.FIT_CENTER) {
                    srcMappingCenterRect(
                        imageWidth,
                        imageHeight,
                        newImageWidth,
                        newImageHeight
                    )
                } else if (newScaleType == ScaleType.FIT_END) {
                    srcMappingEndRect(
                        imageWidth,
                        imageHeight,
                        newImageWidth,
                        newImageHeight
                    )
                } else if (newScaleType == ScaleType.FIT_XY) {
                    Rect(0, 0, imageWidth, imageHeight)
                } else if (newScaleType == ScaleType.MATRIX) {
                    srcMatrixRect(
                        imageWidth,
                        imageHeight,
                        newImageWidth,
                        newImageHeight
                    )
                } else {
                    srcMappingCenterRect(
                        imageWidth,
                        imageHeight,
                        newImageWidth,
                        newImageHeight
                    )
                }
            return ResizeMapping(newImageWidth, newImageHeight, srcRect, destRect)
        }

        private fun srcMappingStartRect(
            originalImageWidth: Int,
            originalImageHeight: Int,
            targetImageWidth: Int,
            targetImageHeight: Int
        ): Rect {
            val widthScale = originalImageWidth.toFloat() / targetImageWidth
            val heightScale = originalImageHeight.toFloat() / targetImageHeight
            val finalScale = widthScale.coerceAtMost(heightScale)
            val srcWidth = (targetImageWidth * finalScale).toInt()
            val srcHeight = (targetImageHeight * finalScale).toInt()
            val srcLeft = 0
            val srcTop = 0
            return Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight)
        }

        private fun srcMappingCenterRect(
            originalImageWidth: Int,
            originalImageHeight: Int,
            targetImageWidth: Int,
            targetImageHeight: Int
        ): Rect {
            val widthScale = originalImageWidth.toFloat() / targetImageWidth
            val heightScale = originalImageHeight.toFloat() / targetImageHeight
            val finalScale = widthScale.coerceAtMost(heightScale)
            val srcWidth = (targetImageWidth * finalScale).toInt()
            val srcHeight = (targetImageHeight * finalScale).toInt()
            val srcLeft = (originalImageWidth - srcWidth) / 2
            val srcTop = (originalImageHeight - srcHeight) / 2
            return Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight)
        }

        private fun srcMappingEndRect(
            originalImageWidth: Int,
            originalImageHeight: Int,
            targetImageWidth: Int,
            targetImageHeight: Int
        ): Rect {
            val widthScale = originalImageWidth.toFloat() / targetImageWidth
            val heightScale = originalImageHeight.toFloat() / targetImageHeight
            val finalScale = widthScale.coerceAtMost(heightScale)
            val srcWidth = (targetImageWidth * finalScale).toInt()
            val srcHeight = (targetImageHeight * finalScale).toInt()
            val srcLeft: Int = originalImageWidth - srcWidth
            val srcTop: Int = originalImageHeight - srcHeight
            return Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight)
        }

        private fun srcMatrixRect(
            originalImageWidth: Int,
            originalImageHeight: Int,
            targetImageWidth: Int,
            targetImageHeight: Int
        ): Rect {
            return if (originalImageWidth > targetImageWidth && originalImageHeight > targetImageHeight) {
                Rect(0, 0, targetImageWidth, targetImageHeight)
            } else {
                val scale =
                    if (targetImageWidth - originalImageWidth > targetImageHeight - originalImageHeight) targetImageWidth.toFloat() / originalImageWidth else targetImageHeight.toFloat() / originalImageHeight
                val srcWidth = (targetImageWidth / scale).toInt()
                val srcHeight = (targetImageHeight / scale).toInt()
                val srcLeft = 0
                val srcTop = 0
                Rect(srcLeft, srcTop, srcLeft + srcWidth, srcTop + srcHeight)
            }
        }

        private fun scaleTargetSize(
            originalImageWidth: Int,
            originalImageHeight: Int,
            targetImageWidth: Int,
            targetImageHeight: Int
        ): IntArray {
            var newTargetImageWidth = targetImageWidth
            var newTargetImageHeight = targetImageHeight
            if (newTargetImageWidth > originalImageWidth || newTargetImageHeight > originalImageHeight) {
                val scale =
                    if (abs(newTargetImageWidth - originalImageWidth) < abs(
                            newTargetImageHeight - originalImageHeight
                        )
                    ) newTargetImageWidth.toFloat() / originalImageWidth else newTargetImageHeight.toFloat() / originalImageHeight
                newTargetImageWidth = (newTargetImageWidth / scale).roundToInt()
                newTargetImageHeight = (newTargetImageHeight / scale).roundToInt()
            }
            return intArrayOf(newTargetImageWidth, newTargetImageHeight)
        }
    }
}