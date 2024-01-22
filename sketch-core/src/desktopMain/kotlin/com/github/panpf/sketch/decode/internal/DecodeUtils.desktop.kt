package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.drew.imaging.ImageMetadataReader
import com.github.panpf.sketch.datasource.BasedStreamDataSource
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.internal.ImageFormat.PNG
import com.github.panpf.sketch.util.Size
import java.awt.image.BufferedImage
import java.io.IOException
import java.io.InputStream
import javax.imageio.ImageIO
import javax.imageio.ImageReadParam
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream
import kotlin.math.ceil
import kotlin.math.floor


@Throws(IOException::class)
fun BasedStreamDataSource.readImageInfoWithImageReader(ignoreExifOrientation: Boolean = false): ImageInfo {
    val inputStream: InputStream = openInputStream()
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
fun BasedStreamDataSource.readImageInfoWithImageReaderOrThrow(ignoreExifOrientation: Boolean = false): ImageInfo {
    val imageInfo = readImageInfoWithImageReader(ignoreExifOrientation)
    val width = imageInfo.width
    val height = imageInfo.height
    if (width <= 0 || height <= 0) {
        throw ImageInvalidException("Invalid image. size=${width}x${height}")
    }
    return imageInfo
}

@WorkerThread
fun BasedStreamDataSource.readImageInfoWithImageReaderOrNull(ignoreExifOrientation: Boolean = false): ImageInfo? =
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
internal fun BasedStreamDataSource.decodeExifOrientation(): Int {
    val inputStream = openInputStream()
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


/**
 * Calculate the sample size, support for BitmapFactory or ImageDecoder
 */
// TODO Desktop version
actual fun calculateSampleSize(
    imageSize: Size,
    targetSize: Size,
    smallerSizeMode: Boolean,
    mimeType: String?
): Int {
    var sampleSize = 1
    var accepted = false
    while (!accepted) {
        val sampledBitmapSize = calculateSampledBitmapSize(imageSize, sampleSize, mimeType)
        accepted = checkSampledSize(
            sampledSize = sampledBitmapSize,
            targetSize = targetSize,
            smallerSizeMode = smallerSizeMode
        )
        if (!accepted) {
            sampleSize *= 2
        }
    }
    return limitedSampleSizeByMaxBitmapSize(
        sampleSize = sampleSize,
        imageSize = imageSize,
        targetSize = targetSize,
        mimeType = mimeType
    )
}

/**
 * Calculate the sample size, support for BitmapRegionDecoder
 */
// TODO Desktop version
actual fun calculateSampleSizeForRegion(
    regionSize: Size,
    targetSize: Size,
    smallerSizeMode: Boolean,
    mimeType: String?,
    imageSize: Size?
): Int {
    var sampleSize = 1
    var accepted = false
    while (!accepted) {
        val bitmapSize = calculateSampledBitmapSize(
            regionSize, sampleSize, mimeType
        )
        accepted = checkSampledSize(
            sampledSize = bitmapSize,
            targetSize = targetSize,
            smallerSizeMode = smallerSizeMode
        )
        if (!accepted) {
            sampleSize *= 2
        }
    }
    return limitedSampleSizeByMaxBitmapSize(
        sampleSize = sampleSize,
        imageSize = regionSize,
        targetSize = targetSize,
        mimeType = mimeType,
    )
}

/**
 * Calculate the size of the sampled Bitmap, support for BitmapFactory or ImageDecoder
 */
fun calculateSampledBitmapSize(
    imageSize: Size, sampleSize: Int, mimeType: String? = null
): Size {
    val widthValue = imageSize.width / sampleSize.toDouble()
    val heightValue = imageSize.height / sampleSize.toDouble()
    val isPNGFormat = PNG.matched(mimeType)
    val width: Int
    val height: Int
    if (isPNGFormat) {
        width = floor(widthValue).toInt()
        height = floor(heightValue).toInt()
    } else {
        width = ceil(widthValue).toInt()
        height = ceil(heightValue).toInt()
    }
    return Size(width, height)
}

private fun checkSampledSize(
    sampledSize: Size,
    targetSize: Size,
    smallerSizeMode: Boolean
): Boolean {
    return if (smallerSizeMode) {
        sampledSize.width <= targetSize.width && sampledSize.height <= targetSize.height
    } else {
        sampledSize.width * sampledSize.height <= targetSize.width * targetSize.height
    }
}

/*
 * The width and height limit cannot be greater than the maximum size allowed by OpenGL, support for BitmapFactory or ImageDecoder
 */
fun limitedSampleSizeByMaxBitmapSize(
    sampleSize: Int, imageSize: Size, targetSize: Size, mimeType: String? = null
): Int {
    val maxBitmapSize = Size(targetSize.width * 2, targetSize.height * 2)
    var finalSampleSize = sampleSize.coerceAtLeast(1)
    while (true) {
        val bitmapSize = calculateSampledBitmapSize(imageSize, finalSampleSize, mimeType)
        if (bitmapSize.width <= maxBitmapSize.width && bitmapSize.height <= maxBitmapSize.height) {
            break
        } else {
            finalSampleSize *= 2
        }
    }
    return finalSampleSize
}

fun BasedStreamDataSource.decodeImage(block: ImageReadParam.() -> Unit): BufferedImage {
    val inputStream = openInputStream().buffered()
    val imageStream = ImageIO.createImageInputStream(inputStream)
    val imageReader = ImageIO.getImageReaders(imageStream).next().apply {
        input = imageStream
    }
    val readParam = imageReader.defaultReadParam.apply {
        block()
    }
    return imageReader.read(0, readParam)
}