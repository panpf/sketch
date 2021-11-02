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
import com.github.panpf.sketch.request.Resize

/**
 * 圆角矩形图片处理器
 */
class RoundRectImageProcessor @JvmOverloads constructor(
    topLeftRadius: Float, topRightRadius: Float, bottomLeftRadius: Float,
    bottomRightRadius: Float, wrappedImageProcessor: WrappedImageProcessor? = null
) : WrappedImageProcessor(wrappedImageProcessor) {

    val cornerRadius: FloatArray = floatArrayOf(
        topLeftRadius, topLeftRadius,
        topRightRadius, topRightRadius,
        bottomLeftRadius, bottomLeftRadius,
        bottomRightRadius, bottomRightRadius
    )

    /**
     * 创建一个圆角矩形图片处理器
     *
     * @param cornerRadius          圆角角度
     * @param wrappedImageProcessor 嵌套一个图片处理器
     */
    constructor(cornerRadius: Float, wrappedImageProcessor: WrappedImageProcessor?) : this(
        cornerRadius,
        cornerRadius,
        cornerRadius,
        cornerRadius,
        wrappedImageProcessor
    )

    /**
     * 创建一个圆角矩形图片处理器
     *
     * @param cornerRadius 圆角角度
     */
    constructor(cornerRadius: Float) : this(
        cornerRadius,
        cornerRadius,
        cornerRadius,
        cornerRadius,
        null
    )

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
        val resizeCalculator = sketch.configuration.resizeCalculator
        val mapping = resizeCalculator.calculator(
            bitmap.width, bitmap.height,
            resize?.width ?: bitmap.width,
            resize?.height ?: bitmap.height,
            resize?.scaleType,
            resize != null && resize.mode == Resize.Mode.EXACTLY_SAME
        )
        val config = if (lowQualityImage) Bitmap.Config.ARGB_4444 else Bitmap.Config.ARGB_8888
        val bitmapPool = sketch.configuration.bitmapPool
        val roundRectBitmap = bitmapPool.getOrMake(mapping.imageWidth, mapping.imageHeight, config)
        val canvas = Canvas(roundRectBitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = -0x10000

        // 绘制圆角的罩子
        val path = Path()
        path.addRoundRect(
            RectF(0f, 0f, mapping.imageWidth.toFloat(), mapping.imageHeight.toFloat()),
            cornerRadius,
            Path.Direction.CW
        )
        canvas.drawPath(path, paint)

        // 应用遮罩模式并绘制图片
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, mapping.srcRect, mapping.destRect, paint)
        return roundRectBitmap
    }

    override fun onToString(): String {
        return String.format("%s(%s)", "RoundRectImageProcessor", cornerRadius.contentToString())
    }

    override fun onGetKey(): String {
        return String.format("%s(%s)", "RoundRect", cornerRadius.contentToString())
    }
}