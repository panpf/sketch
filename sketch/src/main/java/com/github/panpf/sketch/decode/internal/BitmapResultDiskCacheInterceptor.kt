package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.request.LoadRequest

class BitmapResultDiskCacheInterceptor : DecodeInterceptor<LoadRequest, BitmapDecodeResult> {

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult>,
    ): BitmapDecodeResult {
        val request = chain.request
        val sketch = chain.sketch
        val resultCacheHelper = newBitmapResultDiskCacheHelper(sketch, request)
        resultCacheHelper?.lock?.lock()
        try {
            return resultCacheHelper?.read()
                ?: chain.proceed().apply {
                    resultCacheHelper?.write(this@apply)
                }
        } finally {
            resultCacheHelper?.lock?.unlock()
        }
    }

    override fun toString(): String = "BitmapResultDiskCacheInterceptor"

}