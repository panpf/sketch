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
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.calculateResizeMapping
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.json.JSONObject
import java.lang.Integer.min

class CircleCropTransformation(val scale: Scale = Scale.CENTER_CROP) : Transformation {

    override val key: String = "CircleCropTransformation($scale)"

    override suspend fun transform(
        sketch: Sketch,
        request: ImageRequest,
        input: Bitmap
    ): TransformResult {
        val newSize = min(input.width, input.height)
        val resizeMapping = calculateResizeMapping(
            input.width, input.height, newSize, newSize, SAME_ASPECT_RATIO, scale
        )
        val config = input.config ?: Bitmap.Config.ARGB_8888
        val outBitmap = sketch.bitmapPool.getOrCreate(
            resizeMapping.newWidth, resizeMapping.newHeight, config
        )
        val paint = Paint().apply {
            isAntiAlias = true
            color = -0x10000
        }
        val canvas = Canvas(outBitmap).apply {
            drawARGB(0, 0, 0, 0)
        }
        canvas.drawCircle(
            resizeMapping.newWidth / 2f,
            resizeMapping.newHeight / 2f,
            min(resizeMapping.newWidth, resizeMapping.newHeight) / 2f,
            paint
        )
        paint.xfermode = PorterDuffXfermode(SRC_IN)
        canvas.drawBitmap(input, resizeMapping.srcRect, resizeMapping.destRect, paint)
        return TransformResult(outBitmap, CircleCropTransformed(scale))
    }

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CircleCropTransformation

        if (scale != other.scale) return false

        return true
    }

    override fun hashCode(): Int {
        return scale.hashCode()
    }
}

class CircleCropTransformed(val scale: Scale) : Transformed {

    override val key: String by lazy { "CircleCropTransformed($scale)" }
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CircleCropTransformed

        if (scale != other.scale) return false

        return true
    }

    override fun hashCode(): Int {
        return scale.hashCode()
    }

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