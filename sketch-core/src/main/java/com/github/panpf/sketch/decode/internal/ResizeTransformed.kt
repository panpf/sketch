package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.decode.Transformed

class ResizeTransformed(val resize: Resize): Transformed {
    override val key: String = "ResizeTransformed($resize)"
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key
}

fun List<Transformed>.getResizeTransformed(): ResizeTransformed? =
    find { it is ResizeTransformed } as ResizeTransformed?