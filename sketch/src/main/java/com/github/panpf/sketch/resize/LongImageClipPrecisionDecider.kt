package com.github.panpf.sketch.resize

import androidx.annotation.Keep
import com.github.panpf.sketch.util.format
import org.json.JSONObject

/**
 * The long image uses the specified precision, use the '[Precision.LESS_PIXELS]' for others.
 *
 * Note: The precision parameter can only be [Precision.EXACTLY] or [Precision.KEEP_ASPECT_RATIO].
 */
fun longImageClipPrecision(
    precision: Precision,
    minDifferenceOfAspectRatio: Float = LongImageClipPrecisionDecider.DEFAULT_MIN_DIFFERENCE_OF_ASPECT_RATIO
): LongImageClipPrecisionDecider =
    LongImageClipPrecisionDecider(precision, minDifferenceOfAspectRatio)

/**
 * The long image uses the specified precision, use the '[Precision.LESS_PIXELS]' for others.
 *
 * Note: The precision parameter can only be [Precision.EXACTLY] or [Precision.KEEP_ASPECT_RATIO].
 */
@Keep
data class LongImageClipPrecisionDecider constructor(
    private val precision: Precision = Precision.KEEP_ASPECT_RATIO,
    val minDifferenceOfAspectRatio: Float = DEFAULT_MIN_DIFFERENCE_OF_ASPECT_RATIO
) : PrecisionDecider {

    companion object {
        const val DEFAULT_MIN_DIFFERENCE_OF_ASPECT_RATIO: Float = 3f
    }

    @Keep
    constructor(jsonObject: JSONObject) : this(
        Precision.valueOf(jsonObject.getString("precision")),
        jsonObject.getDouble("minDifferenceOfAspectRatio").toFloat()
    )

    override fun serializationToJSON(): JSONObject =
        JSONObject().apply {
            put("precision", precision.name)
            put("minDifferenceOfAspectRatio", minDifferenceOfAspectRatio)
        }

    init {
        require(precision == Precision.EXACTLY || precision == Precision.KEEP_ASPECT_RATIO) {
            "precision must be EXACTLY or KEEP_ASPECT_RATIO"
        }
    }

    override val key: String by lazy { "LongImageClipPrecisionDecider($precision,$minDifferenceOfAspectRatio)" }

    override fun precision(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Precision = if (isLongImage(imageWidth, imageHeight, resizeWidth, resizeHeight))
        precision else Precision.LESS_PIXELS

    fun isLongImage(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Boolean {
        val imageAspectRatio = imageWidth.toFloat().div(imageHeight).format(1)
        val resizeAspectRatio = resizeWidth.toFloat().div(resizeHeight).format(1)
        val maxAspectRatio = resizeAspectRatio.coerceAtLeast(imageAspectRatio)
        val minAspectRatio = resizeAspectRatio.coerceAtMost(imageAspectRatio)
        return maxAspectRatio >= (minAspectRatio * minDifferenceOfAspectRatio)
    }

    override fun toString(): String =
        "LongImageClipPrecisionDecider($precision,$minDifferenceOfAspectRatio)"
}