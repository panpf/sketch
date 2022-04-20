package com.github.panpf.sketch.resize

import android.content.Context
import androidx.annotation.Keep
import com.github.panpf.sketch.sketch
import org.json.JSONObject

/**
 * The long image uses the specified precision, use the '[Precision.LESS_PIXELS]' for others.
 *
 * Note: The precision parameter can only be [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO].
 */
fun longImageClipPrecision(precision: Precision): LongImageClipPrecisionDecider =
    LongImageClipPrecisionDecider(precision)

/**
 * The long image uses the specified precision, use the '[Precision.LESS_PIXELS]' for others.
 *
 * Note: The precision parameter can only be [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO].
 */
@Keep
data class LongImageClipPrecisionDecider constructor(
    private val precision: Precision = Precision.SAME_ASPECT_RATIO,
) : PrecisionDecider {

    @Keep
    constructor(jsonObject: JSONObject) : this(
        Precision.valueOf(jsonObject.getString("precision")),
    )

    // todo 搞一个专门 json 序列化的接口，然后需要序列化的都提供这个接口的实现
    override fun serializationToJSON(): JSONObject =
        JSONObject().apply {
            put("precision", precision.name)
        }

    init {
        require(precision == Precision.EXACTLY || precision == Precision.SAME_ASPECT_RATIO) {
            "precision must be EXACTLY or SAME_ASPECT_RATIO"
        }
    }

    override val key: String by lazy { "LongImageClipPrecisionDecider($precision)" }

    override fun get(
        context: Context, imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Precision {
        val longImageDecider = context.sketch.longImageDecider
        return if (longImageDecider.isLongImage(imageWidth, imageHeight, resizeWidth, resizeHeight))
            precision else Precision.LESS_PIXELS
    }

    override fun toString(): String = "LongImageClipPrecisionDecider($precision)"
}