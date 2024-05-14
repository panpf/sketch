package com.github.panpf.sketch.images

import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.util.Size

open class MyImage(
    val uri: String,
    val name: String,
    val size: Size,
    val exifOrientation: Int = ExifOrientationHelper.UNDEFINED,
)

class MyResourceImage(
    val fileName: String,
    name: String,
    size: Size,
    exifOrientation: Int = ExifOrientationHelper.UNDEFINED
) : MyImage(
    uri = nameToUri(fileName),
    name = name,
    size = size,
    exifOrientation = exifOrientation,
)