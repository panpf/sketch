package com.github.panpf.sketch.transform

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.Keep
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.fastGaussianBlur
import com.github.panpf.sketch.util.safeConfig
import org.json.JSONObject

class BlurTransformation constructor(
    /** Blur radius */
    @IntRange(from = 0, to = 100)
    val radius: Int = 15,

    /** If the Bitmap has transparent pixels, it will force the Bitmap to add an opaque background color and then blur it */
    @ColorInt
    val hasAlphaBitmapBgColor: Int? = Color.BLACK,

    /** Overlay the blurred image with a layer of color, often useful when using images as a background */
    @ColorInt
    val maskColor: Int? = null,
) : Transformation {

    init {
        require(radius in 1..100) {
            "Radius must range from 1 to 100: $radius"
        }
        require(hasAlphaBitmapBgColor == null || Color.alpha(hasAlphaBitmapBgColor) == 255) {
            "hasAlphaBitmapBgColor must be not transparent"
        }
    }

    override val key: String =
        "BlurTransformation(${radius},$hasAlphaBitmapBgColor,$maskColor)"

    override suspend fun transform(
        sketch: Sketch,
        request: ImageRequest,
        input: Bitmap
    ): TransformResult {
        // Transparent pixels cannot be blurred
        val compatAlphaBitmap = if (hasAlphaBitmapBgColor != null && input.hasAlpha()) {
            val bitmap = sketch.bitmapPool.getOrCreate(input.width, input.height, input.safeConfig)
            val canvas = Canvas(bitmap)
            canvas.drawColor(hasAlphaBitmapBgColor)
            canvas.drawBitmap(input, 0f, 0f, null)
            bitmap
        } else {
            input
        }
        if (compatAlphaBitmap !== input) {
            sketch.bitmapPool.free(input)
        }

        val outBitmap = fastGaussianBlur(compatAlphaBitmap, radius)
        maskColor?.let {
            Canvas(outBitmap).drawColor(it)
        }
        return TransformResult(outBitmap, BlurTransformed(radius, hasAlphaBitmapBgColor, maskColor))
    }

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlurTransformation) return false

        if (radius != other.radius) return false
        if (hasAlphaBitmapBgColor != other.hasAlphaBitmapBgColor) return false
        if (maskColor != other.maskColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = radius
        result = 31 * result + (hasAlphaBitmapBgColor ?: 0)
        result = 31 * result + (maskColor ?: 0)
        return result
    }
}

class BlurTransformed constructor(
    val radius: Int,
    val hasAlphaBitmapBgColor: Int?,
    val maskColor: Int?
) :
    Transformed {

    override val key: String by lazy { "BlurTransformed($radius,$hasAlphaBitmapBgColor,$maskColor)" }
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlurTransformed) return false

        if (radius != other.radius) return false
        if (hasAlphaBitmapBgColor != other.hasAlphaBitmapBgColor) return false
        if (maskColor != other.maskColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = radius
        result = 31 * result + (hasAlphaBitmapBgColor ?: 0)
        result = 31 * result + (maskColor ?: 0)
        return result
    }

    override fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1> {
        @Suppress("UNCHECKED_CAST")
        return Serializer::class.java as Class<T1>
    }

    @Keep
    class Serializer : JsonSerializer<BlurTransformed> {
        override fun toJson(t: BlurTransformed): JSONObject =
            JSONObject().apply {
                put("radius", t.radius)
                put("hasAlphaBitmapBgColor", t.hasAlphaBitmapBgColor)
                put("maskColor", t.maskColor)
            }

        override fun fromJson(jsonObject: JSONObject): BlurTransformed =
            BlurTransformed(
                jsonObject.getInt("radius"),
                jsonObject.optInt("hasAlphaBitmapBgColor", Int.MIN_VALUE).takeIf { it != Int.MIN_VALUE },
                jsonObject.optInt("maskColor", Int.MIN_VALUE).takeIf { it != Int.MIN_VALUE }
            )
    }
}

fun List<Transformed>.getBlurTransformed(): BlurTransformed? =
    find { it is BlurTransformed }.asOrNull()