package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.toLogString

class TestKeyDrawable(drawable: Drawable, override val key: String) :
    DrawableWrapperCompat(drawable), Key {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TestKeyDrawable
        if (drawable != other.drawable) return false
        return true
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {
        return "TestKeyDrawable(drawable=${drawable?.toLogString()})"
    }
}