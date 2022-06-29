package com.github.panpf.sketch.transform

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.Keep
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import com.github.panpf.sketch.util.safeConfig
import org.json.JSONObject

class RotateTransformation(val degrees: Int) : Transformation {

    override val key: String = "RotateTransformation($degrees)"

    override suspend fun transform(
        sketch: Sketch,
        request: ImageRequest,
        input: Bitmap
    ): TransformResult {
        val matrix = Matrix().apply {
            setRotate(degrees.toFloat())
        }
        val newRect = RectF(0f, 0f, input.width.toFloat(), input.height.toFloat()).apply {
            matrix.mapRect(this)
        }
        val newWidth = newRect.width().toInt()
        val newHeight = newRect.height().toInt()

        // If the Angle is not divisible by 90°, the new image will be oblique, so support transparency so that the oblique part is not black
        var config = input.safeConfig
        if (degrees % 90 != 0 && config != Bitmap.Config.ARGB_8888) {
            config = Bitmap.Config.ARGB_8888
        }
        val result = sketch.bitmapPool.getOrCreate(newWidth, newHeight, config)
        matrix.postTranslate(-newRect.left, -newRect.top)
        val canvas = Canvas(result)
        val paint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(input, matrix, paint)
        return TransformResult(result, RotateTransformed(degrees))
    }

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RotateTransformation) return false

        if (degrees != other.degrees) return false

        return true
    }

    override fun hashCode(): Int {
        return degrees
    }
}

class RotateTransformed(val degrees: Int) : Transformed {

    override val key: String by lazy { "RotateTransformed($degrees)" }
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RotateTransformed) return false

        if (degrees != other.degrees) return false

        return true
    }

    override fun hashCode(): Int {
        return degrees
    }

    override fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1> {
        @Suppress("UNCHECKED_CAST")
        return Serializer::class.java as Class<T1>
    }

    @Keep
    class Serializer : JsonSerializer<RotateTransformed> {
        override fun toJson(t: RotateTransformed): JSONObject =
            JSONObject().apply {
                t.apply {
                    put("degrees", degrees)
                }
            }

        override fun fromJson(jsonObject: JSONObject): RotateTransformed =
            RotateTransformed(
                jsonObject.getInt("degrees")
            )
    }
}

fun List<Transformed>.getRotateTransformed(): RotateTransformed? =
    find { it is RotateTransformed } as RotateTransformed?