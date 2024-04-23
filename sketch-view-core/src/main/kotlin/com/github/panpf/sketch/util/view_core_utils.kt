package com.github.panpf.sketch.util

import android.widget.ImageView.ScaleType


/**
 * Convert to the type specified by the generic, if this is null or cannot be converted return null
 */
internal inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
}

internal fun Any.toHexString(): String = this.hashCode().toString(16)

internal val ScaleType.fitScale: Boolean
    get() = this == ScaleType.FIT_START
            || this == ScaleType.FIT_CENTER
            || this == ScaleType.FIT_END
            || this == ScaleType.CENTER_INSIDE