package com.github.panpf.sketch.transform

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffXfermode
import androidx.annotation.Keep
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.calculateResizeMapping
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.json.JSONObject

class CircleCropTransformation(val scale: Scale = Scale.CENTER_CROP) : Transformation {

    override val key: String = "CircleCropTransformation($scale)"

    override suspend fun transform(request: ImageRequest, input: Bitmap): TransformResult {
        val newSize = input.width.coerceAtMost(input.height)
        val resizeMapping = calculateResizeMapping(
            input.width, input.height, newSize, newSize, SAME_ASPECT_RATIO, scale
        )

        val circleBitmap = request.sketch.bitmapPool.getOrCreate(
            resizeMapping.newWidth, resizeMapping.newHeight, input.config ?: Bitmap.Config.ARGB_8888
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

class CircleCropTransformed(val scale: Scale) : Transformed {

    override val key: String by lazy { toString() }
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = "CircleCropTransformed($scale)"

    override fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1> {
        @Suppress("UNCHECKED_CAST")
        return Serializer::class.java as Class<T1>
    }

    @Keep
    class Serializer : JsonSerializer<CircleCropTransformed> {
        override fun toJson(t: CircleCropTransformed): JSONObject =
            JSONObject().apply {
                t.apply {
                    put("scale", scale.name)
                }
            }

        override fun fromJson(jsonObject: JSONObject): CircleCropTransformed =
            CircleCropTransformed(
                Scale.valueOf(jsonObject.getString("scale"))
            )
    }
}

fun List<Transformed>.getCircleCropTransformed(): CircleCropTransformed? =
    find { it is CircleCropTransformed } as CircleCropTransformed?