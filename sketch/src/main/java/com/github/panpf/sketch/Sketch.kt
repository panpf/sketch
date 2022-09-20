/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch

import android.content.ComponentCallbacks2
import android.content.Context
import android.net.ConnectivityManager.NetworkCallback
import androidx.annotation.AnyThread
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.cache.internal.LruDiskCache
import com.github.panpf.sketch.cache.internal.LruMemoryCache
import com.github.panpf.sketch.cache.internal.defaultMemoryCacheBytes
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.DrawableDecodeInterceptor
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.decode.internal.BitmapResultCacheDecodeInterceptor
import com.github.panpf.sketch.decode.internal.DefaultBitmapDecoder
import com.github.panpf.sketch.decode.internal.DefaultDrawableDecoder
import com.github.panpf.sketch.decode.internal.EngineBitmapDecodeInterceptor
import com.github.panpf.sketch.decode.internal.EngineDrawableDecodeInterceptor
import com.github.panpf.sketch.decode.internal.XmlDrawableBitmapDecoder
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.ContentUriFetcher
import com.github.panpf.sketch.fetch.Fetcher
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
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.OneShotDisposable
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.request.internal.RequestExecutor
import com.github.panpf.sketch.request.internal.requestManager
import com.github.panpf.sketch.target.ViewDisplayTarget
import com.github.panpf.sketch.transform.internal.BitmapTransformationDecodeInterceptor
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.SystemCallbacks
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.roundToLong

/**
 * A service class that performs an [ImageRequest] to load an image.
 *
 * Sketch is responsible for handling data acquisition, image decoding, image conversion,
 * processing cache, request management, memory management and other functions.
 *
 * You just need to create an instance of the ImageRequest subclass and pass it to the [enqueue] or [execute] method for execution.
 *
 * Sketch is designed to be sharable and works best when the same instance is used throughout the
 * application via the built-in extension function `Context.sketch`
 */
class Sketch private constructor(
    _context: Context,
    _logger: Logger?,
    _memoryCache: MemoryCache?,
    _downloadCache: DiskCache?,
    _resultCache: DiskCache?,
    _bitmapPool: BitmapPool?,
    _componentRegistry: ComponentRegistry?,
    _httpStack: HttpStack?,
    _globalImageOptions: ImageOptions?,
) {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate + CoroutineExceptionHandler { _, throwable ->
            logger.e("scope", throwable, "exception")
        }
    )
    private val requestExecutor = RequestExecutor()
    private val isShutdown = AtomicBoolean(false)

    /** Application Context */
    val context: Context = _context.applicationContext

    /** Output log */
    val logger: Logger = _logger ?: Logger()

    /** Memory cache of previously loaded images */
    val memoryCache: MemoryCache

    /** Reuse Bitmap */
    val bitmapPool: BitmapPool

    /** Disk caching of http downloads images */
    val downloadCache: DiskCache =
        _downloadCache ?: LruDiskCache.ForDownloadBuilder(context).build()

    /** Disk caching of transformed images */
    val resultCache: DiskCache =
        _resultCache ?: LruDiskCache.ForResultBuilder(context).build()

    /** Execute HTTP request */
    val httpStack: HttpStack = _httpStack ?: HurlStack.Builder().build()

    /** Fill unset [ImageRequest] value */
    val globalImageOptions: ImageOptions? = _globalImageOptions

    /** Register components that are required to perform [ImageRequest] and can be extended,
     * such as [Fetcher], [BitmapDecoder], [DrawableDecoder], [RequestInterceptor], [BitmapDecodeInterceptor], [DrawableDecodeInterceptor] */
    val components: Components

    /** Proxies [ComponentCallbacks2] and [NetworkCallback]. Clear memory cache when system memory is low, and monitor network connection status */
    val systemCallbacks = SystemCallbacks(context, WeakReference(this))

    /* Limit the number of concurrent network tasks, too many network tasks will cause network congestion */
    @OptIn(ExperimentalCoroutinesApi::class)
    val networkTaskDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(10)

    /* Limit the number of concurrent decoding tasks because too many concurrent BitmapFactory tasks can affect UI performance */
    @OptIn(ExperimentalCoroutinesApi::class)
    val decodeTaskDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(4)

    init {
        val defaultMemoryCacheBytes = context.defaultMemoryCacheBytes()
        memoryCache = _memoryCache
            ?: LruMemoryCache((defaultMemoryCacheBytes * 0.66f).roundToLong())
        bitmapPool = _bitmapPool
            ?: LruBitmapPool((defaultMemoryCacheBytes * 0.33f).roundToLong())
        memoryCache.logger = logger
        bitmapPool.logger = logger
        downloadCache.logger = logger
        resultCache.logger = logger

        val componentRegistry =
            (_componentRegistry?.newBuilder() ?: ComponentRegistry.Builder()).apply {
                addFetcher(HttpUriFetcher.Factory())
                addFetcher(FileUriFetcher.Factory())
                addFetcher(ContentUriFetcher.Factory())
                addFetcher(ResourceUriFetcher.Factory())
                addFetcher(AssetUriFetcher.Factory())
                addFetcher(Base64UriFetcher.Factory())

                addBitmapDecoder(XmlDrawableBitmapDecoder.Factory())
                addBitmapDecoder(DefaultBitmapDecoder.Factory())

                addDrawableDecoder(DefaultDrawableDecoder.Factory())

                addRequestInterceptor(MemoryCacheRequestInterceptor())
                addRequestInterceptor(EngineRequestInterceptor())

                addBitmapDecodeInterceptor(BitmapResultCacheDecodeInterceptor())
                addBitmapDecodeInterceptor(BitmapTransformationDecodeInterceptor())
                addBitmapDecodeInterceptor(EngineBitmapDecodeInterceptor())

                addDrawableDecodeInterceptor(EngineDrawableDecodeInterceptor())
            }.build()
        components = Components(this, componentRegistry)

        logger.d("Configuration") {
            buildString {
                append("\n").append("logger: $logger")
                append("\n").append("httpStack: $httpStack")
                append("\n").append("memoryCache: $memoryCache")
                append("\n").append("bitmapPool: $bitmapPool")
                append("\n").append("downloadCache: $downloadCache")
                append("\n").append("resultCache: $resultCache")
                append("\n").append("fetchers: ${componentRegistry.fetcherFactoryList}")
                append("\n").append("bitmapDecoders: ${componentRegistry.bitmapDecoderFactoryList}")
                append("\n").append("drawableDecoders: ${componentRegistry.drawableDecoderFactoryList}")
                append("\n").append("requestInterceptors: ${componentRegistry.requestInterceptorList}")
                append("\n").append("bitmapDecodeInterceptors: ${componentRegistry.bitmapDecodeInterceptorList}")
                append("\n").append("drawableDecodeInterceptors: ${componentRegistry.drawableDecodeInterceptorList}")
            }
        }
    }


    /**
     * Execute the DisplayRequest asynchronously.
     *
     * Note: The request will not start executing until [ImageRequest.lifecycle]
     * reaches [Lifecycle.State.STARTED] state and [ViewDisplayTarget.view] is attached to window
     *
     * @return A [Disposable] which can be used to cancel or check the status of the request.
     */
    @AnyThread
    fun enqueue(request: DisplayRequest): Disposable<DisplayResult> {
        val job = scope.async {
            requestExecutor.execute(this@Sketch, request, enqueue = true) as DisplayResult
        }
        val target = request.target
        return if (target is ViewDisplayTarget<*>) {
            target.view.requestManager.getDisposable(job)
        } else {
            OneShotDisposable(job)
        }
    }

    /**
     * Execute the DisplayRequest synchronously in the current coroutine scope.
     *
     * Note: The request will not start executing until [ImageRequest.lifecycle]
     * reaches [Lifecycle.State.STARTED] state and [ViewDisplayTarget.view] is attached to window
     *
     * @return A [DisplayResult.Success] if the request completes successfully. Else, returns an [DisplayResult.Error].
     */
    suspend fun execute(request: DisplayRequest): DisplayResult =
        coroutineScope {
            val job = async(Dispatchers.Main.immediate) {
                requestExecutor.execute(this@Sketch, request, enqueue = false) as DisplayResult
            }
            // Update the current request attached to the view and await the result.
            val target = request.target
            if (target is ViewDisplayTarget<*>) {
                target.view.requestManager.getDisposable(job)
            }
            job.await()
        }


    /**
     * Execute the LoadRequest asynchronously.
     *
     * Note: The request will not start executing until [ImageRequest.lifecycle]
     * reaches [Lifecycle.State.STARTED] state and [ViewDisplayTarget.view] is attached to window
     *
     * @return A [Disposable] which can be used to cancel or check the status of the request.
     */
    @AnyThread
    fun enqueue(request: LoadRequest): Disposable<LoadResult> {
        val job = scope.async {
            requestExecutor.execute(this@Sketch, request, enqueue = true) as LoadResult
        }
        return OneShotDisposable(job)
    }

    /**
     * Execute the LoadRequest synchronously in the current coroutine scope.
     *
     * Note: The request will not start executing until [ImageRequest.lifecycle]
     * reaches [Lifecycle.State.STARTED] state and [ViewDisplayTarget.view] is attached to window
     *
     * @return A [LoadResult.Success] if the request completes successfully. Else, returns an [LoadResult.Error].
     */
    suspend fun execute(request: LoadRequest): LoadResult = coroutineScope {
        val job = async(Dispatchers.Main.immediate) {
            requestExecutor.execute(this@Sketch, request, enqueue = false) as LoadResult
        }
        job.await()
    }


    /**
     * Execute the DownloadRequest asynchronously.
     *
     * Note: The request will not start executing until [ImageRequest.lifecycle]
     * reaches [Lifecycle.State.STARTED] state and [ViewDisplayTarget.view] is attached to window
     *
     * @return A [Disposable] which can be used to cancel or check the status of the request.
     */
    @AnyThread
    fun enqueue(request: DownloadRequest): Disposable<DownloadResult> {
        val job = scope.async {
            requestExecutor.execute(this@Sketch, request, enqueue = true) as DownloadResult
        }
        return OneShotDisposable(job)
    }

    /**
     * Execute the DownloadRequest synchronously in the current coroutine scope.
     *
     * Note: The request will not start executing until [ImageRequest.lifecycle]
     * reaches [Lifecycle.State.STARTED] state and [ViewDisplayTarget.view] is attached to window
     *
     * @return A [DownloadResult.Success] if the request completes successfully. Else, returns an [DownloadResult.Error].
     */
    suspend fun execute(request: DownloadRequest): DownloadResult =
        coroutineScope {
            val job = async(Dispatchers.Main.immediate) {
                requestExecutor.execute(this@Sketch, request, enqueue = false) as DownloadResult
            }
            job.await()
        }

    /**
     * Cancel any new and in progress requests, clear the [MemoryCache] and [BitmapPool], and close any open
     * system resources.
     *
     * Shutting down an image loader is optional.
     */
    fun shutdown() {
        if (isShutdown.getAndSet(true)) return
        scope.cancel()
        systemCallbacks.shutdown()
        memoryCache.clear()
        downloadCache.close()
        resultCache.close()
        bitmapPool.clear()
    }

    class Builder constructor(context: Context) {

        private val appContext: Context = context.applicationContext
        private var logger: Logger? = null
        private var memoryCache: MemoryCache? = null
        private var downloadCache: DiskCache? = null
        private var resultCache: DiskCache? = null
        private var bitmapPool: BitmapPool? = null
        private var componentRegistry: ComponentRegistry? = null
        private var httpStack: HttpStack? = null
        private var globalImageOptions: ImageOptions? = null

        /**
         * Set the [Logger] to write logs to.
         */
        fun logger(logger: Logger?): Builder = apply {
            this.logger = logger
        }

        /**
         * Set the [MemoryCache]
         */
        fun memoryCache(memoryCache: MemoryCache?): Builder = apply {
            this.memoryCache = memoryCache
        }

        /**
         * Set the [DiskCache] for download cache
         */
        fun downloadCache(diskCache: DiskCache?): Builder = apply {
            this.downloadCache = diskCache
        }

        /**
         * Set the [DiskCache] for result cache
         */
        fun resultCache(diskCache: DiskCache?): Builder = apply {
            this.resultCache = diskCache
        }

        /**
         * Set the [BitmapPool]
         */
        fun bitmapPool(bitmapPool: BitmapPool?): Builder = apply {
            this.bitmapPool = bitmapPool
        }

        /**
         * Set the [ComponentRegistry]
         */
        fun components(components: ComponentRegistry?): Builder = apply {
            this.componentRegistry = components
        }

        /**
         * Build and set the [ComponentRegistry]
         */
        fun components(configBlock: (ComponentRegistry.Builder.() -> Unit)): Builder =
            components(ComponentRegistry.Builder().apply(configBlock).build())

        /**
         * Set the [HttpStack] used for network requests.
         */
        fun httpStack(httpStack: HttpStack?): Builder = apply {
            this.httpStack = httpStack
        }

        /**
         * Set an [ImageOptions], fill unset [ImageRequest] value
         */
        fun globalImageOptions(globalImageOptions: ImageOptions?): Builder = apply {
            this.globalImageOptions = globalImageOptions
        }

        fun build(): Sketch = Sketch(
            _context = appContext,
            _logger = logger,
            _memoryCache = memoryCache,
            _downloadCache = downloadCache,
            _resultCache = resultCache,
            _bitmapPool = bitmapPool,
            _componentRegistry = componentRegistry,
            _httpStack = httpStack,
            _globalImageOptions = globalImageOptions,
        )
    }
}