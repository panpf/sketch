package com.github.panpf.sketch.decode.transform

import com.github.panpf.sketch.decode.Transformed

class BlurTransformed(val radius: Int, val maskColor: Int?) : Transformed {
    override val key: String = "BlurTransformed($radius,${maskColor ?: -1})"
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key
}

fun List<Transformed>.getBlurTransformed(): BlurTransformed? =
    find { it is BlurTransformed } as BlurTransformed?