package com.github.panpf.sketch.load.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.Interceptor
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.load.LoadRequest
import com.github.panpf.sketch.load.LoadResult
import kotlinx.coroutines.withContext

class TransformationInterceptor : Interceptor<LoadRequest, LoadResult> {

    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<LoadRequest, LoadResult>,
        extras: RequestExtras<LoadRequest, LoadResult>?
    ): LoadResult {
        val request = chain.request
        val result = chain.proceed(sketch, request, extras)
        val transformations = request.transformations
        return if (transformations?.isNotEmpty() == true) {
            val bitmap = withContext(sketch.decodeTaskDispatcher) {
                var currentBitmap = result.bitmap
                transformations.forEach {
                    val newBitmap = it.transform(request, currentBitmap)
                    if (newBitmap !== currentBitmap) {
                        currentBitmap = newBitmap
                        newBitmap.recycle() // todo Back on the bitmapPool
                    }
                }
                currentBitmap
            }
            LoadResult(bitmap, result.info, result.from)
        } else {
            result
        }
    }
}