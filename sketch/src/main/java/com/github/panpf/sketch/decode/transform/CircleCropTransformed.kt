package com.github.panpf.sketch.decode.transform

import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.decode.Transformed

class CircleCropTransformed(val scale: Resize.Scale) : Transformed {
    override val key: String = "CircleCropTransformed($scale)"
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key
}

fun List<Transformed>.getCircleCropTransformed(): CircleCropTransformed? =
    find { it is CircleCropTransformed } as CircleCropTransformed?