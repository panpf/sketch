package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.drew.imaging.ImageMetadataReader
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.util.Size
import okio.buffer
import java.awt.image.BufferedImage
import java.io.IOException
import java.io.InputStream
import javax.imageio.ImageIO
import javax.imageio.ImageReadParam
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream
import kotlin.math.ceil


/* ************************************** sampling ********************************************** */

actual fun getMaxBitmapSize(targetSize: Size): Size {
    return Size(targetSize.width * 2, targetSize.height * 2)
}

/**
 * Calculate the size of the sampled Bitmap, support for BitmapFactory or ImageDecoder
 */
actual fun calculateSampledBitmapSize(
    imageSize: Size,
    sampleSize: Int,
    mimeType: String?
): Size {
    val widthValue = imageSize.width / sampleSize.toDouble()
    val heightValue = imageSize.height / sampleSize.toDouble()
    val width: Int = ceil(widthValue).toInt()
    val height: Int = ceil(heightValue).toInt()
    return Size(width, height)
}

/**
 * Calculate the size of the sampled Bitmap, support for BitmapRegionDecoder
 */
actual fun calculateSampledBitmapSizeForRegion(
    regionSize: Size,
    sampleSize: Int,
    mimeType: String?,
    imageSize: Size?
): Size = calculateSampledBitmapSize(
    imageSize = regionSize,
    sampleSize = sampleSize,
    mimeType = mimeType
)


/* **************************************** decode ********************************************* */

fun DataSource.decodeImage(block: (ImageReadParam.() -> Unit)? = null): BufferedImage {
    openSource().buffer().inputStream().use { inputStream ->
        val imageStream = ImageIO.createImageInputStream(inputStream)
        val imageReader = ImageIO.getImageReaders(imageStream).next().apply {
            input = imageStream
        }
        val readParam = imageReader.defaultReadParam.apply {
            block?.invoke(this)
        }
        return imageReader.read(0, readParam)
    }
}

@Throws(IOException::class)
fun DataSource.readImageInfoWithImageReader(ignoreExifOrientation: Boolean = false): ImageInfo {
    val inputStream: InputStream = openSource().buffer().inputStream()
    var imageStream: ImageInputStream? = null
    var reader: ImageReader? = null
    try {
        imageStream = ImageIO.createImageInputStream(inputStream)
        reader = ImageIO.getImageReaders(imageStream).next().apply {
            setInput(imageStream, true, true)
        }
        val width = reader.getWidth(0)
        val height = reader.getHeight(0)
        val mimeType = "image/${reader.formatName.lowercase()}"
        val exifOrientation = if (!ignoreExifOrientation) {
            decodeExifOrientation()
        } else {
            ExifOrientation.UNDEFINED
        }
        return ImageInfo(
            width = width,
            height = height,
            mimeType = mimeType,
            exifOrientation = exifOrientation
        )
    } finally {
        reader?.dispose()
        imageStream?.close()
        inputStream.close()
    }
}

@Throws(IOException::class, ImageInvalidException::class)
fun DataSource.readImageInfoWithImageReaderOrThrow(ignoreExifOrientation: Boolean = false): ImageInfo {
    val imageInfo = readImageInfoWithImageReader(ignoreExifOrientation)
    val width = imageInfo.width
    val height = imageInfo.height
    if (width <= 0 || height <= 0) {
        throw ImageInvalidException("Invalid image. size=${width}x${height}")
    }
    return imageInfo
}

@WorkerThread
fun DataSource.readImageInfoWithImageReaderOrNull(ignoreExifOrientation: Boolean = false): ImageInfo? =
    try {
        readImageInfoWithImageReader(ignoreExifOrientation).takeIf {
            it.width > 0 && it.height > 0
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }

@WorkerThread
@Suppress("FoldInitializerAndIfToElvis")
internal fun DataSource.decodeExifOrientation(): Int {
    val inputStream = openSource().buffer().inputStream()
    val metadata = inputStream.use { ImageMetadataReader.readMetadata(it) }
    val directory = metadata.directories
        .find { it.tags.find { tag -> tag.tagName == "Orientation" } != null }
    if (directory == null) {
        return ExifOrientation.UNDEFINED
    }
    val orientationTag = directory
        .tags?.find { it.tagName == "Orientation" }
    if (orientationTag == null) {
        return ExifOrientation.UNDEFINED
    }
    return directory.getInt(orientationTag.tagType)
}

internal fun checkSupportSubsamplingByMimeType(mimeType: String): Boolean =
    !"image/gif".equals(mimeType, true)