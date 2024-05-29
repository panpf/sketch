package com.github.panpf.sketch.util

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Looper
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.resize.Scale
import kotlin.math.max
import kotlin.math.roundToInt


/**
 * Convert to the type specified by the generic, if this is null or cannot be converted return null
 */
internal inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
}

internal fun Any.toHexString(): String = this.hashCode().toString(16)

internal fun requiredMainThread() {
    check(Looper.myLooper() == Looper.getMainLooper()) {
        "This method must be executed in the UI thread"
    }
}

internal val ScaleType.fitScale: Boolean
    get() = this == ScaleType.FIT_START
            || this == ScaleType.FIT_CENTER
            || this == ScaleType.FIT_END
            || this == ScaleType.CENTER_INSIDE


/**
 * Find the last child [Drawable] from the specified Drawable
 */
fun Drawable.findLeafChildDrawable(): Drawable? {
    return when (val drawable = this) {
        is com.github.panpf.sketch.drawable.CrossfadeDrawable -> {
            drawable.end?.findLeafChildDrawable()
        }

        is LayerDrawable -> {
            val layerCount = drawable.numberOfLayers
            if (layerCount > 0) {
                drawable.getDrawable(layerCount - 1).findLeafChildDrawable()
            } else {
                null
            }
        }

        else -> drawable
    }
}

internal fun calculateBounds(srcSize: Size, dstSize: Size, scale: Scale): Rect {
    if (srcSize.isEmpty || dstSize.isEmpty) {
        return Rect(
            /* left = */ 0,
            /* top = */ 0,
            /* right = */ srcSize.width.takeIf { it > 0 } ?: dstSize.width,
            /* bottom = */ srcSize.height.takeIf { it > 0 } ?: dstSize.height
        )
    }

    val srcWidthScaleFactor = dstSize.width.toFloat() / srcSize.width
    val srcHeightScaleFactor = dstSize.height.toFloat() / srcSize.height
    val srcScaleFactor = max(srcWidthScaleFactor, srcHeightScaleFactor)
    val srcScaledWidth = (srcSize.width * srcScaleFactor).roundToInt()
    val srcScaledHeight = (srcSize.height * srcScaleFactor).roundToInt()
    return when (scale) {
        Scale.START_CROP -> {
            Rect(
                /* left = */ 0,
                /* top = */ 0,
                /* right = */ srcScaledWidth,
                /* bottom = */ srcScaledHeight
            )
        }

        Scale.CENTER_CROP -> {
            val left: Int = -(srcScaledWidth - dstSize.width) / 2
            val top: Int = -(srcScaledHeight - dstSize.height) / 2
            Rect(
                /* left = */ left,
                /* top = */ top,
                /* right = */ left + srcScaledWidth,
                /* bottom = */ top + srcScaledHeight,
            )
        }

        Scale.END_CROP -> {
            val left = -(srcScaledWidth - dstSize.width)
            val top = -(srcScaledHeight - dstSize.height)
            Rect(
                /* left = */ left,
                /* top = */ top,
                /* right = */ left + srcScaledWidth,
                /* bottom =*/ top + srcScaledHeight,
            )
        }

        Scale.FILL -> {
            Rect(
                /* left = */0,
                /* top = */0,
                /* right = */dstSize.width,
                /* bottom = */dstSize.height,
            )
        }
    }
}