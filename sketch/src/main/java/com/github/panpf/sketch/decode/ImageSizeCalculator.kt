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
package com.github.panpf.sketch.decode

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.github.panpf.sketch.SketchView
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.FixedSize
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.MaxSize
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.sketch.util.SketchUtils.Companion.calculateSamplingSize
import com.github.panpf.sketch.util.SketchUtils.Companion.formatSupportBitmapRegionDecoder

/**
 * 和图片尺寸相关的需求的计算器
 */
class ImageSizeCalculator {
    /**
     * 获取 OpenGL 所允许的最大尺寸
     */
    /**
     * 设置 OpenGL 所允许的最大尺寸,用来计算 inSampleSize
     */
    var openGLMaxTextureSize = -1
        get() {
            if (field == -1) {
                field = SketchUtils.openGLMaxTextureSize
            }
            return field
        }

    /**
     * 计算 inSampleSize 的时候将 targetSize 稍微放大一点儿，就是乘以这个倍数，默认值是 1.25f
     */
    var targetSizeScale = 1.1f

    /**
     * 计算 [MaxSize]
     *
     * @param sketchView 你需要根据 [ImageView] 的宽高来计算
     * @return [MaxSize]
     */
    fun calculateImageMaxSize(sketchView: SketchView?): MaxSize? {
        var width = getWidth(
            sketchView,
            checkMaxWidth = true,
            acceptWrapContent = true,
            subtractPadding = false
        )
        var height = getHeight(
            sketchView,
            checkMaxHeight = true,
            acceptWrapContent = true,
            subtractPadding = false
        )
        if (sketchView == null || width <= 0 && height <= 0) {
            return null
        }

        // 因为OpenGL对图片的宽高有上限，因此要限制一下，这里就严格一点不能大于屏幕宽高的1.5倍
        val displayMetrics = sketchView.getResources().displayMetrics
        val maxWidth = (displayMetrics.widthPixels * 1.5f).toInt()
        val maxHeight = (displayMetrics.heightPixels * 1.5f).toInt()
        if (width > maxWidth || height > maxHeight) {
            val widthScale = width.toFloat() / maxWidth
            val heightScale = height.toFloat() / maxHeight
            val finalScale = if (widthScale > heightScale) widthScale else heightScale
            width /= finalScale.toInt()
            height /= finalScale.toInt()
        }
        return MaxSize(width, height)
    }

    /**
     * 获取默认的 [MaxSize]，默认 [MaxSize] 是屏幕宽高的 70%
     *
     * @param context 上下文
     * @return [MaxSize]
     */
    fun getDefaultImageMaxSize(context: Context): MaxSize {
        val displayMetrics = context.resources.displayMetrics
        return MaxSize(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    /**
     * 计算 [FixedSize]
     *
     * @param sketchView 你需要根据 [ImageView] 的宽高来计算
     * @return [FixedSize]
     */
    fun calculateImageFixedSize(sketchView: SketchView): FixedSize? {
        val layoutParams = sketchView.getLayoutParams()
        if (layoutParams == null || layoutParams.width <= 0 || layoutParams.height <= 0) {
            return null
        }
        var fixedWidth =
            layoutParams.width - (sketchView.getPaddingLeft() + sketchView.getPaddingRight())
        var fixedHeight =
            layoutParams.height - (sketchView.getPaddingTop() + sketchView.getPaddingBottom())

        // 限制不能超过OpenGL所允许的最大尺寸
        val maxSize = openGLMaxTextureSize
        if (fixedWidth > maxSize || fixedHeight > maxSize) {
            val finalScale =
                (fixedWidth.toFloat() / maxSize).coerceAtLeast(fixedHeight.toFloat() / maxSize)
            fixedWidth /= finalScale.toInt()
            fixedHeight /= finalScale.toInt()
        }
        return FixedSize(fixedWidth, fixedHeight)
    }

    /**
     * 计算 inSampleSize
     *
     * @param outWidth          原始宽
     * @param outHeight         原始高
     * @param targetWidth       目标宽
     * @param targetHeight      目标高
     * @param smallerThumbnails 是否使用较小的缩略图，当 inSampleSize 为 2 时，强制改为 4
     * @return 合适的 inSampleSize
     */
    fun calculateInSampleSize(
        outWidth: Int,
        outHeight: Int,
        targetWidth: Int,
        targetHeight: Int,
        smallerThumbnails: Boolean
    ): Int {
        var newTargetWidth = (targetWidth * targetSizeScale).toInt()
        var newTargetHeight = (targetHeight * targetSizeScale).toInt()

        // 限制target宽高不能大于OpenGL所允许的最大尺寸
        val maxSize = openGLMaxTextureSize
        if (newTargetWidth > maxSize) {
            newTargetWidth = maxSize
        }
        if (newTargetHeight > maxSize) {
            newTargetHeight = maxSize
        }
        var inSampleSize = 1

        // 如果目标宽高都小于等于0，就别计算了
        if (newTargetWidth <= 0 && newTargetHeight <= 0) {
            return inSampleSize
        }

        // 如果目标宽高都大于等于原始尺寸，也别计算了
        if (newTargetWidth >= outWidth && newTargetHeight >= outHeight) {
            return inSampleSize
        }
        if (newTargetWidth <= 0) {
            // 目标宽小于等于0时，只要高度满足要求即可
            while (calculateSamplingSize(outHeight, inSampleSize) > newTargetHeight) {
                inSampleSize *= 2
            }
        } else if (newTargetHeight <= 0) {
            // 目标高小于等于0时，只要宽度满足要求即可
            while (calculateSamplingSize(outWidth, inSampleSize) > newTargetWidth) {
                inSampleSize *= 2
            }
        } else {
            // 首先限制像素数不能超过目标宽高的像素数
            val maxPixels = (newTargetWidth * newTargetHeight).toLong()
            while (calculateSamplingSize(outWidth, inSampleSize) * calculateSamplingSize(
                    outHeight,
                    inSampleSize
                ) > maxPixels
            ) {
                inSampleSize *= 2
            }

            // 然后限制宽高不能大于OpenGL所允许的最大尺寸
            while (calculateSamplingSize(outWidth, inSampleSize) > maxSize || calculateSamplingSize(
                    outHeight,
                    inSampleSize
                ) > maxSize
            ) {
                inSampleSize *= 2
            }

            // 想要较小的缩略图就将 2 改为 4
            if (smallerThumbnails && inSampleSize == 2) {
                inSampleSize = 4
            }
        }
        return inSampleSize
    }

    /**
     * 根据高度计算是否可以使用阅读模式
     */
    fun canUseReadModeByHeight(imageWidth: Int, imageHeight: Int): Boolean {
        return imageHeight > imageWidth * 2
    }

    /**
     * 根据宽度度计算是否可以使用阅读模式
     */
    fun canUseReadModeByWidth(imageWidth: Int, imageHeight: Int): Boolean {
        return imageWidth > imageHeight * 3
    }

    /**
     * 是否可以使用缩略图模式
     */
    fun canUseThumbnailMode(
        outWidth: Int,
        outHeight: Int,
        resizeWidth: Int,
        resizeHeight: Int
    ): Boolean {
        if (resizeWidth > outWidth && resizeHeight > outHeight) {
            return false
        }
        val resizeScale = resizeWidth.toFloat() / resizeHeight
        val imageScale = outWidth.toFloat() / outHeight
        return resizeScale.coerceAtLeast(imageScale) > resizeScale.coerceAtMost(imageScale) * 1.5f
    }

    /**
     * 根据请求和图片类型判断是否使用更小的缩略图
     */
    fun canUseSmallerThumbnails(loadRequest: LoadRequest, imageType: ImageType): Boolean {
        return loadRequest is DisplayRequest &&
                loadRequest.isUseSmallerThumbnails &&
                formatSupportBitmapRegionDecoder(imageType)
    }

    override fun toString(): String {
        return KEY
    }

    companion object {
        private const val KEY = "ImageSizeCalculator"

        @Suppress("SameParameterValue")
        private fun getWidth(
            sketchView: SketchView?,
            checkMaxWidth: Boolean,
            acceptWrapContent: Boolean,
            subtractPadding: Boolean
        ): Int {
            if (sketchView == null) {
                return 0
            }
            var width = 0
            val params = sketchView.getLayoutParams()
            if (params != null) {
                width = params.width
                if (subtractPadding && width > 0 && width - sketchView.getPaddingLeft() - sketchView.getPaddingRight() > 0) {
                    width -= sketchView.getPaddingLeft() + sketchView.getPaddingRight()
                    return width
                }
            }
            if (width <= 0 && checkMaxWidth) {
                width = getViewFieldValue(sketchView, "mMaxWidth")
            }
            if (width <= 0 && acceptWrapContent && params != null && params.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                width = -1
            }
            return width
        }

        @Suppress("SameParameterValue")
        private fun getHeight(
            sketchView: SketchView?,
            checkMaxHeight: Boolean,
            acceptWrapContent: Boolean,
            subtractPadding: Boolean
        ): Int {
            if (sketchView == null) {
                return 0
            }
            var height = 0
            val params = sketchView.getLayoutParams()
            if (params != null) {
                height = params.height
                if (subtractPadding && height > 0 && height - sketchView.getPaddingTop() - sketchView.getPaddingBottom() > 0) {
                    height -= sketchView.getPaddingTop() + sketchView.getPaddingBottom()
                    return height
                }
            }
            if (height <= 0 && checkMaxHeight) {
                height = getViewFieldValue(sketchView, "mMaxHeight")
            }
            if (height <= 0 && acceptWrapContent && params != null && params.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = -1
            }
            return height
        }

        private fun getViewFieldValue(`object`: Any, fieldName: String): Int {
            var value = 0
            try {
                val field = ImageView::class.java.getDeclaredField(fieldName)
                field.isAccessible = true
                val fieldValue = field[`object`] as Int
                if (fieldValue > 0 && fieldValue < Int.MAX_VALUE) {
                    value = fieldValue
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return value
        }
    }
}