package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.request.LoadRequest

class ExifOrientationCorrectInterceptor : DecodeInterceptor<LoadRequest, BitmapDecodeResult> {

    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult>
    ): BitmapDecodeResult {
        val bitmapResult = chain.proceed(chain.request)
        val imageOrientationCorrector =
            ExifOrientationCorrector.fromExifOrientation(bitmapResult.info.exifOrientation)
        return if (imageOrientationCorrector != null) {
            val bitmapPoolHelper = chain.sketch.bitmapPoolHelper
            val bitmap = bitmapResult.bitmap
            val newBitmap = imageOrientationCorrector.rotateBitmap(bitmap, bitmapPoolHelper)
            if (newBitmap !== bitmap) {
                bitmapPoolHelper.freeBitmapToPool(bitmap)
                BitmapDecodeResult(newBitmap, bitmapResult.info, bitmapResult.from, true)
            } else {
                bitmapResult
            }
        } else {
            bitmapResult
        }
    }
}