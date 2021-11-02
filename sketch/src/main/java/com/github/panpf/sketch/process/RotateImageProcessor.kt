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
package com.github.panpf.sketch.process

import android.graphics.*
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.request.Resize
import java.util.*

/**
 * 旋转图片处理器
 */
class RotateImageProcessor @JvmOverloads constructor(
    private val degrees: Int,
    wrappedImageProcessor: WrappedImageProcessor? = null
) : WrappedImageProcessor(wrappedImageProcessor) {

    override fun onProcess(
        sketch: Sketch,
        bitmap: Bitmap,
        resize: Resize?,
        lowQualityImage: Boolean
    ): Bitmap {
        return if (bitmap.isRecycled || degrees % 360 == 0) {
            bitmap
        } else rotate(
            bitmap,
            degrees,
            sketch.configuration.bitmapPool
        )
    }

    override fun onToString(): String {
        return String.format(Locale.US, "%s(%d)", "RotateImageProcessor", degrees)
    }

    override fun onGetKey(): String? {
        // 0 度或 360 度时不加标识，这样做是为了避免浪费合适的内存缓存
        return if (degrees % 360 == 0) {
            null
        } else String.format(
            Locale.US,
            "%s(%d)",
            "Rotate",
            degrees
        )
    }

    companion object {
        fun rotate(bitmap: Bitmap, degrees: Int, bitmapPool: BitmapPool): Bitmap {
            val matrix = Matrix()
            matrix.setRotate(degrees.toFloat())

            // 根据旋转角度计算新的图片的尺寸
            val newRect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
            matrix.mapRect(newRect)
            val newWidth = newRect.width().toInt()
            val newHeight = newRect.height().toInt()

            // 角度不能整除90°时新图片会是斜的，因此要支持透明度，这样倾斜导致露出的部分就不会是黑的
            var config = if (bitmap.config != null) bitmap.config else Bitmap.Config.ARGB_8888
            if (degrees % 90 != 0 && config != Bitmap.Config.ARGB_8888) {
                config = Bitmap.Config.ARGB_8888
            }
            val result = bitmapPool.getOrMake(newWidth, newHeight, config)
            matrix.postTranslate(-newRect.left, -newRect.top)
            val canvas = Canvas(result)
            val paint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(bitmap, matrix, paint)
            return result
        }
    }
}