package com.github.panpf.sketch.resize

import androidx.annotation.Keep
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format
import org.json.JSONObject

fun longImageScale(
    longImage: Scale,
    other: Scale,
    minDifferenceOfAspectRatio: Float = LongImageScaleDecider.DEFAULT_MIN_DIFFERENCE_OF_ASPECT_RATIO
): LongImageScaleDecider =
    LongImageScaleDecider(longImage, other, minDifferenceOfAspectRatio)

@Keep
data class LongImageScaleDecider constructor(
    private val longImage: Scale,
    private val other: Scale,
    val minDifferenceOfAspectRatio: Float = DEFAULT_MIN_DIFFERENCE_OF_ASPECT_RATIO
) : ScaleDecider {

    companion object {
        const val DEFAULT_MIN_DIFFERENCE_OF_ASPECT_RATIO: Float = 3f
    }

    @Keep
    constructor(jsonObject: JSONObject) : this(
        Scale.valueOf(jsonObject.getString("longImage")),
        Scale.valueOf(jsonObject.getString("other")),
        jsonObject.getDouble("minDifferenceOfAspectRatio").toFloat()
    )

    override fun serializationToJSON(): JSONObject =
        JSONObject().apply {
            put("longImage", longImage.name)
            put("other", other.name)
            put("minDifferenceOfAspectRatio", minDifferenceOfAspectRatio)
        }

    override fun addExifOrientation(
        exifOrientationHelper: ExifOrientationHelper,
        imageSize: Size
    ): ScaleDecider {
        return LongImageScaleDecider(
            longImage = exifOrientationHelper.addToScale(longImage, imageSize),
            other = exifOrientationHelper.addToScale(other, imageSize),
            minDifferenceOfAspectRatio = minDifferenceOfAspectRatio
        )
    }

    override val key: String by lazy { toString() }

    override fun get(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Scale = if (isLongImage(imageWidth, imageHeight, resizeWidth, resizeHeight))
        longImage else other

    private fun isLongImage(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Boolean {
        val imageAspectRatio = imageWidth.toFloat().div(imageHeight).format(1)
        val resizeAspectRatio = resizeWidth.toFloat().div(resizeHeight).format(1)
        val maxAspectRatio = resizeAspectRatio.coerceAtLeast(imageAspectRatio)
        val minAspectRatio = resizeAspectRatio.coerceAtMost(imageAspectRatio)
        return maxAspectRatio >= (minAspectRatio * minDifferenceOfAspectRatio)
    }

    override fun toString(): String =
        "LongImageScaleDecider($longImage,$other,$minDifferenceOfAspectRatio)"
}