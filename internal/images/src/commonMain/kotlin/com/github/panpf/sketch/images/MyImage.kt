package com.github.panpf.sketch.images

import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.util.Size

open class MyImage(
    val uri: String,
    val name: String,
    val size: Size,
    @ExifOrientation val exifOrientation: Int = ExifOrientation.UNDEFINED,
)

class MyResourceImage(
    val fileName: String,
    name: String,
    size: Size,
    @ExifOrientation exifOrientation: Int = ExifOrientation.UNDEFINED
) : MyImage(
    uri = nameToUri(fileName),
    name = name,
    size = size,
    exifOrientation = exifOrientation,
)