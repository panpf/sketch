package com.github.panpf.sketch.images

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size

object HttpImageFiles {
    val jpeg: ImageFile = ComposeResImageFiles.jpeg.toHttpImageFile()
    val png: ImageFile = ComposeResImageFiles.png.toHttpImageFile()
    val webp: ImageFile = ComposeResImageFiles.webp.toHttpImageFile()
    val bmp: ImageFile = ComposeResImageFiles.bmp.toHttpImageFile()
    val svg: ImageFile = ComposeResImageFiles.svg.toHttpImageFile()
    val heic: ImageFile = ComposeResImageFiles.heic.toHttpImageFile()
    val avif: ImageFile = ComposeResImageFiles.avif.toHttpImageFile()
    val avif2: ImageFile = ComposeResImageFiles.avif2.toHttpImageFile()
    val animGif: ImageFile = ComposeResImageFiles.animGif.toHttpImageFile()
    val animWebp: ImageFile = ComposeResImageFiles.animWebp.toHttpImageFile()
    val animHeif: ImageFile = ComposeResImageFiles.animHeif.toHttpImageFile()
    val animAvif: ImageFile = ComposeResImageFiles.animAvif.toHttpImageFile()
    val mp4: ImageFile = ComposeResImageFiles.mp4.toHttpImageFile()
    val rotationMp4: ImageFile = ComposeResImageFiles.rotationMp4.toHttpImageFile()
    val longQMSHT: ImageFile = ComposeResImageFiles.longQMSHT.toHttpImageFile()
    val longCOMIC: ImageFile = ComposeResImageFiles.longCOMIC.toHttpImageFile()
    val clockExifFlipHorizontal: ImageFile =
        ComposeResImageFiles.clockExifFlipHorizontal.toHttpImageFile()
    val clockExifFlipVertical: ImageFile =
        ComposeResImageFiles.clockExifFlipVertical.toHttpImageFile()
    val clockExifNormal: ImageFile = ComposeResImageFiles.clockExifNormal.toHttpImageFile()
    val clockExifRotate90: ImageFile = ComposeResImageFiles.clockExifRotate90.toHttpImageFile()
    val clockExifRotate180: ImageFile = ComposeResImageFiles.clockExifRotate180.toHttpImageFile()
    val clockExifRotate270: ImageFile = ComposeResImageFiles.clockExifRotate270.toHttpImageFile()
    val clockExifTranspose: ImageFile = ComposeResImageFiles.clockExifTranspose.toHttpImageFile()
    val clockExifTransverse: ImageFile = ComposeResImageFiles.clockExifTransverse.toHttpImageFile()
    val clockExifUndefined: ImageFile = ComposeResImageFiles.clockExifUndefined.toHttpImageFile()
    val clockHor: ImageFile = ComposeResImageFiles.clockHor.toHttpImageFile()
    val clockVer: ImageFile = ComposeResImageFiles.clockVer.toHttpImageFile()
    val number0: ImageFile = ComposeResImageFiles.number0.toHttpImageFile()
    val number1: ImageFile = ComposeResImageFiles.number1.toHttpImageFile()
    val number2: ImageFile = ComposeResImageFiles.number2.toHttpImageFile()
    val number3: ImageFile = ComposeResImageFiles.number3.toHttpImageFile()
    val number4: ImageFile = ComposeResImageFiles.number4.toHttpImageFile()
    val number5: ImageFile = ComposeResImageFiles.number5.toHttpImageFile()
    val number6: ImageFile = ComposeResImageFiles.number6.toHttpImageFile()
    val number7: ImageFile = ComposeResImageFiles.number7.toHttpImageFile()
    val number8: ImageFile = ComposeResImageFiles.number8.toHttpImageFile()
    val number9: ImageFile = ComposeResImageFiles.number9.toHttpImageFile()
    val numbersGif: ImageFile = ComposeResImageFiles.numbersGif.toHttpImageFile()
    val singleFrameGif: ImageFile = ComposeResImageFiles.singleFrameGif.toHttpImageFile()
}

class HttpImageFile(
    override val name: String,
    override val size: Size,
    override val length: Long,
    override val mimeType: String,
    override val animated: Boolean = false,
    override val exifOrientation: Int = ExifOrientation.UNDEFINED
) : ImageFile {

    override val uri: String = "https://panpf.github.io/zoomimage/app/files/$name"

    override val imageInfo: ImageInfo = ImageInfo(size = size, mimeType = mimeType)

    override suspend fun toDataSource(context: PlatformContext): DataSource {
        throw UnsupportedOperationException("HttpImageFile does not support toDataSource")
    }

    override fun toString(): String =
        "HttpImageFile(name='$name', uri='$uri', size=$size, exifOrientation=$exifOrientation)"
}

fun ComposeResImageFile.toHttpImageFile(): HttpImageFile = HttpImageFile(
    name = this.name,
    size = this.size,
    length = this.length,
    mimeType = this.mimeType,
    animated = this.animated,
    exifOrientation = this.exifOrientation
)