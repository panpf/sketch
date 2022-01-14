package com.github.panpf.sketch.decode.transform.internal

import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.internal.DecodeInterceptor
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.withContext

class TransformationInterceptor : DecodeInterceptor<LoadRequest, BitmapDecodeResult> {

    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult>,
    ): BitmapDecodeResult {
        val sketch = chain.sketch
        val request = chain.request
        val result = chain.proceed(request)
        val transformations = request.transformations
        if (transformations?.isNotEmpty() != true) return result

        val oldBitmap = result.bitmap
        val newBitmap = withContext(sketch.decodeTaskDispatcher) {
            var bitmap = result.bitmap
            transformations.forEach {
                val transformBitmap = it.transform(sketch, request, bitmap)
                if (transformBitmap !== bitmap) {
                    sketch.bitmapPoolHelper.freeBitmapToPool(bitmap)
                    bitmap = transformBitmap
                }
            }
            bitmap
        }
        return if (oldBitmap !== newBitmap) {
            BitmapDecodeResult(newBitmap, result.info, result.from, true)
        } else {
            result
        }
    }
}