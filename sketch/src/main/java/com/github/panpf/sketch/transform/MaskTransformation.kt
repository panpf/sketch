package com.github.panpf.sketch.transform

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffXfermode
import androidx.annotation.ColorInt
import androidx.annotation.Keep
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import com.github.panpf.sketch.util.UnknownException
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.safeConfig
import org.json.JSONObject

class MaskTransformation(
    /** Overlay the blurred image with a layer of color, often useful when using images as a background */
    @ColorInt
    val maskColor: Int
) : Transformation {

    override val key: String = "MaskTransformation($maskColor)"

    override fun toString(): String = key

    override suspend fun transform(
        sketch: Sketch,
        request: ImageRequest,
        input: Bitmap
    ): TransformResult {
        val bitmapPool: BitmapPool = sketch.bitmapPool

        val maskBitmap: Bitmap
        var isNewBitmap = false
        if (input.isMutable) {
            maskBitmap = input
        } else {
            maskBitmap = bitmapPool.getOrCreate(input.width, input.height, input.safeConfig)
            isNewBitmap = true
        }

        val canvas = Canvas(maskBitmap)

        if (isNewBitmap) {
            canvas.drawBitmap(input, 0f, 0f, null)
        }

        val paint = Paint()
        paint.color = maskColor
        paint.xfermode = null

        val saveCount = canvas.saveLayer(
            0f,
            0f,
            input.width.toFloat(),
            input.height.toFloat(),
            paint,
            Canvas.ALL_SAVE_FLAG
        )

        canvas.drawBitmap(input, 0f, 0f, null)

        paint.xfermode = PorterDuffXfermode(SRC_IN)
        canvas.drawRect(0f, 0f, input.width.toFloat(), input.height.toFloat(), paint)

        canvas.restoreToCount(saveCount)

        return TransformResult(maskBitmap, MaskTransformed(maskColor))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaskTransformation) return false

        if (maskColor != other.maskColor) return false

        return true
    }

    override fun hashCode(): Int {
        return maskColor
    }
}

class MaskTransformed(@ColorInt val maskColor: Int) : Transformed {

    override val key: String by lazy { "MaskTransformed(${maskColor})" }

    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaskTransformed) return false

        if (maskColor != other.maskColor) return false

        return true
    }

    override fun hashCode(): Int {
        return maskColor
    }

    override fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1> {
        @Suppress("UNCHECKED_CAST")
        return Serializer::class.java as Class<T1>
    }

    @Keep
    class Serializer : JsonSerializer<MaskTransformed> {
        override fun toJson(t: MaskTransformed): JSONObject =
            JSONObject().apply {
                put("maskColor", t.maskColor)
            }

        override fun fromJson(jsonObject: JSONObject): MaskTransformed =
            MaskTransformed(jsonObject.getInt("maskColor"))
    }
}

fun List<Transformed>.getMaskTransformed(): MaskTransformed? =
    find { it is MaskTransformed }.asOrNull()