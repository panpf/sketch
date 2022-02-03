package com.github.panpf.sketch.decode.internal

import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.Size

class ExifOrientationInterceptor : DecodeInterceptor<LoadRequest, BitmapDecodeResult> {

    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult>
    ): BitmapDecodeResult {
        val request = chain.request
        val bitmapResult = chain.proceed(request)
        val exifOrientationHelper = if (request.ignoreExifOrientation != true) {
            ExifOrientationHelper(bitmapResult.exifOrientation)
        } else {
            ExifOrientationHelper(ExifInterface.ORIENTATION_UNDEFINED)
        }
        val bitmapPool = chain.sketch.bitmapPool
        val bitmap = bitmapResult.bitmap
        val newBitmap = exifOrientationHelper.applyToBitmap(bitmap, bitmapPool)
        return if (newBitmap != null) {
            bitmapPool.free(bitmap)
            bitmapResult.new(newBitmap) {
                addTransformed(ExifOrientationTransformed(exifOrientationHelper.exifOrientation))
                val newSize = exifOrientationHelper.applyToSize(
                    Size(bitmapResult.imageInfo.width, bitmapResult.imageInfo.height)
                )
                imageInfo(ImageInfo(newSize.width, newSize.height, bitmapResult.imageInfo.mimeType))
            }
        } else {
            bitmapResult
        }
    }

    override fun toString(): String = "ExifOrientationInterceptor"
}

class ExifOrientationTransformed(val exifOrientation: Int) : Transformed {
    override val key: String =
        "ExifOrientationTransformed(${exifOrientationName(exifOrientation)}"
    override val cacheResultToDisk: Boolean = true

    override fun toString(): String = key
}

fun List<Transformed>.getExifOrientationTransformed(): ExifOrientationTransformed? =
    find { it is ExifOrientationTransformed } as ExifOrientationTransformed?