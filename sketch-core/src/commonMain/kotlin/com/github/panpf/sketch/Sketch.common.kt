/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.annotation.AnyThread
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.cache.internal.ResultCacheDecodeInterceptor
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.OneShotDisposable
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.RequestExecutor
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.transform.internal.TransformationDecodeInterceptor
import com.github.panpf.sketch.util.DownloadData
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Logger.Level
import com.github.panpf.sketch.util.Logger.Pipeline
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SystemCallbacks
import com.github.panpf.sketch.util.application
import com.github.panpf.sketch.util.defaultFileSystem
import com.github.panpf.sketch.util.defaultLogPipeline
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import com.github.panpf.sketch.util.isMainThread
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okio.FileSystem

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
 *
 * @see com.github.panpf.sketch.core.common.test.SketchTest
 * @see com.github.panpf.sketch.core.android.test.SketchAndroidTest
 * @see com.github.panpf.sketch.core.desktop.test.SketchDesktopTest
 * @see com.github.panpf.sketch.core.jscommon.test.SketchJsCommonTest
 * @see com.github.panpf.sketch.core.ios.test.SketchIosTest
 * @see com.github.panpf.sketch.core.jvmcommon.test.SketchJvmTest
 * @see com.github.panpf.sketch.core.nonjvmcommon.test.SketchNonJvmTest
 */
class Sketch private constructor(options: Options) {

    private val requestExecutor = RequestExecutor(this)
    private val _isShutdown = atomic(false)
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate + CoroutineExceptionHandler { _, throwable ->
            logger.e(throwable, "CoroutineScope. An uncaught exception")
        }
    )

    /** Application Context */
    val context: PlatformContext = options.context.application

    /** Output log */
    val logger: Logger = options.logger

    /** File system */
    val fileSystem: FileSystem = options.fileSystem

    /** Memory cache of previously loaded images */
    val memoryCache: MemoryCache by options.memoryCacheLazy

    /** Disk caching of http downloads images */
    val downloadCache: DiskCache by options.downloadCacheLazy

    /** Disk caching of transformed images */
    val resultCache: DiskCache by options.resultCacheLazy

    /** Execute HTTP request */
    val httpStack: HttpStack = options.httpStack

    /** Fill unset [ImageRequest] value */
    val globalImageOptions: ImageOptions? = options.globalImageOptions

    /** Register components that are required to perform [ImageRequest] and can be extended,
     * such as [Fetcher], [Decoder], [RequestInterceptor], [DecodeInterceptor] */
    val components: Components = Components(options.componentRegistry)

    /** Monitor network connection and system status */
    val systemCallbacks = SystemCallbacks(this)

    /** Limit the number of concurrent network tasks, too many network tasks will cause network congestion */
    @OptIn(ExperimentalCoroutinesApi::class)
    val networkTaskDispatcher: CoroutineDispatcher = ioCoroutineDispatcher().let { dispatcher ->
        options.networkParallelismLimited
            .takeIf { parallelism -> parallelism > 0 }
            ?.let { parallelism -> dispatcher.limitedParallelism(parallelism) }
            ?: dispatcher
    }

    /** Limit the number of concurrent decoding tasks because too many concurrent BitmapFactory tasks can affect UI performance */
    @OptIn(ExperimentalCoroutinesApi::class)
    val decodeTaskDispatcher: CoroutineDispatcher = ioCoroutineDispatcher().let { dispatcher ->
        options.decodeParallelismLimited
            .takeIf { parallelism -> parallelism > 0 }
            ?.let { parallelism -> dispatcher.limitedParallelism(parallelism) }
            ?: dispatcher
    }

    val isShutdown: Boolean get() = _isShutdown.value

    init {
        checkPlatformContext(context)
        systemCallbacks.register()
        logger.d {
            buildString {
                append("Configuration. ")
                appendLine().append("logger: $logger")
                appendLine().append("httpStack: $httpStack")
                appendLine().append("memoryCache: $memoryCache")
                appendLine().append("resultCache: $resultCache")
                appendLine().append("downloadCache: $downloadCache")
                appendLine().append("fetchers: ${options.componentRegistry.fetcherFactoryList}")
                appendLine().append("decoders: ${options.componentRegistry.decoderFactoryList}")
                appendLine().append("requestInterceptors: ${options.componentRegistry.requestInterceptorList}")
                appendLine().append("decodeInterceptors: ${options.componentRegistry.decodeInterceptorList}")
                appendLine().append("networkParallelismLimited: ${options.networkParallelismLimited}")
                appendLine().append("decodeParallelismLimited: ${options.decodeParallelismLimited}")
            }
        }
    }


    /**
     * Execute the ImageRequest asynchronously.
     *
     * Note: The request will not start executing until Lifecycle state is STARTED
     * reaches [Lifecycle.State.STARTED] state and View is attached to window
     *
     * @return A [Disposable] which can be used to cancel or check the status of the request.
     */
    @AnyThread
    fun enqueue(request: ImageRequest): Disposable {
        val job = scope.async {
            requestExecutor.execute(request, enqueue = true)
        }
        // Update the current request attached to the view and return a new disposable.
        val requestManager = request.target?.getRequestManager()
        return requestManager?.getDisposable(job) ?: OneShotDisposable(job)
    }

    /**
     * Execute the ImageRequest synchronously in the current coroutine scope.
     *
     * Note: The request will not start executing until Lifecycle state is STARTED
     * reaches [Lifecycle.State.STARTED] state and View is attached to window
     *
     * @return A [ImageResult.Success] if the request completes successfully. Else, returns an [ImageResult.Error].
     */
    suspend fun execute(request: ImageRequest): ImageResult = coroutineScope {
        val job = async(Dispatchers.Main.immediate) {
            requestExecutor.execute(request, enqueue = false)
        }
        // Update the current request attached to the view and await the result.
        val requestManager = request.target?.getRequestManager()
        val disposable = requestManager?.getDisposable(job) ?: OneShotDisposable(job)
        return@coroutineScope disposable.job.await()
    }


    /**
     * Download images
     */
    suspend fun executeDownload(request: ImageRequest): Result<DownloadData> = kotlin.runCatching {
        val requestContext = RequestContext(this, request, Size.Empty)
        val fetcher = components.newFetcherOrThrow(requestContext)
        val fetchResultResult = if (!isMainThread()) {
            fetcher.fetch()
        } else {
            withContext(ioCoroutineDispatcher()) {
                fetcher.fetch()
            }
        }
        val fetchResult = fetchResultResult.getOrThrow()
        @Suppress("MoveVariableDeclarationIntoWhen") val dataSource = fetchResult.dataSource
        when (dataSource) {
            is FileDataSource -> DownloadData.Cache(downloadCache.fileSystem, dataSource.path)
            is ByteArrayDataSource -> DownloadData.Bytes(dataSource.data)
            else -> throw IllegalArgumentException("Unknown dataSource: $dataSource")
        }
    }

    /**
     * Download images
     */
    fun enqueueDownload(request: ImageRequest): Deferred<Result<DownloadData>> {
        val job = scope.async {
            executeDownload(request)
        }
        return job
    }


    /**
     * Cancel any new and in progress requests, clear the [MemoryCache] and [DiskCache], and close any open
     * system resources.
     *
     * Shutting down an image loader is optional.
     */
    fun shutdown() {
        if (_isShutdown.getAndSet(true)) return
        scope.cancel()
        systemCallbacks.shutdown()
        memoryCache.clear()
        downloadCache.close()
        resultCache.close()
    }

    data class Options(
        val context: PlatformContext,
        val logger: Logger,
        val fileSystem: FileSystem,
        val memoryCacheLazy: Lazy<MemoryCache>,
        val downloadCacheLazy: Lazy<DiskCache>,
        val resultCacheLazy: Lazy<DiskCache>,
        val httpStack: HttpStack,
        val componentRegistry: ComponentRegistry,
        val globalImageOptions: ImageOptions?,
        val networkParallelismLimited: Int,
        val decodeParallelismLimited: Int,
    )

    class Builder(context: PlatformContext) {

        private val context: PlatformContext = context.application
        private var logger: Logger? = null
        private var fileSystem: FileSystem? = null
        private var memoryCacheLazy: Lazy<MemoryCache>? = null
        private var downloadCacheLazy: Lazy<DiskCache>? = null
        private var downloadCacheOptionsLazy: Lazy<DiskCache.Options>? = null
        private var resultCacheLazy: Lazy<DiskCache>? = null
        private var resultCacheOptionsLazy: Lazy<DiskCache.Options>? = null

        private var componentRegistry: ComponentRegistry? = null
        private var httpStack: HttpStack? = null
        private var globalImageOptions: ImageOptions? = null
        private var networkParallelismLimited: Int? = null
        private var decodeParallelismLimited: Int? = null

        init {
            checkPlatformContext(this.context)
        }

        /**
         * Set the [Logger] to write logs to.
         */
        fun logger(
            level: Level = Level.Info,
            pipeline: Pipeline = defaultLogPipeline()
        ): Builder = apply {
            this.logger = Logger(level, pipeline)
        }

        /**
         * Set the [FileSystem] used for disk io.
         */
        fun fileSystem(fileSystem: FileSystem?): Builder = apply {
            this.fileSystem = fileSystem
        }

        /**
         * Set the [MemoryCache]
         */
        fun memoryCache(memoryCache: MemoryCache?): Builder = apply {
            this.memoryCacheLazy = memoryCache?.let { lazyOf(it) }
        }

        /**
         * Set the [MemoryCache]
         */
        fun memoryCache(initializer: () -> MemoryCache): Builder = apply {
            this.memoryCacheLazy = lazy { initializer() }
        }

        /**
         * Set the download [DiskCache]
         */
        fun downloadCache(diskCache: DiskCache?): Builder = apply {
            this.downloadCacheLazy = diskCache?.let { lazyOf(it) }
            this.downloadCacheOptionsLazy = null
        }

        /**
         * Set the download [DiskCache]
         */
        fun downloadCache(initializer: () -> DiskCache): Builder = apply {
            this.downloadCacheLazy = lazy { initializer() }
            this.downloadCacheOptionsLazy = null
        }

        /**
         * Set the download [DiskCache]
         */
        fun downloadCacheOptions(options: DiskCache.Options): Builder = apply {
            this.downloadCacheOptionsLazy = lazyOf(options)
            this.downloadCacheLazy = null
        }

        /**
         * Set the download [DiskCache]
         */
        fun downloadCacheOptions(initializer: () -> DiskCache.Options): Builder = apply {
            this.downloadCacheOptionsLazy = lazy { initializer() }
            this.downloadCacheLazy = null
        }

        /**
         * Set the result [DiskCache]
         */
        fun resultCache(diskCache: DiskCache?): Builder = apply {
            this.resultCacheLazy = diskCache?.let { lazyOf(it) }
        }

        /**
         * Set the result [DiskCache]
         */
        fun resultCache(initializer: () -> DiskCache): Builder = apply {
            this.resultCacheLazy = lazy { initializer() }
        }

        /**
         * Set the result [DiskCache]
         */
        fun resultCacheOptions(options: DiskCache.Options): Builder = apply {
            this.resultCacheOptionsLazy = lazyOf(options)
            this.resultCacheLazy = null
        }

        /**
         * Set the result [DiskCache]
         */
        fun resultCacheOptions(initializer: () -> DiskCache.Options): Builder = apply {
            this.resultCacheOptionsLazy = lazy { initializer() }
            this.resultCacheLazy = null
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
         * Merge the [ComponentRegistry]
         */
        fun addComponents(components: ComponentRegistry?): Builder = apply {
            this.componentRegistry = this.componentRegistry.merged(components)
        }

        /**
         * Merge the [ComponentRegistry]
         */
        fun addComponents(configBlock: (ComponentRegistry.Builder.() -> Unit)): Builder =
            addComponents(ComponentRegistry.Builder().apply(configBlock).build())

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

        /**
         * Set the maximum number of concurrent network tasks. No limit when less than or equal to 0
         */
        fun networkParallelismLimited(parallelism: Int?): Builder = apply {
            this.networkParallelismLimited = parallelism
        }

        /**
         * Set the maximum number of concurrent decode tasks. No limit when less than or equal to 0
         */
        fun decodeParallelismLimited(parallelism: Int?): Builder = apply {
            this.decodeParallelismLimited = parallelism
        }

        fun build(): Sketch {
            val finalFileSystem = fileSystem ?: defaultFileSystem()
            val componentRegistry1 = componentRegistry
                .merged(platformComponents(context))
                .merged(defaultComponents())
                ?: ComponentRegistry.Builder().build()
            val options = Options(
                context = context,
                logger = this.logger ?: Logger(),
                fileSystem = finalFileSystem,
                memoryCacheLazy = memoryCacheLazy ?: lazy { MemoryCache.Builder(context).build() },
                downloadCacheLazy = downloadCacheLazy ?: lazy {
                    val options = downloadCacheOptionsLazy?.value
                    DiskCache.DownloadBuilder(context, finalFileSystem).apply {
                        if (options != null) {
                            options(options)
                        }
                    }.build()
                },
                resultCacheLazy = resultCacheLazy ?: lazy {
                    val options = resultCacheOptionsLazy?.value
                    DiskCache.ResultBuilder(context, finalFileSystem).apply {
                        if (options != null) {
                            options(options)
                        }
                    }.build()
                },
                httpStack = httpStack ?: defaultHttpStack(),
                componentRegistry = componentRegistry1,
                globalImageOptions = globalImageOptions,
                networkParallelismLimited = networkParallelismLimited ?: 10,
                decodeParallelismLimited = decodeParallelismLimited ?: 4,
            )
            return Sketch(options)
        }
    }
}

/**
 * Provide platform-related components
 *
 * @see com.github.panpf.sketch.core.android.test.SketchAndroidTest.testPlatformComponents
 * @see com.github.panpf.sketch.core.desktop.test.SketchDesktopTest.testPlatformComponents
 * @see com.github.panpf.sketch.core.jscommon.test.SketchJsCommonTest.testPlatformComponents
 * @see com.github.panpf.sketch.core.ios.test.SketchIosTest.testPlatformComponents
 */
internal expect fun platformComponents(context: PlatformContext): ComponentRegistry

/**
 * Provide components applicable to all platforms
 *
 * @see com.github.panpf.sketch.core.common.test.SketchTest.testDefaultComponents
 */
internal fun defaultComponents(): ComponentRegistry {
    return ComponentRegistry {
        addFetcher(HttpUriFetcher.Factory())
        addFetcher(Base64UriFetcher.Factory())
        addFetcher(FileUriFetcher.Factory())

        addRequestInterceptor(MemoryCacheRequestInterceptor())
        addRequestInterceptor(EngineRequestInterceptor())

        addDecodeInterceptor(ResultCacheDecodeInterceptor())
        addDecodeInterceptor(TransformationDecodeInterceptor())
        addDecodeInterceptor(EngineDecodeInterceptor())
    }
}

/**
 * Provide a default [HttpStack] for network requests
 *
 * @see com.github.panpf.sketch.core.jvmcommon.test.SketchJvmTest.testDefaultHttpStack
 * @see com.github.panpf.sketch.core.nonjvmcommon.test.SketchNonJvmTest.testDefaultHttpStack
 */
internal expect fun defaultHttpStack(): HttpStack