package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import com.github.panpf.sketch.util.NullableKey
import com.github.panpf.sketch.util.toLogString

class TestNullableKeyDrawable(drawable: Drawable, override val key: String?) :
    DrawableWrapperCompat(drawable), NullableKey {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TestNullableKeyDrawable
        if (drawable != other.drawable) return false
        return true
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {
        return "TestNullableKeyDrawable(drawable=${drawable?.toLogString()})"
    }
}