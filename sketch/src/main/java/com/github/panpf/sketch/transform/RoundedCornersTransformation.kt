package com.github.panpf.sketch.transform

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import androidx.annotation.Keep
import androidx.annotation.Px
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import org.json.JSONObject

class RoundedCornersTransformation constructor(val radiusArray: FloatArray) : Transformation {

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
        require(radiusArray.all { it >= 0f }) {
            "All radius must be >= 0"
        }
    }

    override val key: String =
        "RoundedCornersTransformation(${radiusArray.joinToString(separator = ",")})"

    override suspend fun transform(
        sketch: Sketch,
        request: ImageRequest,
        input: Bitmap
    ): TransformResult {
        val config = input.config ?: Bitmap.Config.ARGB_8888
        val bitmapPool = sketch.bitmapPool
        val newBitmap = bitmapPool.getOrCreate(input.width, input.height, config)
        val paint = Paint().apply {
            isAntiAlias = true
            color = -0x10000
        }
        val canvas = Canvas(newBitmap).apply {
            drawARGB(0, 0, 0, 0)
        }
        val path = Path().apply {
            val rect = RectF(0f, 0f, input.width.toFloat(), input.height.toFloat())
            addRoundRect(rect, radiusArray, Path.Direction.CW)
        }
        canvas.drawPath(path, paint)

        paint.xfermode = PorterDuffXfermode(SRC_IN)
        val rect = Rect(0, 0, input.width, input.height)
        canvas.drawBitmap(input, rect, rect, paint)
        return TransformResult(newBitmap, RoundedCornersTransformed(radiusArray))
    }

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RoundedCornersTransformation) return false

        if (!radiusArray.contentEquals(other.radiusArray)) return false

        return true
    }

    override fun hashCode(): Int {
        return radiusArray.contentHashCode()
    }
}

class RoundedCornersTransformed(val radiusArray: FloatArray) : Transformed {

    override val key: String by lazy {
        "RoundedCornersTransformed(${radiusArray.contentToString()})"
    }
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RoundedCornersTransformed) return false

        if (!radiusArray.contentEquals(other.radiusArray)) return false

        return true
    }

    override fun hashCode(): Int {
        return radiusArray.contentHashCode()
    }

    override fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1> {
        @Suppress("UNCHECKED_CAST")
        return Serializer::class.java as Class<T1>
    }

    @Keep
    class Serializer : JsonSerializer<RoundedCornersTransformed> {
        override fun toJson(t: RoundedCornersTransformed): JSONObject =
            JSONObject().apply {
                t.apply {
                    put("radiusArray", radiusArray.joinToString(separator = ","))
                }
            }

        override fun fromJson(jsonObject: JSONObject): RoundedCornersTransformed =
            RoundedCornersTransformed(
                jsonObject.getString("radiusArray").split(",").map { it.toFloat() }.toFloatArray()
            )
    }
}

fun List<Transformed>.getRoundedCornersTransformed(): RoundedCornersTransformed? =
    find { it is RoundedCornersTransformed } as RoundedCornersTransformed?