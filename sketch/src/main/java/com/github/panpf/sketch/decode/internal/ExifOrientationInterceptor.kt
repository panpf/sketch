package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.request.LoadRequest

class ExifOrientationInterceptor : DecodeInterceptor<LoadRequest, BitmapDecodeResult> {

    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult>
    ): BitmapDecodeResult {
        val bitmapResult = chain.proceed(chain.request)
        // todo support LoadRequest.disabledCorrectExifOrientation
        val imageOrientationCorrector =
            newExifOrientationCorrectorWithExifOrientation(bitmapResult.imageInfo.exifOrientation)
        return if (imageOrientationCorrector != null) {
            val bitmapPool = chain.sketch.bitmapPool
            val bitmap = bitmapResult.bitmap
            val newBitmap = imageOrientationCorrector.rotateBitmap(bitmap, bitmapPool)
            if (newBitmap !== bitmap) {
                bitmapPool.free(bitmap)
                bitmapResult.new(newBitmap) {
                    addTransformed(ExifOrientationTransformed(imageOrientationCorrector.exifOrientation))
                }
            } else {
                bitmapResult
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