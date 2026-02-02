package com.github.panpf.sketch.images

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size

object AssetImageFiles {

    val bird: AssetImageFile = AssetImageFile(
        name = "bird.jpeg",
        size = Size(1080, 1245),
        length = 122970,
        mimeType = "image/jpeg"
    )
    val animGif: AssetImageFile = AssetImageFile(
        name = "sample_anim.gif",
        size = Size(480, 480),
        length = 76938,
        mimeType = "image/gif",
        animated = true
    )
}

class AssetImageFile(
    override val name: String,
    override val size: Size,
    override val length: Long,
    override val mimeType: String,
    override val animated: Boolean = false,
    override val exifOrientation: Int = ExifOrientation.UNDEFINED,
) : ImageFile {

    override val uri = newAssetUri(name)

    override val imageInfo = ImageInfo(size, mimeType)

    override suspend fun toDataSource(context: PlatformContext): DataSource {
        return AssetDataSource(context = context, fileName = name)
    }

    override fun toString(): String =
        "AssetResImage(name='$name', size=$size, mimeType='$mimeType', animated=$animated, exifOrientation=$exifOrientation)"
}