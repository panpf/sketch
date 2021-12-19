package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.ImageType

class ImageBasicInfo(val mimeType: String, width: Int, height: Int, exifOrientation: Int) {
    val imageType = ImageType.valueOfMimeType(mimeType)
}