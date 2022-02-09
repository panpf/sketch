package com.github.panpf.sketch.decode.resize

import com.github.panpf.sketch.decode.Transformed

class ResizeTransformed constructor(val resize: Resize) : Transformed {

    override val key: String by lazy {
        val newSize = resize.newSize
        val precisionDecider = resize.precisionDecider
        val precisionDeciderString = precisionDecider.toString().replace("PrecisionDecider", "")
        val scale = resize.scale
        if (newSize is RealNewSize) {
            "ResizeTransformed(${newSize.size.width}x${newSize.size.height},${precisionDeciderString},${scale})"
        } else {
            "ResizeTransformed(${newSize},${precisionDeciderString},${scale})"
        }
    }
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key
}

fun List<Transformed>.getResizeTransformed(): ResizeTransformed? =
    find { it is ResizeTransformed } as ResizeTransformed?