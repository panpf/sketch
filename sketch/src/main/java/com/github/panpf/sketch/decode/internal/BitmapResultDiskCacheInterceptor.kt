package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.withContext

class BitmapResultDiskCacheInterceptor : DecodeInterceptor<LoadRequest, BitmapDecodeResult> {

    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult>,
    ): BitmapDecodeResult {
        val request = chain.request
        val sketch = chain.sketch
        val resultCacheHelper = newBitmapResultDiskCacheHelper(sketch, request)
        resultCacheHelper?.lock?.lock()
        try {
            return withContext(sketch.decodeTaskDispatcher) {
                resultCacheHelper?.read()
            } ?: chain.proceed(request).apply {
                withContext(sketch.decodeTaskDispatcher) {
                    resultCacheHelper?.write(this@apply)
                }
            }
        } finally {
            resultCacheHelper?.lock?.unlock()
        }
    }

    override fun toString(): String = "BitmapResultDiskCacheInterceptor"

}