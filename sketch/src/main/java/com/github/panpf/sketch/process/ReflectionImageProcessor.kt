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
import android.graphics.Shader.TileMode
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.Resize
import java.util.*

/**
 * 倒影图片处理器
 */
class ReflectionImageProcessor
/**
 * 创建一个倒影图片处理器
 *
 * @param reflectionSpacing 倒影和图片之间的距离
 * @param reflectionScale   倒影的高度所占原图高度比例，取值为 0 到 1
 */ @JvmOverloads constructor(
    val reflectionSpacing: Int = DEFAULT_REFLECTION_SPACING,
    val reflectionScale: Float = DEFAULT_REFLECTION_SCALE,
    wrappedImageProcessor: WrappedImageProcessor? = null
) : WrappedImageProcessor(wrappedImageProcessor) {

    /**
     * 创建一个倒影图片处理器，默认倒影和图片之间的距离是 2 个像素，倒影的高度所占原图高度比例是 0.3
     *
     * @param wrappedImageProcessor 嵌套一个图片处理器
     */
    constructor(wrappedImageProcessor: WrappedImageProcessor?) : this(
        DEFAULT_REFLECTION_SPACING, DEFAULT_REFLECTION_SCALE, wrappedImageProcessor
    )

    override fun onProcess(
        sketch: Sketch,
        bitmap: Bitmap,
        resize: Resize?,
        lowQualityImage: Boolean
    ): Bitmap {
        if (bitmap.isRecycled) {
            return bitmap
        }
        val srcHeight = bitmap.height
        val reflectionHeight = (srcHeight * reflectionScale).toInt()
        val reflectionTop = srcHeight + reflectionSpacing
        val config = if (lowQualityImage) Bitmap.Config.ARGB_4444 else Bitmap.Config.ARGB_8888
        val bitmapPool = sketch.configuration.bitmapPool
        val reflectionBitmap =
            bitmapPool.getOrMake(bitmap.width, reflectionTop + reflectionHeight, config)

        // 在上半部分绘制原图
        val canvas = Canvas(reflectionBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, null)

        // 在下半部分绘制倒影
        val matrix = Matrix()
        matrix.postScale(1f, -1f)
        matrix.postTranslate(0f, (srcHeight + reflectionTop).toFloat())
        canvas.drawBitmap(bitmap, matrix, null)

        // 在倒影部分绘制半透明遮罩，让倒影部分产生半透明渐变的效果
        val paint = Paint()
        paint.shader = LinearGradient(
            0f,
            reflectionTop.toFloat(),
            0f,
            reflectionBitmap.height.toFloat(),
            0x70ffffff,
            0x00ffffff,
            TileMode.CLAMP
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawRect(
            0f,
            reflectionTop.toFloat(),
            reflectionBitmap.width.toFloat(),
            reflectionBitmap.height.toFloat(),
            paint
        )
        return reflectionBitmap
    }

    override fun onToString(): String {
        return String.format(
            Locale.US,
            "%s(scale=%s,spacing=%d)",
            "ReflectionImageProcessor",
            reflectionScale,
            reflectionSpacing
        )
    }

    override fun onGetKey(): String {
        return String.format(
            Locale.US,
            "%s(scale=%s,spacing=%d)",
            "Reflection",
            reflectionScale,
            reflectionSpacing
        )
    }

    companion object {
        private const val DEFAULT_REFLECTION_SPACING = 2
        private const val DEFAULT_REFLECTION_SCALE = 0.3f
    }
    /**
     * 创建一个倒影图片处理器
     *
     * @param reflectionSpacing     倒影和图片之间的距离
     * @param reflectionScale       倒影的高度所占原图高度比例，取值为 0 到 1
     * @param wrappedImageProcessor 嵌套一个图片处理器
     */
    /**
     * 创建一个倒影图片处理器，默认倒影和图片之间的距离是 2 个像素，倒影的高度所占原图高度比例是 0.3
     */
}