package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.request.LoadRequest

class ExifOrientationInterceptor : DecodeInterceptor<LoadRequest, BitmapDecodeResult> {

    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult>
    ): BitmapDecodeResult {
        val bitmapResult = chain.proceed(chain.request)
        val imageOrientationCorrector = ExifOrientationHelper(bitmapResult.exifOrientation)
        val bitmapPool = chain.sketch.bitmapPool
        val bitmap = bitmapResult.bitmap
        val newBitmap = imageOrientationCorrector.applyOrientation(bitmap, bitmapPool)
        return if (newBitmap != null) {
            bitmapPool.free(bitmap)
            bitmapResult.new(newBitmap) {
                addTransformed(ExifOrientationTransformed(imageOrientationCorrector.exifOrientation))
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