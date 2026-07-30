package com.github.panpf.sketch.images

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.newKotlinResourceUri
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.KotlinResourceDataSource
import com.github.panpf.sketch.util.Size

object KotlinResImageFiles {

    val bear: KotlinResImageFile = KotlinResImageFile(
        name = "bear.jpeg",
        size = Size(692, 1055),
        length = 195172,
        mimeType = "image/jpeg"
    )

    val liuyifei: KotlinResImageFile = KotlinResImageFile(
        name = "liuyifei.jpeg",
        size = Size(1080, 1439),
        length = 148833,
        mimeType = "image/jpeg"
    )
}

class KotlinResImageFile(
    override val name: String,
    override val size: Size,
    override val length: Long,
    override val mimeType: String,
    override val animated: Boolean = false,
    override val exifOrientation: Int = ExifOrientation.UNDEFINED,
) : ImageFile {

    override val uri = newKotlinResourceUri(name)

    override val imageInfo = ImageInfo(size, mimeType)

    override suspend fun toDataSource(context: PlatformContext): DataSource {
        return KotlinResourceDataSource(name)
    }

    override fun toString(): String =
        "KotlinResImageFile(name='$name', size=$size, mimeType='$mimeType', animated=$animated, exifOrientation=$exifOrientation)"
}