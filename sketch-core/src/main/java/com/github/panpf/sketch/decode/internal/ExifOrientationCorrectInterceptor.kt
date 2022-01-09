package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Interceptor
import com.github.panpf.sketch.Interceptor.Chain
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.request.LoadRequest

class ExifOrientationCorrectInterceptor : Interceptor<LoadRequest, BitmapDecodeResult> {

    override suspend fun intercept(
        sketch: Sketch,
        chain: Chain<LoadRequest, BitmapDecodeResult>
    ): BitmapDecodeResult {
        val bitmapResult = chain.proceed(sketch, chain.request)
        val imageOrientationCorrector =
            ExifOrientationCorrector.fromExifOrientation(bitmapResult.info.exifOrientation)
        return if (imageOrientationCorrector != null) {
            val bitmapPoolHelper = sketch.bitmapPoolHelper
            val bitmap = bitmapResult.bitmap
            val newBitmap = imageOrientationCorrector.rotateBitmap(bitmap, bitmapPoolHelper)
            if (newBitmap !== bitmap) {
                bitmapPoolHelper.freeBitmapToPool(bitmap)
                BitmapDecodeResult(newBitmap, bitmapResult.info, bitmapResult.from)
            } else {
                bitmapResult
            }
        } else {
            bitmapResult
        }
    }
}