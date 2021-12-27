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
import com.github.panpf.sketch.common.internal.RepeatTaskFilter
import com.github.panpf.sketch.download.DownloadData
import com.github.panpf.sketch.download.DownloadRequest
import com.github.panpf.sketch.download.internal.DownloadEngineInterceptor
import com.github.panpf.sketch.download.internal.DownloadExecutor
import com.github.panpf.sketch.load.LoadData
import com.github.panpf.sketch.load.LoadRequest
import com.github.panpf.sketch.load.internal.LoadEngineInterceptor
import com.github.panpf.sketch.load.internal.LoadExecutor
import kotlinx.coroutines.*
import java.io.File

class Sketch private constructor(
    val appContext: Context,
    val diskCache: DiskCache,
    val componentRegistry: ComponentRegistry,
    val httpStack: HttpStack,
    val downloadInterceptors: List<Interceptor<DownloadRequest, DownloadData>>,
    val loadInterceptors: List<Interceptor<LoadRequest, LoadData>>,
) {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            SLog.wmt("scope", throwable, "exception")
        }
    )
    private val downloadExecutor = DownloadExecutor(this)
    private val loadExecutor = LoadExecutor(this)

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
        listener: Listener<DownloadRequest, DownloadData>? = null,
        httpFetchProgressListener: ProgressListener<DownloadRequest>? = null,
    ): Disposable<ExecuteResult<DownloadData>> {
//        val progressListenerProxy: ProgressListener<DownloadRequest>? = httpFetchProgressListener?.let {
//            ProgressListener<DownloadRequest> { request, totalLength, completedLength ->
//                httpFetchProgressListener.onUpdateProgress(
//                    request as DownloadRequest, totalLength, completedLength
//                )
//            }
//        }
        val job = scope.async(singleThreadTaskDispatcher) {
            downloadExecutor.execute(request, RequestExtras(listener, httpFetchProgressListener))
        }
        return OneShotDisposable(job)
    }

    @AnyThread
    fun enqueueDownload(
        uri: Uri,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null,
        listener: Listener<DownloadRequest, DownloadData>? = null,
        httpFetchProgressListener: ProgressListener<DownloadRequest>? = null,
    ): Disposable<ExecuteResult<DownloadData>> =
        enqueueDownload(DownloadRequest.new(uri, configBlock), listener, httpFetchProgressListener)

    @AnyThread
    fun enqueueDownload(
        uriString: String,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null,
        listener: Listener<DownloadRequest, DownloadData>? = null,
        httpFetchProgressListener: ProgressListener<DownloadRequest>? = null,
    ): Disposable<ExecuteResult<DownloadData>> =
        enqueueDownload(
            DownloadRequest.new(uriString, configBlock),
            listener,
            httpFetchProgressListener
        )

    suspend fun executeDownload(request: DownloadRequest): ExecuteResult<DownloadData> {
        return coroutineScope {
            val job = async(singleThreadTaskDispatcher) {
                downloadExecutor.execute(request, null)
            }
            job.await()
        }
    }

    suspend fun executeDownload(
        uri: Uri,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null
    ): ExecuteResult<DownloadData> = executeDownload(DownloadRequest.new(uri, configBlock))

    suspend fun executeDownload(
        uriString: String,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null
    ): ExecuteResult<DownloadData> = executeDownload(DownloadRequest.new(uriString, configBlock))


    @AnyThread
    fun enqueueLoad(
        request: LoadRequest,
        listener: Listener<LoadRequest, LoadData>? = null,
        httpFetchProgressListener: ProgressListener<LoadRequest>? = null,
    ): Disposable<ExecuteResult<LoadData>> {
//        val progressListenerProxy = httpFetchProgressListener?.let {
//            ProgressListener<ImageRequest> { request, totalLength, completedLength ->
//                httpFetchProgressListener.onUpdateProgress(
//                    request as LoadRequest, totalLength, completedLength
//                )
//            }
//        }
        val job = scope.async(singleThreadTaskDispatcher) {
            loadExecutor.execute(
                request,
                listener,
                RequestExtras(listener, httpFetchProgressListener)
            )
        }
        return OneShotDisposable(job)
    }

    @AnyThread
    fun enqueueLoad(
        uri: Uri,
        configBlock: (LoadRequest.Builder.() -> Unit)? = null,
        listener: Listener<LoadRequest, LoadData>? = null,
        httpFetchProgressListener: ProgressListener<LoadRequest>? = null,
    ): Disposable<ExecuteResult<LoadData>> =
        enqueueLoad(LoadRequest.new(uri, configBlock), listener, httpFetchProgressListener)

    @AnyThread
    fun enqueueLoad(
        uriString: String,
        configBlock: (LoadRequest.Builder.() -> Unit)? = null,
        listener: Listener<LoadRequest, LoadData>? = null,
        httpFetchProgressListener: ProgressListener<LoadRequest>? = null,
    ): Disposable<ExecuteResult<LoadData>> =
        enqueueLoad(
            LoadRequest.new(uriString, configBlock),
            listener,
            httpFetchProgressListener
        )

    suspend fun executeLoad(request: LoadRequest): ExecuteResult<LoadData> {
        return coroutineScope {
            val job = async(singleThreadTaskDispatcher) {
                loadExecutor.execute(request, null, null)
            }
            job.await()
        }
    }

    suspend fun executeLoad(
        uri: Uri,
        configBlock: (LoadRequest.Builder.() -> Unit)? = null
    ): ExecuteResult<LoadData> = executeLoad(LoadRequest.new(uri, configBlock))

    suspend fun executeLoad(
        uriString: String,
        configBlock: (LoadRequest.Builder.() -> Unit)? = null
    ): ExecuteResult<LoadData> = executeLoad(LoadRequest.new(uriString, configBlock))

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
        private var downloadInterceptors: MutableList<Interceptor<DownloadRequest, DownloadData>>? =
            null
        private var loadInterceptors: MutableList<Interceptor<LoadRequest, LoadData>>? =
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

        fun addDownloadInterceptor(interceptor: Interceptor<DownloadRequest, DownloadData>): Builder =
            apply {
                this.downloadInterceptors = (downloadInterceptors ?: mutableListOf()).apply {
                    add(interceptor)
                }
            }

        fun addLoadInterceptor(interceptor: Interceptor<LoadRequest, LoadData>): Builder =
            apply {
                this.loadInterceptors = (loadInterceptors ?: mutableListOf()).apply {
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
            downloadInterceptors = (downloadInterceptors ?: listOf()) + DownloadEngineInterceptor(),
            loadInterceptors = (loadInterceptors ?: listOf()) + LoadEngineInterceptor(),
        )
    }
}