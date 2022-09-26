package com.github.panpf.sketch.resize.internal

import android.content.Context
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.util.Size

/**
 * Returns the display size of the device screen
 */
data class DisplaySizeResolver constructor(private val context: Context) : SizeResolver {

    override suspend fun size(): Size {
        return context.resources.displayMetrics.let {
            Size(it.widthPixels, it.heightPixels)
        }
    }

    override fun toString(): String {
        return "DisplaySizeResolver($context)"
    }
}