package com.github.panpf.sketch.zoom

import android.content.Context
import com.github.panpf.sketch.sketch

class LongImageReadModeDecider : ReadModeDecider {

    override fun should(
        context: Context, imageWidth: Int, imageHeight: Int, viewWidth: Int, viewHeight: Int
    ): Boolean {
        val longImageDecider = context.sketch.longImageDecider
        return longImageDecider.isLongImage(imageWidth, imageHeight, viewWidth, viewHeight)
    }

    override fun toString(): String {
        return "LongImageReadModeDecider"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

interface ReadModeDecider {

    fun should(
        context: Context,
        imageWidth: Int,
        imageHeight: Int,
        viewWidth: Int,
        viewHeight: Int
    ): Boolean
}