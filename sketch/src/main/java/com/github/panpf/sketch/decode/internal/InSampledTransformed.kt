package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.Transformed

class InSampledTransformed(val inSampleSize: Int) : Transformed {
    override val key: String = "InSampledTransformed($inSampleSize)"
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key
}

fun List<Transformed>.getInSampledTransformed(): InSampledTransformed? =
    find { it is InSampledTransformed } as InSampledTransformed?