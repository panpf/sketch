package com.github.panpf.sketch.stateimage

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat

interface ColorFetcher {
    fun getColor(context: Context): Int
}

class IntColor(@ColorInt val colorInt: Int) : ColorFetcher {
    override fun getColor(context: Context): Int {
        return colorInt
    }
}

class ResColor(@ColorRes val colorRes: Int) : ColorFetcher {
    override fun getColor(context: Context): Int {
        return ResourcesCompat.getColor(context.resources, colorRes, null)
    }
}