package com.github.panpf.sketch.transform

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffXfermode
import androidx.annotation.Keep
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.calculateResizeMapping
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.safeConfig
import org.json.JSONObject

class CircleCropTransformation(val scale: Scale = Scale.CENTER_CROP) : Transformation {

    override val key: String = "CircleCropTransformation($scale)"

    override suspend fun transform(sketch: Sketch, request: ImageRequest, input: Bitmap): TransformResult {
        val newSize = input.width.coerceAtMost(input.height)
        val resizeMapping = calculateResizeMapping(
            input.width, input.height, newSize, newSize, scale, false
        )

        val circleBitmap = sketch.bitmapPool.getOrCreate(
            resizeMapping.newWidth, resizeMapping.newHeight, input.safeConfig
        )
        val canvas = Canvas(circleBitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = -0x10000

        canvas.drawCircle(
            (resizeMapping.newWidth / 2).toFloat(),
            (resizeMapping.newHeight / 2).toFloat(),
            (resizeMapping.newWidth.coerceAtMost(resizeMapping.newHeight) / 2).toFloat(),
            paint
        )

        paint.xfermode = PorterDuffXfermode(SRC_IN)
        canvas.drawBitmap(input, resizeMapping.srcRect, resizeMapping.destRect, paint)
        return TransformResult(circleBitmap, CircleCropTransformed(scale))
    }
}

@Keep
class CircleCropTransformed(val scale: Scale) : Transformed {
    override val key: String = "CircleCropTransformed($scale)"
    override val cacheResultToDisk: Boolean = true

    @Keep
    constructor(jsonObject: JSONObject) : this(Scale.valueOf(jsonObject.getString("scale")))

    override fun serializationToJSON(): JSONObject =
        JSONObject().apply {
            put("scale", scale.name)
        }

    override fun toString(): String = key
}

fun List<Transformed>.getCircleCropTransformed(): CircleCropTransformed? =
    find { it is CircleCropTransformed } as CircleCropTransformed?