package com.github.panpf.sketch.transform

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffXfermode
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.internal.ResizeMapping
import com.github.panpf.sketch.request.internal.LoadableRequest
import com.github.panpf.sketch.util.safeConfig

class CircleCropTransformation(val scaleType: ScaleType = ScaleType.FIT_CENTER) : Transformation {

    override val cacheKey: String = "CircleCrop($scaleType)"

    override suspend fun transform(
        sketch: Sketch,
        request: LoadableRequest,
        input: Bitmap
    ): Bitmap {
        val newSize = input.width.coerceAtMost(input.height)
        val resizeMapping = ResizeMapping.calculator(
            input.width, input.height, newSize, newSize, scaleType, true
        )

        val circleBitmap = sketch.bitmapPoolHelper.bitmapPool.getOrMake(
            resizeMapping.newWidth, resizeMapping.newHeight, input.safeConfig
        )
        val canvas = Canvas(circleBitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = -0x10000

        // 绘制圆形的罩子
        canvas.drawCircle(
            (resizeMapping.newWidth / 2).toFloat(),
            (resizeMapping.newHeight / 2).toFloat(),
            (resizeMapping.newWidth.coerceAtMost(resizeMapping.newHeight) / 2).toFloat(),
            paint
        )

        // 应用遮罩模式并绘制图片
        paint.xfermode = PorterDuffXfermode(SRC_IN)
        canvas.drawBitmap(input, resizeMapping.srcRect, resizeMapping.destRect, paint)
        return circleBitmap
    }
}