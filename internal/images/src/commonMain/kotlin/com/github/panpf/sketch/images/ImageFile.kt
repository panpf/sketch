package com.github.panpf.sketch.images

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size

interface ImageFile {
    val name: String
    val uri: String
    val size: Size
    val length: Long
    val mimeType: String
    val animated: Boolean
    val exifOrientation: Int
    val imageInfo: ImageInfo

    suspend fun toDataSource(context: PlatformContext): DataSource
}