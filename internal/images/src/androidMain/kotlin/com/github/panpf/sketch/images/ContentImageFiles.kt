package com.github.panpf.sketch.images

import android.content.Context
import androidx.core.net.toUri
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ContentImageFiles private constructor() {

    companion object {

        private var instance: ContentImageFiles? = null
        private val lock = Mutex()

        suspend fun with(context: Context): ContentImageFiles {
            return instance ?: lock.withLock {
                instance ?: run {
                    LocalImageFiles.with(context)   // Required
                    ContentImageFiles().also { instance = it }
                }
            }
        }
    }

    val jpeg: ContentImageFile = ComposeResImageFiles.jpeg.toContentImageFile()
    val png: ContentImageFile = ComposeResImageFiles.png.toContentImageFile()
    val webp: ContentImageFile = ComposeResImageFiles.webp.toContentImageFile()
    val bmp: ContentImageFile = ComposeResImageFiles.bmp.toContentImageFile()
    val svg: ContentImageFile = ComposeResImageFiles.svg.toContentImageFile()
    val heic: ContentImageFile = ComposeResImageFiles.heic.toContentImageFile()
    val avif: ContentImageFile = ComposeResImageFiles.avif.toContentImageFile()
    val avif2: ContentImageFile = ComposeResImageFiles.avif2.toContentImageFile()
    val animGif: ContentImageFile = ComposeResImageFiles.animGif.toContentImageFile()
    val animWebp: ContentImageFile = ComposeResImageFiles.animWebp.toContentImageFile()
    val animHeif: ContentImageFile = ComposeResImageFiles.animHeif.toContentImageFile()
    val animAvif: ContentImageFile = ComposeResImageFiles.animAvif.toContentImageFile()
    val mp4: ContentImageFile = ComposeResImageFiles.mp4.toContentImageFile()
    val rotationMp4: ContentImageFile =
        ComposeResImageFiles.rotationMp4.toContentImageFile()
    val longQMSHT: ContentImageFile = ComposeResImageFiles.longQMSHT.toContentImageFile()
    val longCOMIC: ContentImageFile = ComposeResImageFiles.longCOMIC.toContentImageFile()
    val clockExifFlipHorizontal: ContentImageFile =
        ComposeResImageFiles.clockExifFlipHorizontal.toContentImageFile()
    val clockExifFlipVertical: ContentImageFile =
        ComposeResImageFiles.clockExifFlipVertical.toContentImageFile()
    val clockExifNormal: ContentImageFile =
        ComposeResImageFiles.clockExifNormal.toContentImageFile()
    val clockExifRotate90: ContentImageFile =
        ComposeResImageFiles.clockExifRotate90.toContentImageFile()
    val clockExifRotate180: ContentImageFile =
        ComposeResImageFiles.clockExifRotate180.toContentImageFile()
    val clockExifRotate270: ContentImageFile =
        ComposeResImageFiles.clockExifRotate270.toContentImageFile()
    val clockExifTranspose: ContentImageFile =
        ComposeResImageFiles.clockExifTranspose.toContentImageFile()
    val clockExifTransverse: ContentImageFile =
        ComposeResImageFiles.clockExifTransverse.toContentImageFile()
    val clockExifUndefined: ContentImageFile =
        ComposeResImageFiles.clockExifUndefined.toContentImageFile()
    val clockHor: ContentImageFile = ComposeResImageFiles.clockHor.toContentImageFile()
    val clockVer: ContentImageFile = ComposeResImageFiles.clockVer.toContentImageFile()
    val number0: ContentImageFile = ComposeResImageFiles.number0.toContentImageFile()
    val number1: ContentImageFile = ComposeResImageFiles.number1.toContentImageFile()
    val number2: ContentImageFile = ComposeResImageFiles.number2.toContentImageFile()
    val number3: ContentImageFile = ComposeResImageFiles.number3.toContentImageFile()
    val number4: ContentImageFile = ComposeResImageFiles.number4.toContentImageFile()
    val number5: ContentImageFile = ComposeResImageFiles.number5.toContentImageFile()
    val number6: ContentImageFile = ComposeResImageFiles.number6.toContentImageFile()
    val number7: ContentImageFile = ComposeResImageFiles.number7.toContentImageFile()
    val number8: ContentImageFile = ComposeResImageFiles.number8.toContentImageFile()
    val number9: ContentImageFile = ComposeResImageFiles.number9.toContentImageFile()
    val singleFrameGif: ContentImageFile = ComposeResImageFiles.singleFrameGif.toContentImageFile()
}

class ContentImageFile(
    override val name: String,
    override val size: Size,
    override val length: Long,
    override val mimeType: String,
    override val animated: Boolean = false,
    override val exifOrientation: Int = ExifOrientation.UNDEFINED,
) : ImageFile {

    override val uri =
        "content://com.github.panpf.sketch.images.fileprovider/images/${this.name}"

    override val imageInfo: ImageInfo = ImageInfo(size = size, mimeType = mimeType)
    override suspend fun toDataSource(context: PlatformContext): DataSource {
        return ContentDataSource(context, uri.toUri())
    }

    override fun toString(): String =
        "ContentImageFile(name='$name', uri='$uri', size=$size, exifOrientation=$exifOrientation)"
}

fun ComposeResImageFile.toContentImageFile(): ContentImageFile = ContentImageFile(
    name = this.name,
    size = this.size,
    length = this.length,
    mimeType = this.mimeType,
    animated = this.animated,
    exifOrientation = this.exifOrientation
)