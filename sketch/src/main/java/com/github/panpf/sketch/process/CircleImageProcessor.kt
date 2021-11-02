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
import com.github.panpf.sketch.process.WrappedImageProcessor
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.Resize
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.decode.ResizeCalculator
import com.github.panpf.sketch.decode.ResizeCalculator.Mapping
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.process.CircleImageProcessor

/**
 * 圆形图片处理器
 */
class CircleImageProcessor(wrappedProcessor: WrappedImageProcessor?) :
    WrappedImageProcessor(wrappedProcessor) {

    private constructor() : this(null)

    override val isInterceptResize: Boolean = true

    override fun onProcess(
        sketch: Sketch,
        bitmap: Bitmap,
        resize: Resize?,
        lowQualityImage: Boolean
    ): Bitmap {
        if (bitmap.isRecycled) {
            return bitmap
        }
        val targetWidth = resize?.width ?: bitmap.width
        val targetHeight = resize?.height ?: bitmap.height
        val newBitmapSize = if (targetWidth < targetHeight) targetWidth else targetHeight
        val scaleType = if (resize != null) resize.scaleType else ScaleType.FIT_CENTER
        val resizeCalculator = sketch.configuration.resizeCalculator
        val mapping = resizeCalculator.calculator(
            bitmap.width,
            bitmap.height,
            newBitmapSize,
            newBitmapSize,
            scaleType,
            resize != null && resize.mode == Resize.Mode.EXACTLY_SAME
        )
        val config = if (lowQualityImage) Bitmap.Config.ARGB_4444 else Bitmap.Config.ARGB_8888
        val bitmapPool = sketch.configuration.bitmapPool
        val circleBitmap = bitmapPool.getOrMake(mapping.imageWidth, mapping.imageHeight, config)
        val canvas = Canvas(circleBitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = -0x10000

        // 绘制圆形的罩子
        canvas.drawCircle(
            (mapping.imageWidth / 2).toFloat(),
            (mapping.imageHeight / 2).toFloat(),
            (
                    (if (mapping.imageWidth < mapping.imageHeight) mapping.imageWidth else mapping.imageHeight) / 2).toFloat(),
            paint
        )

        // 应用遮罩模式并绘制图片
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, mapping.srcRect, mapping.destRect, paint)
        return circleBitmap
    }

    override fun onToString(): String {
        return "CircleImageProcessor"
    }

    override fun onGetKey(): String {
        return "Circle"
    }

    companion object {
        var instance: CircleImageProcessor? = null
            get() {
                if (field == null) {
                    synchronized(CircleImageProcessor::class.java) {
                        if (field == null) {
                            field = CircleImageProcessor()
                        }
                    }
                }
                return field
            }
            private set
    }
}