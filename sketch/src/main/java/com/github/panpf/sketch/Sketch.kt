package com.github.panpf.sketch

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import androidx.annotation.AnyThread
import com.github.panpf.sketch.Sketch.SketchSingleton
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.CountDrawablePendingManager
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.cache.internal.LruDiskCache
import com.github.panpf.sketch.cache.internal.LruMemoryCache
import com.github.panpf.sketch.cache.internal.defaultMemoryCacheBytes
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.internal.BitmapDecodeEngineInterceptor
import com.github.panpf.sketch.decode.internal.BitmapResultDiskCacheInterceptor
import com.github.panpf.sketch.decode.internal.DefaultBitmapDecoder
import com.github.panpf.sketch.decode.internal.DefaultDrawableDecoder
import com.github.panpf.sketch.decode.internal.DrawableDecodeEngineInterceptor
import com.github.panpf.sketch.decode.internal.XmlDrawableBitmapDecoder
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.ContentUriFetcher
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayOptions
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadOptions
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadOptions
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
import com.github.panpf.sketch.transform.internal.TransformationInterceptor
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToLong

val Context.sketch: Sketch
    get() = SketchSingleton.sketch(this)

// todo Monitor system callbacks, TrimMemory and networking
class Sketch private constructor(
    val context: Context,
    val logger: Logger,
    val memoryCache: MemoryCache,
    val diskCache: DiskCache,
    val bitmapPool: BitmapPool,
    val componentRegistry: ComponentRegistry,
    val httpStack: HttpStack,
    val downloadInterceptors: List<RequestInterceptor<DownloadRequest, DownloadData>>,
    val loadInterceptors: List<RequestInterceptor<LoadRequest, LoadData>>,
    val displayInterceptors: List<RequestInterceptor<DisplayRequest, DisplayData>>,
    val bitmapDecodeInterceptors: List<DecodeInterceptor<LoadRequest, BitmapDecodeResult>>,
    val drawableDecodeInterceptors: List<DecodeInterceptor<DisplayRequest, DrawableDecodeResult>>,
    val globalDisplayOptions: DisplayOptions?,
    val globalLoadOptions: LoadOptions?,
    val globalDownloadOptions: DownloadOptions?,
) {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate + CoroutineExceptionHandler { _, throwable ->
            logger.e("scope", throwable, "exception")
        }
    )
    private val downloadExecutor = DownloadExecutor(this)
    private val loadExecutor = LoadExecutor(this)
    private val displayExecutor = DisplayExecutor(this)

    val countDrawablePendingManager = CountDrawablePendingManager(logger)
    val networkTaskDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(10)

    // Limit the number of concurrent decoding tasks because too many concurrent BitmapFactory tasks can affect UI performance
    val decodeTaskDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(4)

    init {
        memoryCache.logger = logger
        bitmapPool.logger = logger
        diskCache.logger = logger

        context.registerComponentCallbacks(object : ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {
            }

            override fun onLowMemory() {
                memoryCache.clear()
                bitmapPool.clear()
            }

            override fun onTrimMemory(level: Int) {
                memoryCache.trim(level)
                bitmapPool.trim(level)
            }
        })

        logger.d("Configuration") {
            buildString {
                val fetchers = componentRegistry.fetcherFactoryList.joinToString(",")
                val bitmapDecoders = componentRegistry.bitmapDecoderFactoryList.joinToString(",")
                val drawableDecoders =
                    componentRegistry.drawableDecoderFactoryList.joinToString(",")
                val displayInterceptors = displayInterceptors.joinToString(",")
                val bitmapDecodeInterceptors = bitmapDecodeInterceptors.joinToString(",")
                val drawableDecodeInterceptors = drawableDecodeInterceptors.joinToString(",")
                append("\n").append("logger: $logger")
                append("\n").append("httpStack: $httpStack")
                append("\n").append("memoryCache: $memoryCache")
                append("\n").append("bitmapPool: $bitmapPool")
                append("\n").append("diskCache: $diskCache")
                append("\n").append("fetchers: $fetchers")
                append("\n").append("bitmapDecoders: $bitmapDecoders")
                append("\n").append("drawableDecoders: $drawableDecoders")
                append("\n").append("downloadInterceptors: ${downloadInterceptors.joinToString()}")
                append("\n").append("loadInterceptors: ${loadInterceptors.joinToString(",")}")
                append("\n").append("displayInterceptors: $displayInterceptors")
                append("\n").append("bitmapDecodeInterceptors: $bitmapDecodeInterceptors")
                append("\n").append("drawableDecodeInterceptors: $drawableDecodeInterceptors")
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


    /****************************************** Load **********************************************/

    @AnyThread
    fun enqueueLoad(request: LoadRequest): Disposable<LoadResult> {
        val job = scope.async(Dispatchers.Main.immediate) {
            loadExecutor.execute(request)
        }
        return OneShotDisposable(job)
    }

    suspend fun executeLoad(request: LoadRequest): LoadResult = coroutineScope {
        val job = async(Dispatchers.Main.immediate) {
            loadExecutor.execute(request)
        }
        job.await()
    }


    /**************************************** Download ********************************************/

    @AnyThread
    fun enqueueDownload(request: DownloadRequest): Disposable<DownloadResult> {
        val job = scope.async(Dispatchers.Main.immediate) {
            downloadExecutor.execute(request)
        }
        return OneShotDisposable(job)
    }

    suspend fun executeDownload(request: DownloadRequest): DownloadResult =
        coroutineScope {
            val job = async(Dispatchers.Main.immediate) {
                downloadExecutor.execute(request)
            }
            job.await()
        }


    companion object {
        fun new(context: Context, configBlock: (Builder.() -> Unit)? = null): Sketch =
            Builder(context).apply {
                configBlock?.invoke(this)
            }.build()
    }

    fun interface Factory {
        fun createSketch(): Sketch
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
        private var globalDisplayOptions: DisplayOptions? = null
        private var globalLoadOptions: LoadOptions? = null
        private var globalDownloadOptions: DownloadOptions? = null

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

        fun globalDisplayOptions(globalDisplayOptions: DisplayOptions): Builder = apply {
            this.globalDisplayOptions = globalDisplayOptions
        }

        fun globalLoadOptions(globalLoadOptions: DisplayOptions): Builder = apply {
            this.globalLoadOptions = globalLoadOptions
        }

        fun globalDownloadOptions(globalDownloadOptions: DisplayOptions): Builder = apply {
            this.globalDownloadOptions = globalDownloadOptions
        }

        fun build(): Sketch {
            val logger = logger ?: Logger()
            val httpStack = httpStack ?: HurlStack.new()

            val defaultMemoryCacheBytes = appContext.defaultMemoryCacheBytes()
            val memoryCache: MemoryCache = memoryCache
                ?: LruMemoryCache((defaultMemoryCacheBytes * 0.66f).roundToLong())
            val bitmapPool: BitmapPool = bitmapPool
                ?: LruBitmapPool((defaultMemoryCacheBytes * 0.33f).roundToLong())
            val diskCache: DiskCache = diskCache ?: LruDiskCache(appContext)

            val componentRegistry: ComponentRegistry =
                (componentRegistry ?: ComponentRegistry.new())
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
                    }.build()

            val downloadInterceptors: List<RequestInterceptor<DownloadRequest, DownloadData>> =
                (downloadInterceptors ?: listOf()) + DownloadEngineInterceptor()
            val loadInterceptors: List<RequestInterceptor<LoadRequest, LoadData>> =
                (loadInterceptors ?: listOf()) + LoadEngineInterceptor()
            val displayInterceptors: List<RequestInterceptor<DisplayRequest, DisplayData>> =
                (displayInterceptors ?: listOf()) + DisplayEngineInterceptor()

            val bitmapDecodeInterceptors: List<DecodeInterceptor<LoadRequest, BitmapDecodeResult>> =
                (bitmapDecodeInterceptors ?: listOf()) +
                        BitmapResultDiskCacheInterceptor() +
                        TransformationInterceptor() +
                        BitmapDecodeEngineInterceptor()
            val drawableDecodeInterceptors: List<DecodeInterceptor<DisplayRequest, DrawableDecodeResult>> =
                (drawableDecodeInterceptors ?: listOf()) + DrawableDecodeEngineInterceptor()

            return Sketch(
                context = appContext,
                logger = logger,
                memoryCache = memoryCache,
                diskCache = diskCache,
                bitmapPool = bitmapPool,
                componentRegistry = componentRegistry,
                httpStack = httpStack,
                downloadInterceptors = downloadInterceptors,
                loadInterceptors = loadInterceptors,
                displayInterceptors = displayInterceptors,
                bitmapDecodeInterceptors = bitmapDecodeInterceptors,
                drawableDecodeInterceptors = drawableDecodeInterceptors,
                globalDisplayOptions = globalDisplayOptions,
                globalLoadOptions = globalLoadOptions,
                globalDownloadOptions = globalDownloadOptions,
            )
        }
    }

    internal object SketchSingleton {

        private var sketch: Sketch? = null

        @JvmStatic
        fun sketch(context: Context): Sketch =
            sketch ?: synchronized(this) {
                sketch ?: synchronized(this) {
                    newSketch(context).apply {
                        sketch = this
                    }
                }
            }

        private fun newSketch(context: Context): Sketch {
            val appContext = context.applicationContext
            return if (appContext is Factory) {
                appContext.createSketch()
            } else {
                new(appContext)
            }
        }
    }
}