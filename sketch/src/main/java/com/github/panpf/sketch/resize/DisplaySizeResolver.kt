package com.github.panpf.sketch.resize

import android.content.Context
import com.github.panpf.sketch.util.Size

class DisplaySizeResolver constructor(private val context: Context) : SizeResolver {

    override suspend fun size(): Size {
        return context.resources.displayMetrics.let {
            Size(it.widthPixels, it.heightPixels)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is DisplaySizeResolver && context == other.context
    }

    override fun hashCode() = context.hashCode()

    override fun toString(): String {
        return "DisplaySizeResolver(context=$context)"
    }
}