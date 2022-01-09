package com.github.panpf.sketch.decode.transform.internal

import com.github.panpf.sketch.Interceptor
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.withContext

class TransformationInterceptor : Interceptor<LoadRequest, BitmapDecodeResult> {

    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<LoadRequest, BitmapDecodeResult>,
    ): BitmapDecodeResult {
        val request = chain.request
        val result = chain.proceed(sketch, request)
        val transformations = request.transformations
        return if (transformations?.isNotEmpty() == true) {
            val bitmap = withContext(sketch.decodeTaskDispatcher) {
                var currentBitmap = result.bitmap
                transformations.forEach {
                    val newBitmap = it.transform(sketch, request, currentBitmap)
                    if (newBitmap !== currentBitmap) {
                        val oldBitmap = currentBitmap
                        currentBitmap = newBitmap
                        sketch.bitmapPoolHelper.freeBitmapToPool(oldBitmap)
                    }
                }
                currentBitmap
            }
            BitmapDecodeResult(bitmap, result.info, result.from)
        } else {
            result
        }
    }
}