package com.github.panpf.sketch.util

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Looper
import android.widget.ImageView.ScaleType


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