package com.github.panpf.sketch.images

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.appCacheDirectory
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import okio.buffer
import okio.use

class LocalImageFiles private constructor(cacheDir: Path) {

    companion object {

        private var instance: LocalImageFiles? = null
        private val lock = Mutex()

        suspend fun with(context: PlatformContext): LocalImageFiles {
            return instance ?: lock.withLock {
                instance ?: run {
                    val cacheDir = context.appCacheDirectory()!!.resolve("images")
                    saveImageToExternalFilesDir(
                        context = context,
                        imageFiles = ComposeResImageFiles.values.toList(),
                        cacheDir = cacheDir
                    )
                    LocalImageFiles(cacheDir).also { instance = it }
                }
            }
        }
    }

    val jpeg: LocalImageFile = ComposeResImageFiles.jpeg.toLocalImageFile(cacheDir)
    val png: LocalImageFile = ComposeResImageFiles.png.toLocalImageFile(cacheDir)
    val webp: LocalImageFile = ComposeResImageFiles.webp.toLocalImageFile(cacheDir)
    val bmp: LocalImageFile = ComposeResImageFiles.bmp.toLocalImageFile(cacheDir)
    val svg: LocalImageFile = ComposeResImageFiles.svg.toLocalImageFile(cacheDir)
    val heic: LocalImageFile = ComposeResImageFiles.heic.toLocalImageFile(cacheDir)
    val avif: LocalImageFile = ComposeResImageFiles.avif.toLocalImageFile(cacheDir)
    val avif2: LocalImageFile = ComposeResImageFiles.avif2.toLocalImageFile(cacheDir)
    val animGif: LocalImageFile = ComposeResImageFiles.animGif.toLocalImageFile(cacheDir)
    val animWebp: LocalImageFile = ComposeResImageFiles.animWebp.toLocalImageFile(cacheDir)
    val animHeif: LocalImageFile = ComposeResImageFiles.animHeif.toLocalImageFile(cacheDir)
    val animAvif: LocalImageFile = ComposeResImageFiles.animAvif.toLocalImageFile(cacheDir)
    val mp4: LocalImageFile = ComposeResImageFiles.mp4.toLocalImageFile(cacheDir)
    val rotationMp4: LocalImageFile =
        ComposeResImageFiles.rotationMp4.toLocalImageFile(cacheDir)

    val videos = arrayOf(mp4, rotationMp4)

    val longQMSHT: LocalImageFile = ComposeResImageFiles.longQMSHT.toLocalImageFile(cacheDir)
    val longCOMIC: LocalImageFile = ComposeResImageFiles.longCOMIC.toLocalImageFile(cacheDir)
    val clockExifFlipHorizontal: LocalImageFile =
        ComposeResImageFiles.clockExifFlipHorizontal.toLocalImageFile(cacheDir)
    val clockExifFlipVertical: LocalImageFile =
        ComposeResImageFiles.clockExifFlipVertical.toLocalImageFile(cacheDir)
    val clockExifNormal: LocalImageFile =
        ComposeResImageFiles.clockExifNormal.toLocalImageFile(cacheDir)
    val clockExifRotate90: LocalImageFile =
        ComposeResImageFiles.clockExifRotate90.toLocalImageFile(cacheDir)
    val clockExifRotate180: LocalImageFile =
        ComposeResImageFiles.clockExifRotate180.toLocalImageFile(cacheDir)
    val clockExifRotate270: LocalImageFile =
        ComposeResImageFiles.clockExifRotate270.toLocalImageFile(cacheDir)
    val clockExifTranspose: LocalImageFile =
        ComposeResImageFiles.clockExifTranspose.toLocalImageFile(cacheDir)
    val clockExifTransverse: LocalImageFile =
        ComposeResImageFiles.clockExifTransverse.toLocalImageFile(cacheDir)
    val clockExifUndefined: LocalImageFile =
        ComposeResImageFiles.clockExifUndefined.toLocalImageFile(cacheDir)
    val clockHor: LocalImageFile = ComposeResImageFiles.clockHor.toLocalImageFile(cacheDir)
    val clockVer: LocalImageFile = ComposeResImageFiles.clockVer.toLocalImageFile(cacheDir)
    val number0: LocalImageFile = ComposeResImageFiles.number0.toLocalImageFile(cacheDir)
    val number1: LocalImageFile = ComposeResImageFiles.number1.toLocalImageFile(cacheDir)
    val number2: LocalImageFile = ComposeResImageFiles.number2.toLocalImageFile(cacheDir)
    val number3: LocalImageFile = ComposeResImageFiles.number3.toLocalImageFile(cacheDir)
    val number4: LocalImageFile = ComposeResImageFiles.number4.toLocalImageFile(cacheDir)
    val number5: LocalImageFile = ComposeResImageFiles.number5.toLocalImageFile(cacheDir)
    val number6: LocalImageFile = ComposeResImageFiles.number6.toLocalImageFile(cacheDir)
    val number7: LocalImageFile = ComposeResImageFiles.number7.toLocalImageFile(cacheDir)
    val number8: LocalImageFile = ComposeResImageFiles.number8.toLocalImageFile(cacheDir)
    val number9: LocalImageFile = ComposeResImageFiles.number9.toLocalImageFile(cacheDir)
    val singleFrameGif: LocalImageFile =
        ComposeResImageFiles.singleFrameGif.toLocalImageFile(cacheDir)
}

class LocalImageFile(
    override val name: String,
    override val uri: String,
    override val size: Size,
    override val length: Long,
    override val mimeType: String,
    override val animated: Boolean = false,
    override val exifOrientation: Int = ExifOrientation.UNDEFINED,
) : ImageFile {

    override val imageInfo: ImageInfo = ImageInfo(size = size, mimeType = mimeType)

    override suspend fun toDataSource(context: PlatformContext): DataSource {
        return FileDataSource(uri.toPath())
    }

    override fun toString(): String =
        "LocalImageFile(name='$name', uri='$uri', size=$size, exifOrientation=$exifOrientation)"
}

fun ComposeResImageFile.toLocalImageFile(cacheDir: Path): LocalImageFile =
    LocalImageFile(
        name = this.name,
        uri = cacheDir.resolve(this.name).toString(),
        size = this.size,
        length = this.length,
        mimeType = this.mimeType,
        animated = this.animated,
        exifOrientation = this.exifOrientation
    )

suspend fun saveImageToExternalFilesDir(
    context: PlatformContext,
    imageFiles: List<ComposeResImageFile>,
    cacheDir: Path
): Unit = withContext(ioCoroutineDispatcher()) {
    val fileSystem = FileSystem.SYSTEM
    if (!fileSystem.exists(cacheDir)) {
        fileSystem.createDirectories(cacheDir)
    }
    imageFiles.forEach { imageFile ->
        val file = cacheDir.resolve(imageFile.name)
        if (!fileSystem.exists(file) || (fileSystem.metadataOrNull(file)?.size ?: 0L) <= 0L) {
            val tempFile = "${file}.temp".toPath()
            fileSystem.delete(tempFile)
            try {
                imageFile.toDataSource(context).openSource()
                    .buffer().use { input ->
                        fileSystem.sink(tempFile).buffer().use { output ->
                            output.writeAll(input)
                        }
                    }
                fileSystem.atomicMove(tempFile, file)
            } catch (e: Exception) {
                fileSystem.delete(tempFile)
                throw Exception("Failed to copy ${imageFile.name} to $file", e)
            }
        }
    }
}