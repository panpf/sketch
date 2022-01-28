package com.github.panpf.sketch.decode.transform

import com.github.panpf.sketch.decode.Transformed

class RoundedCornersTransformed(val radiusArray: FloatArray) : Transformed {
    override val key: String = "RoundedCornersTransformed($radiusArray)"
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key
}

fun List<Transformed>.getRoundedCornersTransformed(): RoundedCornersTransformed? =
    find { it is RoundedCornersTransformed } as RoundedCornersTransformed?