package com.github.panpf.sketch.decode.transform

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import androidx.annotation.Px
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.safeConfig

class RoundedCornersTransformation(val radiusArray: FloatArray) : Transformation {

    constructor(
        @Px topLeft: Float = 0f,
        @Px topRight: Float = 0f,
        @Px bottomLeft: Float = 0f,
        @Px bottomRight: Float = 0f
    ) : this(
        floatArrayOf(
            topLeft, topLeft,
            topRight, topRight,
            bottomLeft, bottomLeft,
            bottomRight, bottomRight
        )
    )

    constructor(@Px allRadius: Float) : this(
        floatArrayOf(
            allRadius, allRadius,
            allRadius, allRadius,
            allRadius, allRadius,
            allRadius, allRadius
        )
    )

    init {
        require(radiusArray.size == 8) {
            "radiusArray size must be 8"
        }
        require(radiusArray.all { it >= 0 }) {
            "All radius must be >= 0"
        }
    }

    override val cacheKey: String = "RoundedCorners(${radiusArray.joinToString(separator = ",")})"

    override suspend fun transform(sketch: Sketch, request: LoadRequest, input: Bitmap): Bitmap {
        val bitmapPoolHelper = sketch.bitmapPoolHelper
        val roundedCornersBitmap = bitmapPoolHelper.getOrMake(input.width, input.height, input.safeConfig)
        val canvas = Canvas(roundedCornersBitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = -0x10000

        // 绘制圆角的罩子
        val path = Path()
        path.addRoundRect(
            RectF(0f, 0f, input.width.toFloat(), input.height.toFloat()),
            radiusArray,
            Path.Direction.CW
        )
        canvas.drawPath(path, paint)

        // 应用遮罩模式并绘制图片
        paint.xfermode = PorterDuffXfermode(SRC_IN)
        val rect = Rect(0, 0, input.width, input.height)
        canvas.drawBitmap(input, rect, rect, paint)
        return roundedCornersBitmap
    }
}