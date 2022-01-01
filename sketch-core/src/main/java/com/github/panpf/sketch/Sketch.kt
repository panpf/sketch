package com.github.panpf.sketch

import android.content.Context
import android.net.Uri
import androidx.annotation.AnyThread
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.BitmapPoolHelper
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.LruBitmapPool
import com.github.panpf.sketch.cache.LruDiskCache
import com.github.panpf.sketch.cache.LruMemoryCache
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.MemorySizeCalculator
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.ExecuteResult
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.OneShotDisposable
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.internal.DisplayEngineInterceptor
import com.github.panpf.sketch.request.internal.DisplayExecutor
import com.github.panpf.sketch.request.internal.DownloadEngineInterceptor
import com.github.panpf.sketch.request.internal.DownloadExecutor
import com.github.panpf.sketch.request.internal.LoadEngineInterceptor
import com.github.panpf.sketch.request.internal.LoadExecutor
import com.github.panpf.sketch.request.internal.LoadResultCacheInterceptor
import com.github.panpf.sketch.transform.internal.TransformationInterceptor
import com.github.panpf.sketch.util.SLog
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
    memoryCache: MemoryCache? = null,
    diskCache: DiskCache? = null,
    bitmapPool: BitmapPool? = null,
    componentRegistry: ComponentRegistry? = null,
    httpStack: HttpStack? = null,
    downloadInterceptors: List<Interceptor<DownloadRequest, DownloadResult>>? = null,
    loadInterceptors: List<Interceptor<LoadRequest, LoadResult>>? = null,
    displayInterceptors: List<Interceptor<DisplayRequest, DisplayResult>>? = null,
) {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            SLog.wmt("scope", throwable, "exception")
        }
    )
    private val downloadExecutor = DownloadExecutor(this)
    private val loadExecutor = LoadExecutor(this)
    private val displayExecutor = DisplayExecutor(this)

    val appContext: Context = context.applicationContext
    val httpStack = httpStack ?: HurlStack.new()
    val memoryCache = memoryCache ?: LruMemoryCache(appContext, MemorySizeCalculator(appContext).memoryCacheSize)
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
        ?: listOf()) + LoadResultCacheInterceptor() + TransformationInterceptor() + LoadEngineInterceptor()
    // todo gif, svg, webpA
    val displayInterceptors = (displayInterceptors ?: listOf()) + DisplayEngineInterceptor()

    val singleThreadTaskDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1)
    val httpDownloadTaskDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(10)
    val decodeTaskDispatcher: CoroutineDispatcher = Dispatchers.IO

    init {
        // todo 增加 defaultOptions
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


    /***************************************** Display ********************************************/

    @AnyThread
    fun enqueueDisplay(
        request: DisplayRequest,
        listener: Listener<DisplayRequest, DisplayResult>? = null,
        httpFetchProgressListener: ProgressListener<DisplayRequest>? = null,
    ): Disposable<ExecuteResult<DisplayResult>> {
        val job = scope.async(singleThreadTaskDispatcher) {
            displayExecutor.execute(request, listener, httpFetchProgressListener)
        }
        return OneShotDisposable(job)
    }

    @AnyThread
    fun enqueueDisplay(
        uri: Uri,
        configBlock: (DisplayRequest.Builder.() -> Unit)? = null,
        listener: Listener<DisplayRequest, DisplayResult>? = null,
        httpFetchProgressListener: ProgressListener<DisplayRequest>? = null,
    ): Disposable<ExecuteResult<DisplayResult>> =
        enqueueDisplay(DisplayRequest.new(uri, configBlock), listener, httpFetchProgressListener)

    @AnyThread
    fun enqueueDisplay(
        uriString: String,
        configBlock: (DisplayRequest.Builder.() -> Unit)? = null,
        listener: Listener<DisplayRequest, DisplayResult>? = null,
        httpFetchProgressListener: ProgressListener<DisplayRequest>? = null,
    ): Disposable<ExecuteResult<DisplayResult>> =
        enqueueDisplay(
            DisplayRequest.new(uriString, configBlock),
            listener,
            httpFetchProgressListener
        )

    suspend fun executeDisplay(request: DisplayRequest): ExecuteResult<DisplayResult> =
        coroutineScope {
            val job = async(singleThreadTaskDispatcher) {
                displayExecutor.execute(request, null, null)
            }
            job.await()
        }

    suspend fun executeDisplay(
        uri: Uri,
        configBlock: (DisplayRequest.Builder.() -> Unit)? = null
    ): ExecuteResult<DisplayResult> = executeDisplay(DisplayRequest.new(uri, configBlock))

    suspend fun executeDisplay(
        uriString: String,
        configBlock: (DisplayRequest.Builder.() -> Unit)? = null
    ): ExecuteResult<DisplayResult> = executeDisplay(DisplayRequest.new(uriString, configBlock))


    /****************************************** Load **********************************************/

    @AnyThread
    fun enqueueLoad(
        request: LoadRequest,
        listener: Listener<LoadRequest, LoadResult>? = null,
        httpFetchProgressListener: ProgressListener<LoadRequest>? = null,
    ): Disposable<ExecuteResult<LoadResult>> {
        val job = scope.async(singleThreadTaskDispatcher) {
            loadExecutor.execute(request, listener, httpFetchProgressListener)
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
            loadExecutor.execute(request, null, null)
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


    /**************************************** Download ********************************************/

    @AnyThread
    fun enqueueDownload(
        request: DownloadRequest,
        listener: Listener<DownloadRequest, DownloadResult>? = null,
        httpFetchProgressListener: ProgressListener<DownloadRequest>? = null,
    ): Disposable<ExecuteResult<DownloadResult>> {
        val job = scope.async(singleThreadTaskDispatcher) {
            downloadExecutor.execute(request, listener, httpFetchProgressListener)
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
                downloadExecutor.execute(request, null, null)
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
        private var memoryCache: MemoryCache? = null
        private var diskCache: DiskCache? = null
        private var bitmapPool: BitmapPool? = null
        private var componentRegistry: ComponentRegistry? = null
        private var httpStack: HttpStack? = null
        private var downloadInterceptors: MutableList<Interceptor<DownloadRequest, DownloadResult>>? =
            null
        private var loadInterceptors: MutableList<Interceptor<LoadRequest, LoadResult>>? =
            null
        private var displayInterceptors: MutableList<Interceptor<DisplayRequest, DisplayResult>>? =
            null

        fun memoryCache(memoryCache: MemoryCache?): Builder = apply {
            this.memoryCache = memoryCache
        }

        fun diskCache(diskCache: DiskCache?): Builder = apply {
            this.diskCache = diskCache
        }

        fun bitmapPool(bitmapPool: BitmapPool?): Builder = apply {
            this.bitmapPool = bitmapPool
        }

        fun components(components: ComponentRegistry?): Builder = apply {
            this.componentRegistry = components
        }

        fun componentsWithBuilder(block: ComponentRegistry.Builder.() -> Unit): Builder = apply {
            this.componentRegistry = ComponentRegistry.Builder().apply(block).build()
        }

        fun httpStack(httpStack: HttpStack?): Builder = apply {
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

        fun addDisplayInterceptor(interceptor: Interceptor<DisplayRequest, DisplayResult>): Builder =
            apply {
                this.displayInterceptors = (displayInterceptors ?: mutableListOf()).apply {
                    add(interceptor)
                }
            }

        fun build(): Sketch = Sketch(
            context = appContext,
            memoryCache = memoryCache,
            diskCache = diskCache,
            bitmapPool = bitmapPool,
            componentRegistry = componentRegistry,
            httpStack = httpStack,
            downloadInterceptors = downloadInterceptors,
            loadInterceptors = loadInterceptors,
            displayInterceptors = displayInterceptors,
        )
    }
}