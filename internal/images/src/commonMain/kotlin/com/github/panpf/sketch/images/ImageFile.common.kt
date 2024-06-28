package com.github.panpf.sketch.images

import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.util.Size

open class ImageFile(
    val uri: String,
    val name: String,
    val size: Size,
    val exifOrientation: Int = ExifOrientationHelper.UNDEFINED,
)

class ResourceImageFile(
    val resourceName: String,
    name: String,
    size: Size,
    exifOrientation: Int = ExifOrientationHelper.UNDEFINED
) : ImageFile(
    uri = resourceNameToUri(resourceName),
    name = name,
    size = size,
    exifOrientation = exifOrientation,
)

expect fun resourceNameToUri(name: String): String