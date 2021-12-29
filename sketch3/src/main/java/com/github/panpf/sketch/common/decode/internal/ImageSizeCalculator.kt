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

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.github.panpf.sketch.load.MaxSize
import com.github.panpf.sketch.util.calculateSamplingSize
import com.github.panpf.sketch.util.openGLMaxTextureSize

/**
 * 和图片尺寸相关的需求的计算器
 */
class ImageSizeCalculator {


    fun calculateImageMaxSize(imageView: ImageView): MaxSize? {
        val width = imageView.layoutParams?.width?.takeIf { it > 0 }
            ?: imageView.maxWidth.takeIf { it > 0 }
            ?: (if (imageView.layoutParams?.width == ViewGroup.LayoutParams.WRAP_CONTENT) -1 else null)
            ?: 0
        val height = imageView.layoutParams?.height?.takeIf { it > 0 }
            ?: imageView.maxHeight.takeIf { it > 0 }
            ?: (if (imageView.layoutParams?.height == ViewGroup.LayoutParams.WRAP_CONTENT) -1 else null)
            ?: 0
        return if (width > 0 || height > 0) {
            // 因为 OpenGL 对图片的宽高有上限，因此要限制一下，这里就严格一点不能大于屏幕宽高的 1.5 倍
            val displayMetrics = imageView.resources.displayMetrics
            val maxWidth = (displayMetrics.widthPixels * 1.5f).toInt()
            val maxHeight = (displayMetrics.heightPixels * 1.5f).toInt()
            if (width > maxWidth || height > maxHeight) {
                val finalScale =
                    (width.toFloat() / maxWidth).coerceAtLeast(height.toFloat() / maxHeight)
                MaxSize((width / finalScale).toInt(), (height / finalScale).toInt())
            } else {
                MaxSize(width, height)
            }
        } else {
            null
        }
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

//    /**
//     * 计算 [FixedSize]
//     *
//     * @param imageView 你需要根据 [ImageView] 的宽高来计算
//     * @return [FixedSize]
//     */
//    fun calculateImageFixedSize(imageView: ImageView): FixedSize? {
//        val layoutParams = imageView.getLayoutParams()
//        if (layoutParams == null || layoutParams.width <= 0 || layoutParams.height <= 0) {
//            return null
//        }
//        var fixedWidth =
//            layoutParams.width - (imageView.getPaddingLeft() + imageView.getPaddingRight())
//        var fixedHeight =
//            layoutParams.height - (imageView.getPaddingTop() + imageView.getPaddingBottom())
//
//        // 限制不能超过OpenGL所允许的最大尺寸
//        val maxSize = openGLMaxTextureSize
//        if (fixedWidth > maxSize || fixedHeight > maxSize) {
//            val finalScale =
//                (fixedWidth.toFloat() / maxSize).coerceAtLeast(fixedHeight.toFloat() / maxSize)
//            fixedWidth /= finalScale.toInt()
//            fixedHeight /= finalScale.toInt()
//        }
//        return FixedSize(fixedWidth, fixedHeight)
//    }

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

//    /**
//     * 根据请求和图片类型判断是否使用更小的缩略图
//     */
//    fun canUseSmallerThumbnails(loadRequest: LoadRequest, imageType: ImageType): Boolean {
//        return loadRequest is DisplayRequest &&
//                loadRequest.isUseSmallerThumbnails &&
//                formatSupportBitmapRegionDecoder(imageType)
//    }

    override fun toString(): String {
        return "ImageSizeCalculator"
    }
}