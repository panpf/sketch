package com.github.panpf.sketch3.download.internal

import com.github.panpf.sketch3.Sketch3
import com.github.panpf.sketch3.common.Interceptor
import com.github.panpf.sketch3.common.datasource.ByteArrayDataSource
import com.github.panpf.sketch3.common.datasource.DiskCacheDataSource
import com.github.panpf.sketch3.common.fetch.HttpUriFetcher
import com.github.panpf.sketch3.common.fetch.SourceResult
import com.github.panpf.sketch3.download.ByteArrayDownloadData
import com.github.panpf.sketch3.download.DiskCacheDownloadData
import com.github.panpf.sketch3.download.DownloadErrorResult
import com.github.panpf.sketch3.download.DownloadRequest
import com.github.panpf.sketch3.download.DownloadResult
import com.github.panpf.sketch3.download.DownloadSuccessResult

class DownloadEngineInterceptor : Interceptor<DownloadRequest, DownloadResult> {

    override suspend fun intercept(
        sketch3: Sketch3,
        chain: Interceptor.Chain<DownloadRequest, DownloadResult>
    ): DownloadResult {
        val request = chain.request

        val fetcher = sketch3.componentRegistry.newFetcher(sketch3, request)
        @Suppress("FoldInitializerAndIfToElvis")
        if (fetcher == null) {
            return DownloadErrorResult(IllegalArgumentException("Unsupported uri: ${request.uri}"))
        }
        if (fetcher !is HttpUriFetcher) {
            return DownloadErrorResult(IllegalArgumentException("Download only support HTTP and HTTPS uri: ${request.uri}"))
        }

        val fetchResult = fetcher.fetch()
            ?: return DownloadErrorResult(IllegalStateException("Unable fetch the result: ${request.uri}"))
        return when (val source = (fetchResult as SourceResult).source) {
            is ByteArrayDataSource -> DownloadSuccessResult(ByteArrayDownloadData(source.data))
            is DiskCacheDataSource -> DownloadSuccessResult(DiskCacheDownloadData(source.diskCacheEntry))
            else -> DownloadErrorResult(IllegalArgumentException("The unknown source: ${source::class.qualifiedName}"))
        }
    }
}