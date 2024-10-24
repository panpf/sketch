package com.github.panpf.sketch.core.common.test

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.cache.internal.ResultCacheDecodeInterceptor
import com.github.panpf.sketch.commonComponents
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.fetch.KtorHttpUriFetcher
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
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.runInNewSketchWithUse
import com.github.panpf.sketch.transform.internal.TransformationDecodeInterceptor
import com.github.panpf.sketch.util.ComponentLoader
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.defaultFileSystem
import com.github.panpf.sketch.util.toComponentRegistry
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
        val context = getTestContext()

        // logger
        val fakePipeline = object : Logger.Pipeline {
            override fun log(level: Logger.Level, tag: String, msg: String, tr: Throwable?) {

            }

            override fun flush() {

            }

            override fun toString(): String {
                return "FakePipeline"
            }
        }
        Sketch.Builder(context).build().apply {
            assertEquals(Logger.Level.Info, logger.level)
            assertFalse(logger.toString().contains(fakePipeline.toString()))
        }

        Sketch.Builder(context).apply {
            logger()
        }.build().apply {
            assertEquals(Logger.Level.Info, logger.level)
            assertFalse(logger.toString().contains(fakePipeline.toString()))
        }

        Sketch.Builder(context).apply {
            logger(level = Logger.Level.Verbose)
        }.build().apply {
            assertEquals(Logger.Level.Verbose, logger.level)
            assertFalse(logger.toString().contains(fakePipeline.toString()))
        }

        Sketch.Builder(context).apply {
            logger(level = Logger.Level.Verbose, pipeline = fakePipeline)
        }.build().apply {
            assertEquals(
                Logger(level = Logger.Level.Verbose, pipeline = fakePipeline),
                logger
            )
            assertTrue(logger.toString().contains(fakePipeline.toString()))
        }

        // memoryCache
        val memoryCache0 = MemoryCache.Builder(context).build()
        Sketch.Builder(context).build().apply {
            assertEquals(expected = memoryCache0, actual = memoryCache)
        }

        val memoryCache1 = MemoryCache.Builder(context).apply {
            maxSizeBytes(50 * 1024 * 1024)
        }.build()
        Sketch.Builder(context).apply {
            memoryCache(memoryCache1)
        }.build().apply {
            assertEquals(expected = memoryCache1, actual = memoryCache)
        }

        val memoryCache2 = MemoryCache.Builder(context).apply {
            maxSizeBytes(150 * 1024 * 1024)
        }.build()
        Sketch.Builder(context).apply {
            memoryCache { memoryCache2 }
        }.build().apply {
            assertEquals(expected = memoryCache2, actual = memoryCache)
        }

        // downloadCache
        val downloadCache0 = DiskCache.DownloadBuilder(context, defaultFileSystem()).build()
        Sketch.Builder(context).build().apply {
            assertEquals(expected = downloadCache0, actual = downloadCache)
        }

        val downloadCache1 = DiskCache.DownloadBuilder(context, defaultFileSystem()).apply {
            maxSize(50 * 1024 * 1024)
        }.build()
        Sketch.Builder(context).apply {
            downloadCache(downloadCache1)
        }.build().apply {
            assertEquals(expected = downloadCache1, actual = downloadCache)
        }

        val downloadCache2 = DiskCache.DownloadBuilder(context, defaultFileSystem()).apply {
            maxSize(20 * 1024 * 1024)
        }.build()
        Sketch.Builder(context).apply {
            downloadCache { downloadCache2 }
        }.build().apply {
            assertEquals(expected = downloadCache2, actual = downloadCache)
        }

        val downloadCache3 = DiskCache.DownloadBuilder(context, defaultFileSystem()).apply {
            maxSize(10 * 1024 * 1024)
        }.build()
        Sketch.Builder(context).apply {
            downloadCacheOptions(DiskCache.Options(maxSize = downloadCache3.maxSize))
        }.build().apply {
            assertEquals(expected = downloadCache3, actual = downloadCache)
        }

        val downloadCache4 = DiskCache.DownloadBuilder(context, defaultFileSystem()).apply {
            maxSize(5 * 1024 * 1024)
        }.build()
        Sketch.Builder(context).apply {
            downloadCacheOptions { DiskCache.Options(maxSize = downloadCache4.maxSize) }
        }.build().apply {
            assertEquals(expected = downloadCache4, actual = downloadCache)
        }

        // resultCache
        val resultCache0 = DiskCache.ResultBuilder(context, defaultFileSystem()).build()
        Sketch.Builder(context).build().apply {
            assertEquals(expected = resultCache0, actual = resultCache)
        }

        val resultCache1 = DiskCache.ResultBuilder(context, defaultFileSystem()).apply {
            maxSize(50 * 1024 * 1024)
        }.build()
        Sketch.Builder(context).apply {
            resultCache(resultCache1)
        }.build().apply {
            assertEquals(expected = resultCache1, actual = resultCache)
        }

        val resultCache2 = DiskCache.ResultBuilder(context, defaultFileSystem()).apply {
            maxSize(20 * 1024 * 1024)
        }.build()
        Sketch.Builder(context).apply {
            resultCache { resultCache2 }
        }.build().apply {
            assertEquals(expected = resultCache2, actual = resultCache)
        }

        val resultCache3 = DiskCache.ResultBuilder(context, defaultFileSystem()).apply {
            maxSize(10 * 1024 * 1024)
        }.build()
        Sketch.Builder(context).apply {
            resultCacheOptions(DiskCache.Options(maxSize = resultCache3.maxSize))
        }.build().apply {
            assertEquals(expected = resultCache3, actual = resultCache)
        }

        val resultCache4 = DiskCache.ResultBuilder(context, defaultFileSystem()).apply {
            maxSize(5 * 1024 * 1024)
        }.build()
        Sketch.Builder(context).apply {
            resultCacheOptions { DiskCache.Options(maxSize = resultCache4.maxSize) }
        }.build().apply {
            assertEquals(expected = resultCache4, actual = resultCache)
        }

        // components: Fetcher, Decoder
        Sketch.Builder(context).build().apply {
            assertEquals(
                expected = ComponentLoader.toComponentRegistry(context)
                    .merged(platformComponents(context)).merged(commonComponents()).toString(),
                actual = components.registry.toString()
            )
            assertNotNull(components.registry.fetcherFactoryList.find { it is KtorHttpUriFetcher.Factory })
        }

        // components: Fetcher, Decoder
        Sketch.Builder(context).apply {
            componentLoaderEnabled(false)
        }.build().apply {
            assertEquals(
                expected = platformComponents(context).merged(commonComponents()),
                actual = components.registry
            )
            assertNull(components.registry.fetcherFactoryList.find { it is KtorHttpUriFetcher.Factory })
        }

        Sketch.Builder(context).apply {
            componentLoaderEnabled(false)
        }.apply {
            components {
                addFetcher(TestFetcher.Factory())
                addDecoder(TestDecoder.Factory())
            }
        }.build().apply {
            assertEquals(
                expected = ComponentRegistry {
                    addFetcher(TestFetcher.Factory())
                    addDecoder(TestDecoder.Factory())
                }.merged(platformComponents(context).merged(commonComponents())),
                actual = components.registry
            )
        }

        // addIgnoreComponentProviderClasses
        Sketch.Builder(context).build().apply {
            assertEquals(
                expected = ComponentLoader.toComponentRegistry(context)
                    .merged(platformComponents(context).merged(commonComponents())).toString(),
                actual = components.registry.toString()
            )
        }
        Sketch.Builder(context).apply {
            // There is only one KtorHttpUriFetcherProvider in the current environment
            addIgnoreFetcherProvider(ComponentLoader.fetchers.first()::class)
        }.build().apply {
            assertEquals(
                expected = platformComponents(context).merged(commonComponents()).toString(),
                actual = components.registry.toString()
            )
        }

        // components: RequestInterceptor
        Sketch.Builder(context).build().apply {
            assertEquals(
                listOf(
                    MemoryCacheRequestInterceptor(),
                    EngineRequestInterceptor(),
                ),
                components.getRequestInterceptorList(ImageRequest(context, ""))
            )
        }

        Sketch.Builder(context).apply {
            components {
                addRequestInterceptor(TestRequestInterceptor())
            }
        }.build().apply {
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

        // components: DecodeInterceptor
        Sketch.Builder(context).build().apply {
            assertEquals(
                listOf(
                    ResultCacheDecodeInterceptor(),
                    TransformationDecodeInterceptor(),
                    EngineDecodeInterceptor()
                ),
                components.getDecodeInterceptorList(ImageRequest(context, ""))
            )
        }

        Sketch.Builder(context).apply {
            components {
                addDecodeInterceptor(TestDecodeInterceptor())
            }
        }.build().apply {
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

        // globalImageOptions
        Sketch.Builder(context).build().apply {
            assertNull(globalImageOptions)
        }

        Sketch.Builder(context).apply {
            globalImageOptions(ImageOptions())
        }.build().apply {
            assertEquals(ImageOptions(), globalImageOptions)
            assertNotNull(globalImageOptions)
        }

        // The tests for networkParallelismLimited and decodeParallelismLimited are located at
        //  'com.github.panpf.sketch.core.desktop.test.SketchDesktopTest.testBuilder'
    }

    @Test
    fun testEnqueue() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        /* success */
        val listenerSupervisor1 = ListenerSupervisor()
        val request1 = ImageRequest(context, ResourceImages.jpeg.uri) {
            addListener(listenerSupervisor1)
        }
        val result1 = sketch.enqueue(request1).job.await()
        assertTrue(result1 is Success)
        assertEquals(listOf("onStart", "onSuccess"), listenerSupervisor1.callbackActionList)

        /* error */
        val listenerSupervisor2 = ListenerSupervisor()
        val request2 = ImageRequest(context, ResourceImages.jpeg.uri + "1") {
            addListener(listenerSupervisor2)
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
            addListener(listenerSupervisor3)
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
            addListener(listenerSupervisor1)
        }
        val result1 = sketch.execute(request1)
        assertTrue(result1 is Success)
        assertEquals(listOf("onStart", "onSuccess"), listenerSupervisor1.callbackActionList)

        /* error */
        val listenerSupervisor2 = ListenerSupervisor()
        val request2 = ImageRequest(context, ResourceImages.jpeg.uri + "1") {
            addListener(listenerSupervisor2)
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
            addListener(listenerSupervisor3)
        }
        deferred3 = async {
            sketch.execute(request3)
        }
        deferred3.join()
        assertEquals(listOf("onStart", "onCancel"), listenerSupervisor3.callbackActionList)
    }

    @Test
    fun testShutdown() = runTest {
        runInNewSketchWithUse({
        }) { _, sketch ->
            sketch.shutdown()
            sketch.shutdown()
        }
    }

    @Test
    fun testSystemCallbacks() {
        val sketch = getTestContextAndSketch().second
        assertNotNull(sketch.systemCallbacks)
    }

    @Test
    fun testCommonComponents() {
        assertEquals(
            expected = ComponentRegistry {
                addFetcher(Base64UriFetcher.Factory())
                addFetcher(FileUriFetcher.Factory())

                addRequestInterceptor(MemoryCacheRequestInterceptor())
                addRequestInterceptor(EngineRequestInterceptor())

                addDecodeInterceptor(ResultCacheDecodeInterceptor())
                addDecodeInterceptor(TransformationDecodeInterceptor())
                addDecodeInterceptor(EngineDecodeInterceptor())
            },
            actual = commonComponents()
        )
    }
}
