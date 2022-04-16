package com.github.panpf.sketch.stateimage

import android.content.res.Resources
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat

interface ColorFetcher {
    fun getColor(resources: Resources): Int
}

class IntColor(@ColorInt val colorInt: Int) : ColorFetcher {
    override fun getColor(resources: Resources): Int {
        return colorInt
    }
}

class ResColor(@ColorRes val colorRes: Int) : ColorFetcher {
    override fun getColor(resources: Resources): Int {
        return ResourcesCompat.getColor(resources, colorRes, null)
    }
}