package com.github.panpf.sketch.resize

import android.content.Context
import com.github.panpf.sketch.util.Size

class DisplaySizeResolver(private val context: Context) : SizeResolver {

    private val appContext = context.applicationContext

    override suspend fun size(): Size {
        return appContext.resources.displayMetrics.let {
            Size(it.widthPixels, it.heightPixels)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is DisplaySizeResolver && context == other.context
    }

    override fun hashCode() = context.hashCode()
}