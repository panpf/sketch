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

    override val key: String =
        "RoundedCornersTransformation(${radiusArray.joinToString(separator = ",")})"

    override suspend fun transform(sketch: Sketch, request: ImageRequest, input: Bitmap): TransformResult {
        val bitmapPool = sketch.bitmapPool
        val roundedCornersBitmap =
            bitmapPool.getOrCreate(
                input.width,
                input.height,
                input.config ?: Bitmap.Config.ARGB_8888
            )
        val canvas = Canvas(roundedCornersBitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = -0x10000

        // Draw a rounded mask
        val path = Path()
        path.addRoundRect(
            RectF(0f, 0f, input.width.toFloat(), input.height.toFloat()),
            radiusArray,
            Path.Direction.CW
        )
        canvas.drawPath(path, paint)

        // Apply mask mode and draw the image
        paint.xfermode = PorterDuffXfermode(SRC_IN)
        val rect = Rect(0, 0, input.width, input.height)
        canvas.drawBitmap(input, rect, rect, paint)
        return TransformResult(roundedCornersBitmap, RoundedCornersTransformed(radiusArray))
    }
}

class RoundedCornersTransformed(val radiusArray: FloatArray) : Transformed {

    override val key: String by lazy { toString() }
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = "RoundedCornersTransformed($radiusArray)"

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