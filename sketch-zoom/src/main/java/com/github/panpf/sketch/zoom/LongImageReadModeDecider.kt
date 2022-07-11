package com.github.panpf.sketch.zoom

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.resize.DefaultLongImageDecider
import com.github.panpf.sketch.resize.LongImageDecider

fun longImageReadMode(
    longImageDecider: LongImageDecider = DefaultLongImageDecider()
): LongImageReadModeDecider = LongImageReadModeDecider(longImageDecider)

class LongImageReadModeDecider(
    val longImageDecider: LongImageDecider = DefaultLongImageDecider()
) : ReadModeDecider {

    override fun should(
        sketch: Sketch, imageWidth: Int, imageHeight: Int, viewWidth: Int, viewHeight: Int
    ): Boolean = longImageDecider.isLongImage(imageWidth, imageHeight, viewWidth, viewHeight)

    override fun toString(): String {
        return "LongImageReadModeDecider(longImageDecider=$longImageDecider)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LongImageReadModeDecider) return false
        if (longImageDecider != other.longImageDecider) return false
        return true
    }

    override fun hashCode(): Int {
        return longImageDecider.hashCode()
    }
}