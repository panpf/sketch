package com.github.panpf.sketch

import android.content.Context
import android.net.Uri
import androidx.annotation.AnyThread
import com.github.panpf.sketch.common.ComponentRegistry
import com.github.panpf.sketch.common.Disposable
import com.github.panpf.sketch.common.ExecuteResult
import com.github.panpf.sketch.common.Interceptor
import com.github.panpf.sketch.common.Listener
import com.github.panpf.sketch.common.ListenerInfo
import com.github.panpf.sketch.common.OneShotDisposable
import com.github.panpf.sketch.common.ProgressListener
import com.github.panpf.sketch.common.cache.BitmapPool
import com.github.panpf.sketch.common.cache.BitmapPoolHelper
import com.github.panpf.sketch.common.cache.DiskCache
import com.github.panpf.sketch.common.cache.LruBitmapPool
import com.github.panpf.sketch.common.cache.LruDiskCache
import com.github.panpf.sketch.common.cache.MemorySizeCalculator
import com.github.panpf.sketch.common.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.common.fetch.HttpUriFetcher
import com.github.panpf.sketch.common.http.HttpStack
import com.github.panpf.sketch.common.http.HurlStack
import com.github.panpf.sketch.common.internal.RepeatTaskFilter
import com.github.panpf.sketch.download.DownloadRequest
import com.github.panpf.sketch.download.DownloadResult
import com.github.panpf.sketch.download.internal.DownloadEngineInterceptor
import com.github.panpf.sketch.download.internal.DownloadExecutor
import com.github.panpf.sketch.load.LoadRequest
import com.github.panpf.sketch.load.LoadResult
import com.github.panpf.sketch.load.internal.LoadEngineInterceptor
import com.github.panpf.sketch.load.internal.LoadExecutor
import com.github.panpf.sketch.load.internal.ResultCacheInterceptor
import com.github.panpf.sketch.load.internal.TransformationInterceptor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File

class Sketch constructor(
    context: Context,
    diskCache: DiskCache? = null,
    bitmapPool: BitmapPool? = null,
    componentRegistry: ComponentRegistry? = null,
    httpStack: HttpStack? = null,
    downloadInterceptors: List<Interceptor<DownloadRequest, DownloadResult>>? = null,
    loadInterceptors: List<Interceptor<LoadRequest, LoadResult>>? = null,
) {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            SLog.wmt("scope", throwable, "exception")
        }
    )
    private val downloadExecutor = DownloadExecutor(this)
    private val loadExecutor = LoadExecutor(this)

    val appContext: Context = context.applicationContext
    val httpStack = httpStack ?: HurlStack.new()
    val diskCache = diskCache ?: LruDiskCache(appContext)
    val bitmapPoolHelper = BitmapPoolHelper(
        appContext,
        bitmapPool ?: LruBitmapPool(appContext, MemorySizeCalculator(appContext).bitmapPoolSize)
    )
    val componentRegistry: ComponentRegistry =
        (componentRegistry?.newBuilder() ?: ComponentRegistry.Builder()).apply {
            addFetcher(HttpUriFetcher.Factory())
            addDecoder(BitmapFactoryDecoder.Factory())
        }.build()
    val downloadInterceptors = (downloadInterceptors ?: listOf()) + DownloadEngineInterceptor()
    val loadInterceptors = (loadInterceptors
        ?: listOf()) + ResultCacheInterceptor() + TransformationInterceptor() + LoadEngineInterceptor()

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
    fun enqueueDownload(
        request: DownloadRequest,
        listener: Listener<DownloadRequest, DownloadResult>? = null,
        httpFetchProgressListener: ProgressListener<DownloadRequest>? = null,
    ): Disposable<ExecuteResult<DownloadResult>> {
        val job = scope.async(singleThreadTaskDispatcher) {
            downloadExecutor.execute(request, ListenerInfo(listener, httpFetchProgressListener))
        }
        return OneShotDisposable(job)
    }

    @AnyThread
    fun enqueueDownload(
        uri: Uri,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null,
        listener: Listener<DownloadRequest, DownloadResult>? = null,
        httpFetchProgressListener: ProgressListener<DownloadRequest>? = null,
    ): Disposable<ExecuteResult<DownloadResult>> =
        enqueueDownload(DownloadRequest.new(uri, configBlock), listener, httpFetchProgressListener)

    @AnyThread
    fun enqueueDownload(
        uriString: String,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null,
        listener: Listener<DownloadRequest, DownloadResult>? = null,
        httpFetchProgressListener: ProgressListener<DownloadRequest>? = null,
    ): Disposable<ExecuteResult<DownloadResult>> = enqueueDownload(
        DownloadRequest.new(uriString, configBlock), listener, httpFetchProgressListener
    )

    suspend fun executeDownload(request: DownloadRequest): ExecuteResult<DownloadResult> =
        coroutineScope {
            val job = async(singleThreadTaskDispatcher) {
                downloadExecutor.execute(request, null)
            }
            job.await()
        }

    suspend fun executeDownload(
        uri: Uri,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null
    ): ExecuteResult<DownloadResult> = executeDownload(DownloadRequest.new(uri, configBlock))

    suspend fun executeDownload(
        uriString: String,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null
    ): ExecuteResult<DownloadResult> = executeDownload(DownloadRequest.new(uriString, configBlock))


    @AnyThread
    fun enqueueLoad(
        request: LoadRequest,
        listener: Listener<LoadRequest, LoadResult>? = null,
        httpFetchProgressListener: ProgressListener<LoadRequest>? = null,
    ): Disposable<ExecuteResult<LoadResult>> {
        val job = scope.async(singleThreadTaskDispatcher) {
            loadExecutor.execute(request, ListenerInfo(listener, httpFetchProgressListener))
        }
        return OneShotDisposable(job)
    }

    @AnyThread
    fun enqueueLoad(
        uri: Uri,
        configBlock: (LoadRequest.Builder.() -> Unit)? = null,
        listener: Listener<LoadRequest, LoadResult>? = null,
        httpFetchProgressListener: ProgressListener<LoadRequest>? = null,
    ): Disposable<ExecuteResult<LoadResult>> =
        enqueueLoad(LoadRequest.new(uri, configBlock), listener, httpFetchProgressListener)

    @AnyThread
    fun enqueueLoad(
        uriString: String,
        configBlock: (LoadRequest.Builder.() -> Unit)? = null,
        listener: Listener<LoadRequest, LoadResult>? = null,
        httpFetchProgressListener: ProgressListener<LoadRequest>? = null,
    ): Disposable<ExecuteResult<LoadResult>> =
        enqueueLoad(LoadRequest.new(uriString, configBlock), listener, httpFetchProgressListener)

    suspend fun executeLoad(request: LoadRequest): ExecuteResult<LoadResult> = coroutineScope {
        val job = async(singleThreadTaskDispatcher) {
            loadExecutor.execute(request, null)
        }
        job.await()
    }

    suspend fun executeLoad(
        uri: Uri,
        configBlock: (LoadRequest.Builder.() -> Unit)? = null
    ): ExecuteResult<LoadResult> = executeLoad(LoadRequest.new(uri, configBlock))

    suspend fun executeLoad(
        uriString: String,
        configBlock: (LoadRequest.Builder.() -> Unit)? = null
    ): ExecuteResult<LoadResult> = executeLoad(LoadRequest.new(uriString, configBlock))

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
        private var bitmapPool: BitmapPool? = null
        private var componentRegistry: ComponentRegistry? = null
        private var httpStack: HttpStack? = null
        private var downloadInterceptors: MutableList<Interceptor<DownloadRequest, DownloadResult>>? =
            null
        private var loadInterceptors: MutableList<Interceptor<LoadRequest, LoadResult>>? =
            null

        fun diskCache(diskCache: DiskCache): Builder = apply {
            this.diskCache = diskCache
        }

        fun bitmapPool(bitmapPool: BitmapPool): Builder = apply {
            this.bitmapPool = bitmapPool
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

        fun addLoadInterceptor(interceptor: Interceptor<LoadRequest, LoadResult>): Builder =
            apply {
                this.loadInterceptors = (loadInterceptors ?: mutableListOf()).apply {
                    add(interceptor)
                }
            }

        fun build(): Sketch = Sketch(
            context = appContext,
            diskCache = diskCache,
            bitmapPool = bitmapPool,
            componentRegistry = componentRegistry,
            httpStack = httpStack,
            downloadInterceptors = downloadInterceptors,
            loadInterceptors = loadInterceptors,
        )
    }
}