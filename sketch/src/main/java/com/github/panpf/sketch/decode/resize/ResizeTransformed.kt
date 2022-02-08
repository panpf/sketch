package com.github.panpf.sketch.decode.resize

import com.github.panpf.sketch.decode.Transformed

class ResizeTransformed constructor(val resize: Resize) : Transformed {

    override val key: String by lazy {
        val newSize = resize.newSize
        val precisionDecider = resize.precisionDecider
        val scale = resize.scale
        if (newSize is RealNewSize) {
            "ResizeTransformed(${newSize.size.width}x${newSize.size.height},${precisionDecider},${scale})"
        } else {
            "ResizeTransformed(${newSize},${precisionDecider},${scale})"
        }
    }
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key
}

fun List<Transformed>.getResizeTransformed(): ResizeTransformed? =
    find { it is ResizeTransformed } as ResizeTransformed?