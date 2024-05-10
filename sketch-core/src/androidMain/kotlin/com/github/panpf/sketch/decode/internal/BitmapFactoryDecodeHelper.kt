@file:Suppress("UnnecessaryVariable")

package com.github.panpf.sketch.decode.internal

import android.graphics.BitmapFactory
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toAndroidRect

class BitmapFactoryDecodeHelper(val request: ImageRequest, val dataSource: DataSource) : DecodeHelper {

    override val imageInfo: ImageInfo by lazy { decodeImageInfo() }
    override val supportRegion: Boolean by lazy {
        ImageFormat.parseMimeType(imageInfo.mimeType)?.supportBitmapRegionDecoder() == true
    }

    private val exifOrientation: Int by lazy { dataSource.readExifOrientation() }
    private val exifOrientationHelper by lazy { AndroidExifOrientationHelper(exifOrientation) }

    override fun decode(sampleSize: Int): Image {
        val config = request.newDecodeConfigByQualityParams(imageInfo.mimeType).apply {
            inSampleSize = sampleSize
        }
        val options = config.toBitmapOptions()
        val bitmap = dataSource.decodeBitmap(options)
            ?: throw ImageInvalidException("Invalid image. decode return null")
        val image = bitmap.asSketchImage()
        val correctedImage = exifOrientationHelper.applyToImage(image) ?: image
        return correctedImage
    }

    override fun decodeRegion(region: Rect, sampleSize: Int): Image {
        val config = request.newDecodeConfigByQualityParams(imageInfo.mimeType).apply {
            inSampleSize = sampleSize
        }
        val options = config.toBitmapOptions()
        val originalRegion =
            exifOrientationHelper.applyToRect(region, imageInfo.size, reverse = true)
        val bitmap = dataSource.decodeRegionBitmap(originalRegion.toAndroidRect(), options)
            ?: throw ImageInvalidException("Invalid image. region decode return null")
        val image = bitmap.asSketchImage()
        val correctedImage = exifOrientationHelper.applyToImage(image) ?: image
        return correctedImage
    }

    private fun decodeImageInfo(): ImageInfo {
        val boundOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        dataSource.decodeBitmap(boundOptions)
        val mimeType = boundOptions.outMimeType.orEmpty()
        val imageSize = Size(width = boundOptions.outWidth, height = boundOptions.outHeight)
        val correctedImageSize = exifOrientationHelper.applyToSize(imageSize)
        return ImageInfo(size = correctedImageSize, mimeType = mimeType)
    }

    override fun close() {

    }

    override fun toString(): String {
        return "BitmapFactoryDecodeHelper(uri=${request.uriString}, dataSource=$dataSource)"
    }
}