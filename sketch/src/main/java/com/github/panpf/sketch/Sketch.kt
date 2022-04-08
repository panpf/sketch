package com.github.panpf.sketch

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import androidx.annotation.AnyThread
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
import com.github.panpf.sketch.decode.internal.BitmapEngineDecodeInterceptor
import com.github.panpf.sketch.decode.internal.BitmapResultDiskCacheDecodeInterceptor
import com.github.panpf.sketch.decode.internal.DefaultBitmapDecoder
import com.github.panpf.sketch.decode.internal.DefaultDrawableDecoder
import com.github.panpf.sketch.decode.internal.DrawableEngineDecodeInterceptor
import com.github.panpf.sketch.decode.internal.XmlDrawableBitmapDecoder
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.ContentUriFetcher
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.OneShotDisposable
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.RequestExecutor
import com.github.panpf.sketch.request.internal.requestManager
import com.github.panpf.sketch.target.ViewTarget
import com.github.panpf.sketch.transform.internal.BitmapTransformationDecodeInterceptor
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToLong

// todo Monitor system callbacks, TrimMemory and networking
class Sketch private constructor(
    val context: Context,
    val logger: Logger,
    val memoryCache: MemoryCache,
    val diskCache: DiskCache,
    val bitmapPool: BitmapPool,
    val componentRegistry: ComponentRegistry,
    val httpStack: HttpStack,
    val requestInterceptors: List<RequestInterceptor>,
    val bitmapDecodeInterceptors: List<DecodeInterceptor<BitmapDecodeResult>>,
    val drawableDecodeInterceptors: List<DecodeInterceptor<DrawableDecodeResult>>,
    val globalImageOptions: ImageOptions?,
) {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate + CoroutineExceptionHandler { _, throwable ->
            logger.e("scope", throwable, "exception")
        }
    )
    private val imageExecutor = RequestExecutor(this)

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
                append("\n").append("imageInterceptors: ${requestInterceptors.joinToString(",")}")
                append("\n").append("bitmapDecodeInterceptors: $bitmapDecodeInterceptors")
                append("\n").append("drawableDecodeInterceptors: $drawableDecodeInterceptors")
            }
        }
    }


    @AnyThread
    fun enqueue(request: DisplayRequest): Disposable<DisplayResult> {
        val job = scope.async(Dispatchers.Main.immediate) {
            imageExecutor.execute(request) as DisplayResult
        }
        val target = request.target
        return if (target is ViewTarget<*>) {
            target.view.requestManager.getDisposable(job)
        } else {
            OneShotDisposable(job)
        }
    }

    suspend fun execute(request: DisplayRequest): DisplayResult =
        coroutineScope {
            val job = async(Dispatchers.Main.immediate) {
                imageExecutor.execute(request) as DisplayResult
            }
            // Update the current request attached to the view and await the result.
            val target = request.target
            if (target is ViewTarget<*>) {
                target.view.requestManager.getDisposable(job)
            }
            job.await()
        }


    @AnyThread
    fun enqueue(request: LoadRequest): Disposable<LoadResult> {
        val job = scope.async(Dispatchers.Main.immediate) {
            imageExecutor.execute(request) as LoadResult
        }
        return OneShotDisposable(job)
    }

    suspend fun execute(request: LoadRequest): LoadResult = coroutineScope {
        val job = async(Dispatchers.Main.immediate) {
            imageExecutor.execute(request) as LoadResult
        }
        job.await()
    }


    @AnyThread
    fun enqueue(request: DownloadRequest): Disposable<DownloadResult> {
        val job = scope.async(Dispatchers.Main.immediate) {
            imageExecutor.execute(request) as DownloadResult
        }
        return OneShotDisposable(job)
    }

    suspend fun execute(request: DownloadRequest): DownloadResult =
        coroutineScope {
            val job = async(Dispatchers.Main.immediate) {
                imageExecutor.execute(request) as DownloadResult
            }
            job.await()
        }

    class Builder internal constructor(context: Context) {

        private val appContext: Context = context.applicationContext
        private var logger: Logger? = null
        private var memoryCache: MemoryCache? = null
        private var diskCache: DiskCache? = null
        private var bitmapPool: BitmapPool? = null
        private var componentRegistry: ComponentRegistry? = null
        private var httpStack: HttpStack? = null
        private var requestInterceptors: MutableList<RequestInterceptor>? =
            null
        private var bitmapDecodeInterceptors: MutableList<DecodeInterceptor<BitmapDecodeResult>>? =
            null
        private var drawableDecodeInterceptors: MutableList<DecodeInterceptor<DrawableDecodeResult>>? =
            null
        private var globalImageOptions: ImageOptions? = null

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

        fun addRequestInterceptor(interceptor: RequestInterceptor): Builder =
            apply {
                this.requestInterceptors = (requestInterceptors ?: mutableListOf()).apply {
                    add(interceptor)
                }
            }

        fun addBitmapDecodeInterceptor(bitmapDecodeInterceptor: DecodeInterceptor<BitmapDecodeResult>): Builder =
            apply {
                this.bitmapDecodeInterceptors =
                    (bitmapDecodeInterceptors ?: mutableListOf()).apply {
                        add(bitmapDecodeInterceptor)
                    }
            }

        fun addDrawableDecodeInterceptor(drawableDecodeInterceptor: DecodeInterceptor<DrawableDecodeResult>): Builder =
            apply {
                this.drawableDecodeInterceptors =
                    (drawableDecodeInterceptors ?: mutableListOf()).apply {
                        add(drawableDecodeInterceptor)
                    }
            }

        fun globalImageOptions(globalImageOptions: ImageOptions?): Builder = apply {
            this.globalImageOptions = globalImageOptions
        }

        internal fun build(): Sketch {
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

            val requestInterceptors: List<RequestInterceptor> =
                (requestInterceptors ?: listOf()) + EngineRequestInterceptor()

            val bitmapDecodeInterceptors: List<DecodeInterceptor<BitmapDecodeResult>> =
                (bitmapDecodeInterceptors ?: listOf()) +
                        BitmapResultDiskCacheDecodeInterceptor() +
                        BitmapTransformationDecodeInterceptor() +
                        BitmapEngineDecodeInterceptor()
            val drawableDecodeInterceptors: List<DecodeInterceptor<DrawableDecodeResult>> =
                (drawableDecodeInterceptors ?: listOf()) + DrawableEngineDecodeInterceptor()

            return Sketch(
                context = appContext,
                logger = logger,
                memoryCache = memoryCache,
                diskCache = diskCache,
                bitmapPool = bitmapPool,
                componentRegistry = componentRegistry,
                httpStack = httpStack,
                requestInterceptors = requestInterceptors,
                bitmapDecodeInterceptors = bitmapDecodeInterceptors,
                drawableDecodeInterceptors = drawableDecodeInterceptors,
                globalImageOptions = globalImageOptions,
            )
        }
    }
}