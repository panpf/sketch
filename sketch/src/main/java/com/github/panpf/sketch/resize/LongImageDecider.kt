package com.github.panpf.sketch.resize

import androidx.annotation.Keep
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import com.github.panpf.sketch.util.format
import org.json.JSONObject

/**
 * Determine whether it is a long image given the image size and target size
 */
interface LongImageDecider : JsonSerializable {

    val key: String

    /**
     * Determine whether it is a long image given the image size and target size
     */
    fun isLongImage(
        imageWidth: Int, imageHeight: Int, targetWidth: Int, targetHeight: Int
    ): Boolean
}

/**
 * Default [LongImageDecider] implementation
 */
open class DefaultLongImageDecider(
    val smallRatioMultiple: Float = 2.5f,
    val bigRatioMultiple: Float = 5.0f,
) : LongImageDecider {

    override val key: String by lazy { "Default($smallRatioMultiple,$bigRatioMultiple)" }

    /**
     * Determine whether it is a long image given the image size and target size
     *
     * If the directions of image and target are the same, then the aspect ratio of
     * the two is considered as a long image when the aspect ratio reaches [smallRatioMultiple] times,
     * otherwise it is considered as a long image when it reaches [bigRatioMultiple] times
     */
    override fun isLongImage(
        imageWidth: Int, imageHeight: Int, targetWidth: Int, targetHeight: Int
    ): Boolean {
        val imageAspectRatio = imageWidth.toFloat().div(imageHeight).format(2)
        val targetAspectRatio = targetWidth.toFloat().div(targetHeight).format(2)
        val maxAspectRatio = targetAspectRatio.coerceAtLeast(imageAspectRatio)
        val minAspectRatio = targetAspectRatio.coerceAtMost(imageAspectRatio)
        val ratioMultiple = if (imageAspectRatio == 1.0f || targetAspectRatio == 1.0f) {
            // Either one is a square
            smallRatioMultiple
        } else if (imageAspectRatio > 1.0f && targetAspectRatio > 1.0f || (imageAspectRatio < 1.0f && targetAspectRatio < 1.0f)) {
            // They go in the same direction
            smallRatioMultiple
        } else {
            // They don't go in the same direction
            bigRatioMultiple
        }
        return maxAspectRatio >= (minAspectRatio * ratioMultiple)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DefaultLongImageDecider) return false

        if (smallRatioMultiple != other.smallRatioMultiple) return false
        if (bigRatioMultiple != other.bigRatioMultiple) return false

        return true
    }

    override fun hashCode(): Int {
        var result = smallRatioMultiple.hashCode()
        result = 31 * result + bigRatioMultiple.hashCode()
        return result
    }

    override fun toString(): String {
        return "DefaultLongImageDecider(smallRatioMultiple=$smallRatioMultiple, bigRatioMultiple=$bigRatioMultiple)"
    }

    override fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1> {
        @Suppress("UNCHECKED_CAST")
        return Serializer::class.java as Class<T1>
    }

    @Keep
    class Serializer : JsonSerializer<DefaultLongImageDecider> {
        override fun toJson(t: DefaultLongImageDecider): JSONObject =
            JSONObject().apply {
                put("smallRatioMultiple", t.smallRatioMultiple)
                put("bigRatioMultiple", t.bigRatioMultiple)
            }

        override fun fromJson(jsonObject: JSONObject): DefaultLongImageDecider =
            DefaultLongImageDecider(
                smallRatioMultiple = jsonObject.getDouble("smallRatioMultiple").toFloat(),
                bigRatioMultiple = jsonObject.getDouble("bigRatioMultiple").toFloat(),
            )
    }
}