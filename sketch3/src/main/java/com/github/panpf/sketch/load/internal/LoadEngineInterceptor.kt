package com.github.panpf.sketch.load.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.ImageResult
import com.github.panpf.sketch.common.Interceptor
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.load.LoadRequest
import com.github.panpf.sketch.load.LoadResult
import kotlinx.coroutines.withContext

class LoadEngineInterceptor : Interceptor<LoadRequest, LoadResult> {

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<LoadRequest, LoadResult>,
        extras: RequestExtras<LoadRequest, LoadResult>?
    ): LoadResult {
        val request = chain.request

        val fetcher = sketch.componentRegistry.newFetcher(
            sketch, request, extras as RequestExtras<ImageRequest, ImageResult>?
        )
        val fetchResult = fetcher.fetch()
        val decoder =
            sketch.componentRegistry.newDecoder(sketch, request, extras, fetchResult.source)
        val result = withContext(sketch.decodeTaskDispatcher) {
            decoder.decode()
        }

        // todo decode, maxSize resize, exifOrientation

        throw Exception("To achieve")
    }
}