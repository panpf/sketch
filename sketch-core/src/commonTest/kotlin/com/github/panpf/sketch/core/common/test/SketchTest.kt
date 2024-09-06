package com.github.panpf.sketch.core.common.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch.Builder
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.internal.LruDiskCache
import com.github.panpf.sketch.cache.internal.LruMemoryCache
import com.github.panpf.sketch.cache.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.cache.internal.ResultCacheDecodeInterceptor
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.defaultComponents
import com.github.panpf.sketch.defaultHttpStack
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.merged
import com.github.panpf.sketch.platformComponents
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.ImageResult.Error
import com.github.panpf.sketch.request.ImageResult.Success
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.DelayDecodeInterceptor
import com.github.panpf.sketch.test.utils.ListenerSupervisor
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.transform.internal.TransformationDecodeInterceptor
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.defaultFileSystem
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SketchTest {

    @Test
    fun testBuilder() {
        // TODO networkParallelismLimited
        // TODO decodeParallelismLimited

        val context = getTestContext()
        Builder(context).apply {
            val fakePipeline = object : Logger.Pipeline {
                override fun log(level: Logger.Level, tag: String, msg: String, tr: Throwable?) {

                }

                override fun flush() {

                }

                override fun toString(): String {
                    return "FakePipeline"
                }
            }
            build().apply {
                assertEquals(Logger.Level.Info, logger.level)
                assertFalse(logger.toString().contains(fakePipeline.toString()))
            }

            logger()
            build().apply {
                assertEquals(Logger.Level.Info, logger.level)
                assertFalse(logger.toString().contains(fakePipeline.toString()))
            }

            logger(level = Logger.Level.Verbose)
            build().apply {
                assertEquals(Logger.Level.Verbose, logger.level)
                assertFalse(logger.toString().contains(fakePipeline.toString()))
            }

            logger(level = Logger.Level.Verbose, pipeline = fakePipeline)
            build().apply {
                assertEquals(
                    Logger(level = Logger.Level.Verbose, pipeline = fakePipeline),
                    logger
                )
                assertTrue(logger.toString().contains(fakePipeline.toString()))
            }

            val defaultMemoryCache = MemoryCache.Builder(context).build()
            build().apply {
                assertEquals(defaultMemoryCache, memoryCache)
            }
            val littleMemoryCacheBytes = defaultMemoryCache.maxSize / 2
            memoryCache(LruMemoryCache(littleMemoryCacheBytes))
            build().apply {
                assertEquals(LruMemoryCache(littleMemoryCacheBytes), memoryCache)
                assertNotEquals(defaultMemoryCache, memoryCache)
            }

            val defaultDownloadCache =
                DiskCache.DownloadBuilder(context, defaultFileSystem()).build()
            val defaultResultCache =
                DiskCache.ResultBuilder(context, defaultFileSystem()).build()
            build().apply {
                assertEquals(defaultDownloadCache, downloadCache)
                assertEquals(defaultResultCache, resultCache)
            }
            val littleDownloadDiskCache = LruDiskCache(
                context = context,
                fileSystem = defaultFileSystem(),
                maxSize = 50 * 1024 * 1024,
                directory = defaultDownloadCache.directory,
                appVersion = 10,
                internalVersion = 0
            )
            val littleResultDiskCache = LruDiskCache(
                context = context,
                fileSystem = defaultFileSystem(),
                maxSize = 150 * 1024 * 1024,
                directory = defaultResultCache.directory,
                appVersion = 10,
                internalVersion = 0
            )
            downloadCache(littleDownloadDiskCache)
            // TODO downloadCacheOptions
            resultCache(littleResultDiskCache)
            // TODO resultCacheOptions
            build().apply {
                assertEquals(littleDownloadDiskCache, downloadCache)
                assertEquals(littleResultDiskCache, resultCache)
            }

            build().apply {
                assertEquals(
                    expected = platformComponents().merged(defaultComponents()),
                    actual = components.registry
                )
            }

            components {
                addFetcher(TestFetcher.Factory())
                addDecoder(TestDecoder.Factory())
            }
            build().apply {
                assertEquals(
                    expected = ComponentRegistry {
                        addFetcher(TestFetcher.Factory())
                        addDecoder(TestDecoder.Factory())
                    }.merged(platformComponents().merged(defaultComponents())),
                    actual = components.registry
                )
            }

            build().apply {
                assertEquals(defaultHttpStack(), httpStack)
            }
            httpStack(TestHttpStack(context))
            build().apply {
                assertEquals(TestHttpStack(context), httpStack)
            }

            build().apply {
                assertEquals(
                    listOf(
                        MemoryCacheRequestInterceptor(),
                        EngineRequestInterceptor(),
                    ),
                    components.getRequestInterceptorList(ImageRequest(context, ""))
                )
            }
            components {
                addRequestInterceptor(TestRequestInterceptor())
            }
            build().apply {
                assertEquals(
                    listOf(
                        TestRequestInterceptor(),
                        MemoryCacheRequestInterceptor(),
                        EngineRequestInterceptor()
                    ),
                    components.getRequestInterceptorList(ImageRequest(context, ""))
                )
                assertNotEquals(
                    listOf(
                        MemoryCacheRequestInterceptor(),
                        EngineRequestInterceptor()
                    ),
                    components.getRequestInterceptorList(ImageRequest(context, ""))
                )
            }

            build().apply {
                assertEquals(
                    listOf(
                        ResultCacheDecodeInterceptor(),
                        TransformationDecodeInterceptor(),
                        EngineDecodeInterceptor()
                    ),
                    components.getDecodeInterceptorList(ImageRequest(context, ""))
                )
            }
            components {
                addDecodeInterceptor(TestDecodeInterceptor())
            }
            build().apply {
                assertEquals(
                    listOf(
                        TestDecodeInterceptor(),
                        ResultCacheDecodeInterceptor(),
                        TransformationDecodeInterceptor(),
                        EngineDecodeInterceptor()
                    ),
                    components.getDecodeInterceptorList(ImageRequest(context, ""))
                )
                assertNotEquals(
                    listOf(
                        ResultCacheDecodeInterceptor(),
                        TransformationDecodeInterceptor(),
                        EngineDecodeInterceptor()
                    ),
                    components.getDecodeInterceptorList(ImageRequest(context, ""))
                )
            }

            build().apply {
                assertNull(globalImageOptions)
            }
            globalImageOptions(ImageOptions())
            build().apply {
                assertEquals(ImageOptions(), globalImageOptions)
                assertNotNull(globalImageOptions)
            }
        }
    }

    @Test
    fun testEnqueue() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        /* success */
        val listenerSupervisor1 = ListenerSupervisor()
        val request1 = ImageRequest(context, ResourceImages.jpeg.uri) {
            registerListener(listenerSupervisor1)
        }
        val result1 = sketch.enqueue(request1).job.await()
        assertTrue(result1 is Success)
        assertEquals(listOf("onStart", "onSuccess"), listenerSupervisor1.callbackActionList)

        /* error */
        val listenerSupervisor2 = ListenerSupervisor()
        val request2 = ImageRequest(context, ResourceImages.jpeg.uri + "1") {
            registerListener(listenerSupervisor2)
        }
        val result2 = sketch.enqueue(request2).job.await()
        assertTrue(result2 is Error)
        assertEquals(listOf("onStart", "onError"), listenerSupervisor2.callbackActionList)

        /* cancel */
        var disposable3: Disposable? = null
        val listenerSupervisor3 = ListenerSupervisor()
        val request3 = ImageRequest(context, ResourceImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            // Make the execution slower, cancellation can take effect
            components {
                addDecodeInterceptor(DelayDecodeInterceptor(1000) {
                    disposable3?.job?.cancel()
                })
            }
            registerListener(listenerSupervisor3)
        }
        disposable3 = sketch.enqueue(request3)
        disposable3.job.join()
        assertEquals(listOf("onStart", "onCancel"), listenerSupervisor3.callbackActionList)
    }

    @Test
    fun testExecute() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        /* success */
        val listenerSupervisor1 = ListenerSupervisor()
        val request1 = ImageRequest(context, ResourceImages.jpeg.uri) {
            registerListener(listenerSupervisor1)
        }
        val result1 = sketch.execute(request1)
        assertTrue(result1 is Success)
        assertEquals(listOf("onStart", "onSuccess"), listenerSupervisor1.callbackActionList)

        /* error */
        val listenerSupervisor2 = ListenerSupervisor()
        val request2 = ImageRequest(context, ResourceImages.jpeg.uri + "1") {
            registerListener(listenerSupervisor2)
        }
        val result2 = sketch.execute(request2)
        assertTrue(result2 is Error)
        assertEquals(listOf("onStart", "onError"), listenerSupervisor2.callbackActionList)

        /* cancel */
        var deferred3: Deferred<ImageResult>? = null
        val listenerSupervisor3 = ListenerSupervisor {
            deferred3?.cancel()
        }
        val request3 = ImageRequest(context, ResourceImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            registerListener(listenerSupervisor3)
        }
        deferred3 = async {
            sketch.execute(request3)
        }
        deferred3.join()
        assertEquals(listOf("onStart", "onCancel"), listenerSupervisor3.callbackActionList)
    }

    @Test
    fun testShutdown() {
        val sketch = getTestContextAndNewSketch { }.second
        sketch.shutdown()
        sketch.shutdown()
    }

    @Test
    fun testSystemCallbacks() {
        val sketch = getTestContextAndSketch().second
        assertNotNull(sketch.systemCallbacks)
    }

    @Test
    fun testDefaultComponents() {

    }
}
