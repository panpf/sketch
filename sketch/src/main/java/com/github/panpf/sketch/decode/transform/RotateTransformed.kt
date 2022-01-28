package com.github.panpf.sketch.decode.transform

import com.github.panpf.sketch.decode.Transformed

class RotateTransformed(val degrees: Int) : Transformed {
    override val key: String = "RotateTransformed($degrees)"
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key
}

fun List<Transformed>.getRotateTransformed(): RotateTransformed? =
    find { it is RotateTransformed } as RotateTransformed?