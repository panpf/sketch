package com.github.panpf.sketch.decode.resize

import com.github.panpf.sketch.decode.Transformed

class ResizeTransformed constructor(val resize: Resize) : Transformed {

    override val key: String by lazy {
        resize.cacheKey.replace("Resize", "ResizeTransformed")
    }
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key
}

fun List<Transformed>.getResizeTransformed(): ResizeTransformed? =
    find { it is ResizeTransformed } as ResizeTransformed?