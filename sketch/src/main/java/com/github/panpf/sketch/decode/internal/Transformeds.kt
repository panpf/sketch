package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.resize.Resize

fun createInSampledTransformed(inSampleSize: Int) = "InSampledTransformed($inSampleSize)"

fun List<String>.getInSampledTransformed(): String? =
    find { it.startsWith("InSampledTransformed(") }

fun createExifOrientationTransformed(exifOrientation: Int) =
    "ExifOrientationTransformed(${exifOrientationName(exifOrientation)})"

fun List<String>.getExifOrientationTransformed(): String? =
    find { it.startsWith("ExifOrientationTransformed(") }

fun createResizeTransformed(resize: Resize) =
    "ResizeTransformed(${resize.key})"

fun List<String>.getResizeTransformed(): String? =
    find { it.startsWith("ResizeTransformed(") }