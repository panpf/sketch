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
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.internal.BitmapDecodeEngineInterceptor
import com.github.panpf.sketch.decode.internal.BitmapResultCacheInterceptor
import com.github.panpf.sketch.decode.internal.DecodeInterceptor
import com.github.panpf.sketch.decode.internal.DefaultBitmapDecoder
import com.github.panpf.sketch.decode.internal.DefaultDrawableDecoder
import com.github.panpf.sketch.decode.internal.DrawableDecodeEngineInterceptor
import com.github.panpf.sketch.decode.internal.ExifOrientationCorrectInterceptor
import com.github.panpf.sketch.decode.internal.SizeInterceptor
import com.github.panpf.sketch.decode.internal.XmlDrawableBitmapDecoder
import com.github.panpf.sketch.decode.transform.internal.TransformationInterceptor
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.ContentUriFetcher
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.OneShotDisposable
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.internal.DisplayEngineInterceptor
import com.github.panpf.sketch.request.internal.DisplayExecutor
import com.github.panpf.sketch.request.internal.DownloadEngineInterceptor
import com.github.panpf.sketch.request.internal.DownloadExecutor
import com.github.panpf.sketch.request.internal.LoadEngineInterceptor
import com.github.panpf.sketch.request.internal.LoadExecutor
import com.github.panpf.sketch.request.internal.requestManager
import com.github.panpf.sketch.target.ViewTarget
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File

class Sketch private constructor(
    _context: Context,
    _logger: Logger?,
    _memoryCache: MemoryCache?,
    _diskCache: DiskCache?,
    _bitmapPool: BitmapPool?,
    _componentRegistry: ComponentRegistry?,
    _httpStack: HttpStack?,
    _downloadInterceptors: List<RequestInterceptor<DownloadRequest, DownloadData>>?,
    _loadInterceptors: List<RequestInterceptor<LoadRequest, LoadData>>?,
    _displayInterceptors: List<RequestInterceptor<DisplayRequest, DisplayData>>?,
    _bitmapDecodeInterceptors: List<DecodeInterceptor<LoadRequest, BitmapDecodeResult>>?,
    _drawableDecodeInterceptors: List<DecodeInterceptor<DisplayRequest, DrawableDecodeResult>>?,
) {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            logger.e("scope", throwable, "exception")
        }
    )
    private val downloadExecutor = DownloadExecutor(this)
    private val loadExecutor = LoadExecutor(this)
    private val displayExecutor = DisplayExecutor(this)

    val appContext: Context = _context.applicationContext
    val logger = _logger ?: Logger()
    val httpStack = _httpStack ?: HurlStack.new()

    val diskCache = _diskCache ?: LruDiskCache(appContext, logger)
    val memoryCache: MemoryCache
    val bitmapPoolHelper: BitmapPoolHelper

    val componentRegistry: ComponentRegistry = (_componentRegistry ?: ComponentRegistry.new())
        .newBuilder().apply {
            addFetcher(HttpUriFetcher.Factory())
            addFetcher(FileUriFetcher.Factory())
            addFetcher(ContentUriFetcher.Factory())
            addFetcher(ResourceUriFetcher.Factory())
            addFetcher(AssetUriFetcher.Factory())
            addFetcher(Base64UriFetcher.Factory())
            addBitmapDecoder(XmlDrawableBitmapDecoder.Factory())
            addBitmapDecoder(DefaultBitmapDecoder.Factory())
            addDrawableDecoder(DefaultDrawableDecoder.Factory())
            // todo 支持 svg
        }.build()
    val downloadInterceptors: List<RequestInterceptor<DownloadRequest, DownloadData>> =
        (_downloadInterceptors ?: listOf()) + DownloadEngineInterceptor()
    val loadInterceptors: List<RequestInterceptor<LoadRequest, LoadData>> =
        (_loadInterceptors ?: listOf()) + LoadEngineInterceptor()
    val displayInterceptors: List<RequestInterceptor<DisplayRequest, DisplayData>> =
        (_displayInterceptors ?: listOf()) + DisplayEngineInterceptor()
    val bitmapDecodeInterceptors: List<DecodeInterceptor<LoadRequest, BitmapDecodeResult>> =
        (_bitmapDecodeInterceptors ?: listOf()) +
                BitmapResultCacheInterceptor() +
                TransformationInterceptor() +
                SizeInterceptor() +
                ExifOrientationCorrectInterceptor() +
                BitmapDecodeEngineInterceptor()
    val drawableDecodeInterceptors: List<DecodeInterceptor<DisplayRequest, DrawableDecodeResult>> =
        (_drawableDecodeInterceptors ?: listOf()) + DrawableDecodeEngineInterceptor()

    //    val singleThreadTaskDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1)
    val networkTaskDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(10)
    val decodeTaskDispatcher: CoroutineDispatcher = Dispatchers.IO

    init {
        val memorySizeCalculator = MemorySizeCalculator(appContext, logger)
        memoryCache = _memoryCache
            ?: LruMemoryCache(appContext, memorySizeCalculator.memoryCacheSize, logger)
        val bitmapPool = _bitmapPool
            ?: LruBitmapPool(appContext, memorySizeCalculator.bitmapPoolSize, logger)
        bitmapPoolHelper = BitmapPoolHelper(_context, logger, bitmapPool)

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
    fun enqueueDisplay(request: DisplayRequest): Disposable<DisplayResult> {
        val job = scope.async(Dispatchers.Main.immediate) {
            displayExecutor.execute(request)
        }
        return if (request.target is ViewTarget<*>) {
            (request.target as ViewTarget<*>).view.requestManager.getDisposable(job)
        } else {
            OneShotDisposable(job)
        }
    }

    @AnyThread
    fun enqueueDisplay(
        uriString: String?,
        configBlock: (DisplayRequest.Builder.() -> Unit)? = null,
    ): Disposable<DisplayResult> = enqueueDisplay(DisplayRequest.new(uriString, configBlock))

    @AnyThread
    fun enqueueDisplay(
        uri: Uri?,
        configBlock: (DisplayRequest.Builder.() -> Unit)? = null,
    ): Disposable<DisplayResult> = enqueueDisplay(DisplayRequest.new(uri, configBlock))

    suspend fun executeDisplay(request: DisplayRequest): DisplayResult =
        coroutineScope {
            val job = async(Dispatchers.Main.immediate) {
                displayExecutor.execute(request)
            }
            // Update the current request attached to the view and await the result.
            if (request.target is ViewTarget<*>) {
                (request.target as ViewTarget<*>).view.requestManager.getDisposable(job)
            }
            job.await()
        }

    suspend fun executeDisplay(
        uriString: String?,
        configBlock: (DisplayRequest.Builder.() -> Unit)? = null
    ): DisplayResult = executeDisplay(DisplayRequest.new(uriString, configBlock))

    suspend fun executeDisplay(
        uri: Uri?,
        configBlock: (DisplayRequest.Builder.() -> Unit)? = null
    ): DisplayResult = executeDisplay(DisplayRequest.new(uri, configBlock))


    /****************************************** Load **********************************************/

    @AnyThread
    fun enqueueLoad(request: LoadRequest): Disposable<LoadResult> {
        val job = scope.async(decodeTaskDispatcher) {
            loadExecutor.execute(request)
        }
        return OneShotDisposable(job)
    }

    @AnyThread
    fun enqueueLoad(
        uriString: String,
        configBlock: (LoadRequest.Builder.() -> Unit)? = null,
    ): Disposable<LoadResult> = enqueueLoad(LoadRequest.new(uriString, configBlock))

    @AnyThread
    fun enqueueLoad(
        uri: Uri,
        configBlock: (LoadRequest.Builder.() -> Unit)? = null,
    ): Disposable<LoadResult> = enqueueLoad(LoadRequest.new(uri, configBlock))

    suspend fun executeLoad(request: LoadRequest): LoadResult = coroutineScope {
        val job = async(decodeTaskDispatcher) {
            loadExecutor.execute(request)
        }
        job.await()
    }

    suspend fun executeLoad(
        uriString: String,
        configBlock: (LoadRequest.Builder.() -> Unit)? = null
    ): LoadResult = executeLoad(LoadRequest.new(uriString, configBlock))

    suspend fun executeLoad(
        uri: Uri,
        configBlock: (LoadRequest.Builder.() -> Unit)? = null
    ): LoadResult = executeLoad(LoadRequest.new(uri, configBlock))


    /**************************************** Download ********************************************/

    @AnyThread
    fun enqueueDownload(request: DownloadRequest): Disposable<DownloadResult> {
        val job = scope.async(decodeTaskDispatcher) {
            downloadExecutor.execute(request)
        }
        return OneShotDisposable(job)
    }

    @AnyThread
    fun enqueueDownload(
        uriString: String,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null,
    ): Disposable<DownloadResult> = enqueueDownload(DownloadRequest.new(uriString, configBlock))

    @AnyThread
    fun enqueueDownload(
        uri: Uri,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null,
    ): Disposable<DownloadResult> = enqueueDownload(DownloadRequest.new(uri, configBlock))

    suspend fun executeDownload(request: DownloadRequest): DownloadResult =
        coroutineScope {
            val job = async(decodeTaskDispatcher) {
                downloadExecutor.execute(request)
            }
            job.await()
        }

    suspend fun executeDownload(
        uriString: String,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null
    ): DownloadResult = executeDownload(DownloadRequest.new(uriString, configBlock))

    suspend fun executeDownload(
        uri: Uri,
        configBlock: (DownloadRequest.Builder.() -> Unit)? = null
    ): DownloadResult = executeDownload(DownloadRequest.new(uri, configBlock))


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
        private var logger: Logger? = null
        private var memoryCache: MemoryCache? = null
        private var diskCache: DiskCache? = null
        private var bitmapPool: BitmapPool? = null
        private var componentRegistry: ComponentRegistry? = null
        private var httpStack: HttpStack? = null
        private var downloadInterceptors: MutableList<RequestInterceptor<DownloadRequest, DownloadData>>? =
            null
        private var loadInterceptors: MutableList<RequestInterceptor<LoadRequest, LoadData>>? =
            null
        private var displayInterceptors: MutableList<RequestInterceptor<DisplayRequest, DisplayData>>? =
            null
        private var bitmapDecodeInterceptors: MutableList<DecodeInterceptor<LoadRequest, BitmapDecodeResult>>? =
            null
        private var drawableDecodeInterceptors: MutableList<DecodeInterceptor<DisplayRequest, DrawableDecodeResult>>? =
            null

        fun logger(logger: Logger?): Builder = apply {
            this.logger = logger
        }

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

        fun components(configBlock: (ComponentRegistry.Builder.() -> Unit)): Builder = apply {
            this.componentRegistry = ComponentRegistry.new(configBlock)
        }

        fun httpStack(httpStack: HttpStack?): Builder = apply {
            this.httpStack = httpStack
        }

        fun addDownloadInterceptor(interceptor: RequestInterceptor<DownloadRequest, DownloadData>): Builder =
            apply {
                this.downloadInterceptors = (downloadInterceptors ?: mutableListOf()).apply {
                    add(interceptor)
                }
            }

        fun addLoadInterceptor(interceptor: RequestInterceptor<LoadRequest, LoadData>): Builder =
            apply {
                this.loadInterceptors = (loadInterceptors ?: mutableListOf()).apply {
                    add(interceptor)
                }
            }

        fun addDisplayInterceptor(interceptor: RequestInterceptor<DisplayRequest, DisplayData>): Builder =
            apply {
                this.displayInterceptors = (displayInterceptors ?: mutableListOf()).apply {
                    add(interceptor)
                }
            }

        fun addBitmapDecodeInterceptor(bitmapDecodeInterceptor: DecodeInterceptor<LoadRequest, BitmapDecodeResult>): Builder =
            apply {
                this.bitmapDecodeInterceptors =
                    (bitmapDecodeInterceptors ?: mutableListOf()).apply {
                        add(bitmapDecodeInterceptor)
                    }
            }

        fun addDrawableDecodeInterceptor(drawableDecodeInterceptor: DecodeInterceptor<DisplayRequest, DrawableDecodeResult>): Builder =
            apply {
                this.drawableDecodeInterceptors =
                    (drawableDecodeInterceptors ?: mutableListOf()).apply {
                        add(drawableDecodeInterceptor)
                    }
            }

        fun build(): Sketch = Sketch(
            _context = appContext,
            _logger = logger,
            _memoryCache = memoryCache,
            _diskCache = diskCache,
            _bitmapPool = bitmapPool,
            _componentRegistry = componentRegistry,
            _httpStack = httpStack,
            _downloadInterceptors = downloadInterceptors,
            _loadInterceptors = loadInterceptors,
            _displayInterceptors = displayInterceptors,
            _bitmapDecodeInterceptors = bitmapDecodeInterceptors,
            _drawableDecodeInterceptors = drawableDecodeInterceptors,
        )
    }
}