package com.github.panpf.sketch.images

import android.net.Uri
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size

object AssetImageFiles {
    val jpeg: AssetImageFile = ComposeResImageFiles.jpeg.toAssetImageFile()
    val png: AssetImageFile = ComposeResImageFiles.png.toAssetImageFile()
    val webp: AssetImageFile = ComposeResImageFiles.webp.toAssetImageFile()
    val bmp: AssetImageFile = ComposeResImageFiles.bmp.toAssetImageFile()
    val svg: AssetImageFile = ComposeResImageFiles.svg.toAssetImageFile()
    val heic: AssetImageFile = ComposeResImageFiles.heic.toAssetImageFile()
    val avif: AssetImageFile = ComposeResImageFiles.avif.toAssetImageFile()
    val avif2: AssetImageFile = ComposeResImageFiles.avif2.toAssetImageFile()
    val animGif: AssetImageFile = ComposeResImageFiles.animGif.toAssetImageFile()
    val animWebp: AssetImageFile = ComposeResImageFiles.animWebp.toAssetImageFile()
    val animHeif: AssetImageFile = ComposeResImageFiles.animHeif.toAssetImageFile()
    val animAvif: AssetImageFile = ComposeResImageFiles.animAvif.toAssetImageFile()
    val mp4: AssetImageFile = ComposeResImageFiles.mp4.toAssetImageFile()
    val rotationMp4: AssetImageFile = ComposeResImageFiles.rotationMp4.toAssetImageFile()
    val longQMSHT: AssetImageFile = ComposeResImageFiles.longQMSHT.toAssetImageFile()
    val longCOMIC: AssetImageFile = ComposeResImageFiles.longCOMIC.toAssetImageFile()
    val clockExifFlipHorizontal: AssetImageFile =
        ComposeResImageFiles.clockExifFlipHorizontal.toAssetImageFile()
    val clockExifFlipVertical: AssetImageFile =
        ComposeResImageFiles.clockExifFlipVertical.toAssetImageFile()
    val clockExifNormal: AssetImageFile = ComposeResImageFiles.clockExifNormal.toAssetImageFile()
    val clockExifRotate90: AssetImageFile =
        ComposeResImageFiles.clockExifRotate90.toAssetImageFile()
    val clockExifRotate180: AssetImageFile =
        ComposeResImageFiles.clockExifRotate180.toAssetImageFile()
    val clockExifRotate270: AssetImageFile =
        ComposeResImageFiles.clockExifRotate270.toAssetImageFile()
    val clockExifTranspose: AssetImageFile =
        ComposeResImageFiles.clockExifTranspose.toAssetImageFile()
    val clockExifTransverse: AssetImageFile =
        ComposeResImageFiles.clockExifTransverse.toAssetImageFile()
    val clockExifUndefined: AssetImageFile =
        ComposeResImageFiles.clockExifUndefined.toAssetImageFile()
    val clockHor: AssetImageFile = ComposeResImageFiles.clockHor.toAssetImageFile()
    val clockVer: AssetImageFile = ComposeResImageFiles.clockVer.toAssetImageFile()
    val number0: AssetImageFile = ComposeResImageFiles.number0.toAssetImageFile()
    val number1: AssetImageFile = ComposeResImageFiles.number1.toAssetImageFile()
    val number2: AssetImageFile = ComposeResImageFiles.number2.toAssetImageFile()
    val number3: AssetImageFile = ComposeResImageFiles.number3.toAssetImageFile()
    val number4: AssetImageFile = ComposeResImageFiles.number4.toAssetImageFile()
    val number5: AssetImageFile = ComposeResImageFiles.number5.toAssetImageFile()
    val number6: AssetImageFile = ComposeResImageFiles.number6.toAssetImageFile()
    val number7: AssetImageFile = ComposeResImageFiles.number7.toAssetImageFile()
    val number8: AssetImageFile = ComposeResImageFiles.number8.toAssetImageFile()
    val number9: AssetImageFile = ComposeResImageFiles.number9.toAssetImageFile()
    val singleFrameGif: AssetImageFile = ComposeResImageFiles.singleFrameGif.toAssetImageFile()

    val bird: AssetImageFile = AssetImageFile(
        name = "bird.jpeg",
        size = Size(1080, 1245),
        length = 122970,
        mimeType = "image/jpeg"
    )
}

class AssetImageFile(
    override val name: String,
    override val uri: String,
    override val size: Size,
    override val length: Long,
    override val mimeType: String,
    override val animated: Boolean = false,
    override val exifOrientation: Int = ExifOrientation.UNDEFINED,
) : ImageFile {

    constructor(
        name: String,
        size: Size,
        length: Long,
        mimeType: String,
        animated: Boolean = false,
        exifOrientation: Int = ExifOrientation.UNDEFINED,
    ) : this(
        name = name,
        uri = newAssetUri(name),
        size = size,
        length = length,
        mimeType = mimeType,
        animated = animated,
        exifOrientation = exifOrientation
    )

    override val imageInfo = ImageInfo(size, mimeType)

    val fileName = Uri.parse(uri).pathSegments.drop(1).joinToString("/")

    override suspend fun toDataSource(context: PlatformContext): DataSource {
        return AssetDataSource(context = context, fileName = fileName)
    }

    override fun toString(): String =
        "AssetResImage(name='$name', size=$size, mimeType='$mimeType', animated=$animated, exifOrientation=$exifOrientation)"
}

fun ComposeResImageFile.toAssetImageFile(): AssetImageFile = AssetImageFile(
    name = this.name,
    uri = this.uri.replace("/compose_resource/", "/android_asset/"),
    size = this.size,
    length = this.length,
    mimeType = this.mimeType,
    animated = this.animated,
    exifOrientation = this.exifOrientation
)