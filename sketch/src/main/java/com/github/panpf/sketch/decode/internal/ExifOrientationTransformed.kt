package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.Transformed

class ExifOrientationTransformed(val exifOrientation: Int) : Transformed {
    override val key: String =
        "ExifOrientationTransformed(${exifOrientationName(exifOrientation)})"
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key
}

fun List<Transformed>.getExifOrientationTransformed(): ExifOrientationTransformed? =
    find { it is ExifOrientationTransformed } as ExifOrientationTransformed?