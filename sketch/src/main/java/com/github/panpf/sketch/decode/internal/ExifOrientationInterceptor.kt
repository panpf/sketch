package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.request.LoadRequest

class ExifOrientationInterceptor : DecodeInterceptor<LoadRequest, BitmapDecodeResult> {

    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult>
    ): BitmapDecodeResult {
        val bitmapResult = chain.proceed(chain.request)
        val imageOrientationCorrector =
            ExifOrientationCorrector.fromExifOrientation(bitmapResult.imageInfo.exifOrientation)
        return if (imageOrientationCorrector != null) {
            val bitmapPoolHelper = chain.sketch.bitmapPoolHelper
            val bitmap = bitmapResult.bitmap
            val newBitmap = imageOrientationCorrector.rotateBitmap(bitmap, bitmapPoolHelper)
            if (newBitmap !== bitmap) {
                bitmapPoolHelper.freeBitmapToPool(bitmap)
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