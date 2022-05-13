package com.github.panpf.sketch.stateimage

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat

interface ColorFetcher {
    fun getColor(context: Context): Int
}

class IntColor(@ColorInt val colorInt: Int) : ColorFetcher {

    override fun getColor(context: Context): Int = colorInt

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IntColor

        if (colorInt != other.colorInt) return false

        return true
    }

    override fun hashCode(): Int = colorInt

    override fun toString(): String = "IntColor($colorInt)"
}

class ResColor(@ColorRes val colorRes: Int) : ColorFetcher {

    override fun getColor(context: Context): Int =
        ResourcesCompat.getColor(context.resources, colorRes, null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResColor

        if (colorRes != other.colorRes) return false

        return true
    }

    override fun hashCode(): Int = colorRes

    override fun toString(): String = "ResColor($colorRes)"
}