package com.github.panpf.sketch.images

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size

open class ImageFile(
    val uri: String,
    val name: String,
    val size: Size,
    val exifOrientation: Int = ExifOrientation.UNDEFINED,
)

class ResourceImageFile(
    val resourceName: String,
    name: String,
    size: Size,
    exifOrientation: Int = ExifOrientation.UNDEFINED
) : ImageFile(
    uri = resourceNameToUri(resourceName),
    name = name,
    size = size,
    exifOrientation = exifOrientation,
)

expect fun resourceNameToUri(name: String): String

expect fun ResourceImageFile.toDataSource(context: PlatformContext): DataSource