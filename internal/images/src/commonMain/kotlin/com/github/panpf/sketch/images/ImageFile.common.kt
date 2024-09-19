package com.github.panpf.sketch.images

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size

open class ImageFile constructor(
    val uri: String,
    val name: String,
    val size: Size,
    val mimeType: String,
    val animated: Boolean = false,
    val exifOrientation: Int = ExifOrientation.UNDEFINED,
) {
    override fun toString(): String {
        return "ImageFile(uri='$uri', name='$name', size=$size, exifOrientation=$exifOrientation)"
    }
}

class ResourceImageFile constructor(
    val resourceName: String,
    name: String,
    size: Size,
    mimeType: String,
    animated: Boolean = false,
    exifOrientation: Int = ExifOrientation.UNDEFINED
) : ImageFile(
    uri = resourceNameToUri(resourceName),
    name = name,
    size = size,
    mimeType = mimeType,
    animated = animated,
    exifOrientation = exifOrientation,
) {
    override fun toString(): String {
        return "ResourceImageFile(resourceName='$resourceName', uri='$uri', name='$name', size=$size, exifOrientation=$exifOrientation)"
    }
}

expect fun resourceNameToUri(name: String): String

expect fun ResourceImageFile.toDataSource(context: PlatformContext): DataSource