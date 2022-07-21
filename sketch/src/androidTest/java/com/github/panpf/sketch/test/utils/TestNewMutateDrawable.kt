package com.github.panpf.sketch.test.utils

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapper

class TestNewMutateDrawable(drawable: Drawable) : DrawableWrapper(drawable) {

    @SuppressLint("RestrictedApi")
    override fun mutate(): TestNewMutateDrawable {
        val mutateDrawable = wrappedDrawable.mutate()
        return TestNewMutateDrawable(drawable = mutateDrawable)
    }
}