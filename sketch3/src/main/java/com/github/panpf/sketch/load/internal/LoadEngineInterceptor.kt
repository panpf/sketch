package com.github.panpf.sketch.load.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.Interceptor
import com.github.panpf.sketch.common.ProgressListener
import com.github.panpf.sketch.load.LoadErrorResult
import com.github.panpf.sketch.load.LoadRequest
import com.github.panpf.sketch.load.LoadResult

class LoadEngineInterceptor : Interceptor<LoadRequest, LoadResult> {

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<LoadRequest, LoadResult>,
        httpFetchProgressListener: ProgressListener<ImageRequest>?
    ): LoadResult {
        val request = chain.request

        val fetcher =
            sketch.componentRegistry.newFetcher(sketch, request, httpFetchProgressListener)
//        if (fetcher !is HttpUriFetcher) {
//            return LoadErrorResult(IllegalArgumentException("Load only support HTTP and HTTPS uri: ${request.uri}"))
//        }
//
//        val fetchResult = fetcher.fetch()
//            ?: return LoadErrorResult(IllegalStateException("Unable fetch the result: ${request.uri}"))
//
//        return when (val source = fetchResult.source) {
//            is ByteArrayDataSource -> LoadSuccessResult(
//                ByteArrayLoadData(source.data),
//                fetchResult.from
//            )
//            is DiskCacheDataSource -> LoadSuccessResult(
//                DiskCacheLoadData(source.diskCacheEntry),
//                fetchResult.from
//            )
//            else -> LoadErrorResult(IllegalArgumentException("The unknown source: ${source::class.qualifiedName}"))
//        }
        // todo To achieve
        return LoadErrorResult(Exception("To achieve"))
    }
}