package com.github.panpf.sketch.resize

import androidx.annotation.Keep

/**
 * The long image uses the specified precision, use the '[Precision.LESS_PIXELS]' for others.
 *
 * Note: The precision parameter can only be [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO].
 */
fun longImageClipPrecision(
    precision: Precision,
    longImageDecider: LongImageDecider = DefaultLongImageDecider()
): LongImageClipPrecisionDecider =
    LongImageClipPrecisionDecider(precision, longImageDecider)

/**
 * The long image uses the specified precision, use the '[Precision.LESS_PIXELS]' for others.
 *
 * Note: The precision parameter can only be [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO].
 */
@Keep
class LongImageClipPrecisionDecider constructor(
    val precision: Precision = Precision.SAME_ASPECT_RATIO,
    val longImageDecider: LongImageDecider = DefaultLongImageDecider(),
) : PrecisionDecider {

    init {
        require(precision == Precision.EXACTLY || precision == Precision.SAME_ASPECT_RATIO) {
            "precision must be EXACTLY or SAME_ASPECT_RATIO"
        }
    }

    override val key: String by lazy { "LongImageClip($precision,${longImageDecider.key})" }

    override fun get(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Precision {
        val isLongImage = longImageDecider
            .isLongImage(imageWidth, imageHeight, resizeWidth, resizeHeight)
        return if (isLongImage) precision else Precision.LESS_PIXELS
    }

    override fun toString(): String {
        return "LongImageClipPrecisionDecider(precision=$precision, longImageDecider=$longImageDecider)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LongImageClipPrecisionDecider) return false
        if (precision != other.precision) return false
        if (longImageDecider != other.longImageDecider) return false
        return true
    }

    override fun hashCode(): Int {
        var result = precision.hashCode()
        result = 31 * result + longImageDecider.hashCode()
        return result
    }
}