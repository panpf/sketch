package com.github.panpf.sketch.load.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ImageData
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.Interceptor
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.load.LoadData
import com.github.panpf.sketch.load.LoadRequest

class LoadEngineInterceptor : Interceptor<LoadRequest, LoadData> {

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<LoadRequest, LoadData>,
        extras: RequestExtras<LoadRequest, LoadData>?
    ): LoadData {
        val request = chain.request

        val fetcher =
            sketch.componentRegistry.newFetcher(
                sketch, request, extras as RequestExtras<ImageRequest, ImageData>?
            )
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
        throw Exception("To achieve")
    }
}