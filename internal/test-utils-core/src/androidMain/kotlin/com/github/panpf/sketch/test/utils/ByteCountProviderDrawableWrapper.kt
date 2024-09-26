package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import com.github.panpf.sketch.ByteCountProvider

class ByteCountProviderDrawableWrapper(
    drawable: Drawable?,
    override val byteCount: Long
) : DrawableWrapperCompat(drawable), ByteCountProvider