package com.github.panpf.sketch

import android.content.Context
import android.net.Uri
import androidx.annotation.AnyThread
import com.github.panpf.sketch.common.*
import com.github.panpf.sketch.common.cache.disk.DiskCache
import com.github.panpf.sketch.common.cache.disk.LruDiskCache
import com.github.panpf.sketch.common.fetch.HttpUriFetcher
import com.github.panpf.sketch.common.http.HttpStack
import com.github.panpf.sketch.common.http.HurlStack
import com.github.panpf.sketch.download.DownloadRequest
import com.github.panpf.sketch.download.DownloadResult
import com.github.panpf.sketch.download.internal.DownloadEngineInterceptor
import com.github.panpf.sketch.download.internal.DownloadExecutor
import kotlinx.coroutines.*
import java.io.File

class Sketch private constructor(
    val appContext: Context,
    val diskCache: DiskCache,
    val componentRegistry: ComponentRegistry,
    val httpStack: HttpStack,
    val downloadInterceptors: List<Interceptor<DownloadRequest, DownloadResult>>,
) {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            SLog.wmt("scope", throwable, "exception")
        }
    )
    private val downloadExecutor = DownloadExecutor(this)

    val repeatTaskFilter = RepeatTaskFilter()
    val singleThreadTaskDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1)
    val httpDownloadTaskDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(10)
    val decodeTaskDispatcher: CoroutineDispatcher = Dispatchers.IO

    init {
        if (diskCache is LruDiskCache) {
            val wrapperErrorCallback = diskCache.errorCallback
            diskCache.errorCallback =
                LruDiskCache.ErrorCallback { dir: File, throwable: Throwable ->
                    wrapperErrorCallback?.onInstallDiskCacheError(dir, throwable)
                    // todo
//                configuration.callback.onError(InstallDiskCacheException(e, cacheDir))
                }
        }
    }

    @AnyThread
    fun enqueueDownload(downloadRequest: DownloadRequest): Disposable<DownloadResult> {
        val job = scope.async(singleThreadTaskDispatcher) {
            downloadExecutor.execute(downloadRequest)
        }
        return OneShotDisposable(job)
    }

    @AnyThread
    fun enqueueDownload(
        uri: Uri,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null
    ): Disposable<DownloadResult> = enqueueDownload(DownloadRequest.new(uri, configBlock))

    @AnyThread
    fun enqueueDownload(
        uriString: String,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null
    ): Disposable<DownloadResult> = enqueueDownload(DownloadRequest.new(uriString, configBlock))

    suspend fun executeDownload(downloadRequest: DownloadRequest): DownloadResult {
        return coroutineScope {
            val job = async(singleThreadTaskDispatcher) {
                downloadExecutor.execute(downloadRequest)
            }
            job.await()
        }
    }

    suspend fun executeDownload(
        uri: Uri,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null
    ): DownloadResult = executeDownload(DownloadRequest.new(uri, configBlock))

    suspend fun executeDownload(
        uriString: String,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null
    ): DownloadResult = executeDownload(DownloadRequest.new(uriString, configBlock))

    companion object {
        fun new(
            context: Context,
            configBlock: (Builder.() -> Unit)? = null
        ): Sketch = Builder(context).apply {
            configBlock?.invoke(this)
        }.build()
    }

    class Builder(context: Context) {

        private val appContext: Context = context.applicationContext
        private var diskCache: DiskCache? = null
        private var componentRegistry: ComponentRegistry? = null
        private var httpStack: HttpStack? = null
        private var downloadInterceptors: MutableList<Interceptor<DownloadRequest, DownloadResult>>? =
            null

        fun diskCache(diskCache: DiskCache): Builder = apply {
            this.diskCache = diskCache
        }

        fun components(components: ComponentRegistry): Builder = apply {
            this.componentRegistry = components
        }

        fun components(block: ComponentRegistry.Builder.() -> Unit): Builder = apply {
            this.componentRegistry = ComponentRegistry.Builder().apply(block).build()
        }

        fun httpStack(httpStack: HttpStack): Builder = apply {
            this.httpStack = httpStack
        }

        fun addDownloadInterceptor(interceptor: Interceptor<DownloadRequest, DownloadResult>): Builder =
            apply {
                this.downloadInterceptors = (downloadInterceptors ?: mutableListOf()).apply {
                    add(interceptor)
                }
            }

        fun build(): Sketch = Sketch(
            appContext = appContext,
            diskCache = diskCache ?: LruDiskCache(appContext),
            componentRegistry = (componentRegistry?.newBuilder()
                ?: ComponentRegistry.Builder()).apply {
                addFetcher(HttpUriFetcher.Factory())
            }.build(),
            httpStack = httpStack ?: HurlStack.new(),
            downloadInterceptors = (downloadInterceptors ?: listOf()) + DownloadEngineInterceptor()
        )
    }
}