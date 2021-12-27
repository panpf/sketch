package com.github.panpf.sketch.load.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.Interceptor
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.load.LoadRequest
import com.github.panpf.sketch.load.LoadResult

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
            var bitmap = result.bitmap
            transformations.forEach {
                val newBitmap = it.transform(request, bitmap)
                if (newBitmap !== bitmap) {
                    bitmap = newBitmap
                    newBitmap.recycle()
                    // todo Back on the bitmapPool
                }
            }
            LoadResult(bitmap, result.info, result.from)
        } else {
            result
        }
    }
}