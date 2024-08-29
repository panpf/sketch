package com.github.panpf.sketch.core.android.test

import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch.Builder
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.internal.LruDiskCache
import com.github.panpf.sketch.cache.internal.LruMemoryCache
import com.github.panpf.sketch.cache.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.cache.internal.ResultCacheDecodeInterceptor
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.decode.internal.DrawableDecoder
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.ContentUriFetcher
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.ImageResult.Error
import com.github.panpf.sketch.request.ImageResult.Success
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.DelayTransformation
import com.github.panpf.sketch.test.utils.ListenerSupervisor
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.transform.internal.TransformationDecodeInterceptor
import com.github.panpf.sketch.util.Logger
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okio.FileSystem
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SketchTest {

    private val errorUri = newAssetUri("error.jpeg")

    @Test
    fun testBuilder() {
        // TODO networkParallelismLimited
        // TODO decodeParallelismLimited
        val activity = TestActivity::class.launchActivity().getActivitySync()
        Builder(activity).build().apply {
            Assert.assertNotEquals(activity, context)
            Assert.assertEquals(activity.applicationContext, context)
        }

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
                Assert.assertEquals(Logger.Level.Info, logger.level)
                Assert.assertFalse(logger.toString().contains(fakePipeline.toString()))
            }

            logger()
            build().apply {
                Assert.assertEquals(Logger.Level.Info, logger.level)
                Assert.assertFalse(logger.toString().contains(fakePipeline.toString()))
            }

            logger(level = Logger.Level.Verbose)
            build().apply {
                Assert.assertEquals(Logger.Level.Verbose, logger.level)
                Assert.assertFalse(logger.toString().contains(fakePipeline.toString()))
            }

            logger(level = Logger.Level.Verbose, pipeline = fakePipeline)
            build().apply {
                Assert.assertEquals(
                    Logger(level = Logger.Level.Verbose, pipeline = fakePipeline),
                    logger
                )
                Assert.assertTrue(logger.toString().contains(fakePipeline.toString()))
            }

            val defaultMemoryCache = MemoryCache.Builder(context).build()
            build().apply {
                Assert.assertEquals(defaultMemoryCache, memoryCache)
            }
            val littleMemoryCacheBytes = defaultMemoryCache.maxSize / 2
            memoryCache(LruMemoryCache(littleMemoryCacheBytes))
            build().apply {
                Assert.assertEquals(LruMemoryCache(littleMemoryCacheBytes), memoryCache)
                Assert.assertNotEquals(defaultMemoryCache, memoryCache)
            }

            val defaultDownloadCache =
                DiskCache.DownloadBuilder(context, FileSystem.SYSTEM).build()
            val defaultResultCache =
                DiskCache.ResultBuilder(context, FileSystem.SYSTEM).build()
            build().apply {
                Assert.assertEquals(defaultDownloadCache, downloadCache)
                Assert.assertEquals(defaultResultCache, resultCache)
            }
            val littleDownloadDiskCache = LruDiskCache(
                context = context,
                fileSystem = FileSystem.SYSTEM,
                maxSize = 50 * 1024 * 1024,
                directory = defaultDownloadCache.directory,
                appVersion = 10,
                internalVersion = 0
            )
            val littleResultDiskCache = LruDiskCache(
                context = context,
                fileSystem = FileSystem.SYSTEM,
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
                Assert.assertEquals(littleDownloadDiskCache, downloadCache)
                Assert.assertEquals(littleResultDiskCache, resultCache)
            }

            build().apply {
                Assert.assertEquals(
                    ComponentRegistry.Builder().apply {
                        addFetcher(ContentUriFetcher.Factory())
                        addFetcher(ResourceUriFetcher.Factory())
                        addFetcher(AssetUriFetcher.Factory())
                        addFetcher(HttpUriFetcher.Factory())
                        addFetcher(Base64UriFetcher.Factory())
                        addFetcher(FileUriFetcher.Factory())
                        addDecoder(DrawableDecoder.Factory())
                        addDecoder(BitmapFactoryDecoder.Factory())
                        addRequestInterceptor(MemoryCacheRequestInterceptor())
                        addRequestInterceptor(EngineRequestInterceptor())
                        addDecodeInterceptor(ResultCacheDecodeInterceptor())
                        addDecodeInterceptor(TransformationDecodeInterceptor())
                        addDecodeInterceptor(EngineDecodeInterceptor())
                    }.build(),
                    components.registry
                )
            }
            components {
                addFetcher(TestFetcher.Factory())
                addDecoder(TestDecoder.Factory())
            }
            build().apply {
                Assert.assertEquals(
                    ComponentRegistry.Builder().apply {
                        addFetcher(TestFetcher.Factory())
                        addDecoder(TestDecoder.Factory())
                        addFetcher(ContentUriFetcher.Factory())
                        addFetcher(ResourceUriFetcher.Factory())
                        addFetcher(AssetUriFetcher.Factory())
                        addFetcher(HttpUriFetcher.Factory())
                        addFetcher(Base64UriFetcher.Factory())
                        addFetcher(FileUriFetcher.Factory())
                        addDecoder(DrawableDecoder.Factory())
                        addDecoder(BitmapFactoryDecoder.Factory())
                        addRequestInterceptor(MemoryCacheRequestInterceptor())
                        addRequestInterceptor(EngineRequestInterceptor())
                        addDecodeInterceptor(ResultCacheDecodeInterceptor())
                        addDecodeInterceptor(TransformationDecodeInterceptor())
                        addDecodeInterceptor(EngineDecodeInterceptor())
                    }.build(),
                    components.registry
                )
                Assert.assertNotEquals(
                    ComponentRegistry.Builder().apply {
                        addFetcher(HttpUriFetcher.Factory())
                        addFetcher(FileUriFetcher.Factory())
                        addFetcher(ContentUriFetcher.Factory())
                        addFetcher(ResourceUriFetcher.Factory())
                        addFetcher(AssetUriFetcher.Factory())
                        addFetcher(Base64UriFetcher.Factory())
                        addDecoder(DrawableDecoder.Factory())
                        addDecoder(BitmapFactoryDecoder.Factory())
                        addRequestInterceptor(MemoryCacheRequestInterceptor())
                        addRequestInterceptor(EngineRequestInterceptor())
                        addDecodeInterceptor(ResultCacheDecodeInterceptor())
                        addDecodeInterceptor(TransformationDecodeInterceptor())
                        addDecodeInterceptor(EngineDecodeInterceptor())
                    }.build(),
                    components.registry
                )
            }

            build().apply {
                Assert.assertEquals(HurlStack.Builder().build(), httpStack)
            }
            httpStack(TestHttpStack(context))
            build().apply {
                Assert.assertEquals(TestHttpStack(context), httpStack)
                Assert.assertNotEquals(HurlStack.Builder().build(), httpStack)
            }

            build().apply {
                Assert.assertEquals(
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
                Assert.assertEquals(
                    listOf(
                        TestRequestInterceptor(),
                        MemoryCacheRequestInterceptor(),
                        EngineRequestInterceptor()
                    ),
                    components.getRequestInterceptorList(ImageRequest(context, ""))
                )
                Assert.assertNotEquals(
                    listOf(
                        MemoryCacheRequestInterceptor(),
                        EngineRequestInterceptor()
                    ),
                    components.getRequestInterceptorList(ImageRequest(context, ""))
                )
            }

            build().apply {
                Assert.assertEquals(
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
                Assert.assertEquals(
                    listOf(
                        TestDecodeInterceptor(),
                        ResultCacheDecodeInterceptor(),
                        TransformationDecodeInterceptor(),
                        EngineDecodeInterceptor()
                    ),
                    components.getDecodeInterceptorList(ImageRequest(context, ""))
                )
                Assert.assertNotEquals(
                    listOf(
                        ResultCacheDecodeInterceptor(),
                        TransformationDecodeInterceptor(),
                        EngineDecodeInterceptor()
                    ),
                    components.getDecodeInterceptorList(ImageRequest(context, ""))
                )
            }

            build().apply {
                Assert.assertNull(globalImageOptions)
            }
            globalImageOptions(ImageOptions())
            build().apply {
                Assert.assertEquals(ImageOptions(), globalImageOptions)
                Assert.assertNotNull(globalImageOptions)
            }
        }
    }

    @Test
    fun testEnqueue() {
        val (context, sketch) = getTestContextAndSketch()

        /* success */
        val listenerSupervisor1 = ListenerSupervisor()
        val request1 = ImageRequest(context, ResourceImages.jpeg.uri) {
            registerListener(listenerSupervisor1)
        }
        val result1 = runBlocking {
            sketch.enqueue(request1).job.await()
        }
        Assert.assertTrue(result1 is Success)
        Assert.assertEquals(listOf("onStart", "onSuccess"), listenerSupervisor1.callbackActionList)

        /* error */
        val listenerSupervisor2 = ListenerSupervisor()
        val request2 = ImageRequest(context, errorUri) {
            registerListener(listenerSupervisor2)
        }
        val result2 = runBlocking {
            sketch.enqueue(request2).job.await()
        }
        Assert.assertTrue(result2 is Error)
        Assert.assertEquals(listOf("onStart", "onError"), listenerSupervisor2.callbackActionList)

        /* cancel */
        var disposable3: Disposable? = null
        val listenerSupervisor3 = ListenerSupervisor()
        val request3 = ImageRequest(context, ResourceImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            // Make the execution slower, cancellation can take effect
            addTransformations(DelayTransformation {
                disposable3?.job?.cancel()
            })
            registerListener(listenerSupervisor3)
        }
        runBlocking {
            disposable3 = sketch.enqueue(request3)
            disposable3?.job?.join()
        }
        Assert.assertEquals(listOf("onStart", "onCancel"), listenerSupervisor3.callbackActionList)
    }

    @Test
    fun testExecute() {
        val (context, sketch) = getTestContextAndSketch()

        /* success */
        val listenerSupervisor1 = ListenerSupervisor()
        val request1 = ImageRequest(context, ResourceImages.jpeg.uri) {
            registerListener(listenerSupervisor1)
        }
        val result1 = runBlocking {
            sketch.execute(request1)
        }
        Assert.assertTrue(result1 is Success)
        Assert.assertEquals(listOf("onStart", "onSuccess"), listenerSupervisor1.callbackActionList)

        /* error */
        val listenerSupervisor2 = ListenerSupervisor()
        val request2 = ImageRequest(context, errorUri) {
            registerListener(listenerSupervisor2)
        }
        val result2 = runBlocking {
            sketch.execute(request2)
        }
        Assert.assertTrue(result2 is Error)
        Assert.assertEquals(listOf("onStart", "onError"), listenerSupervisor2.callbackActionList)

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
        runBlocking {
            deferred3 = async {
                sketch.execute(request3)
            }
            deferred3?.join()
        }
        Assert.assertEquals(listOf("onStart", "onCancel"), listenerSupervisor3.callbackActionList)

        /* ViewTarget */
        val imageView = ImageView(context)
        val listenerSupervisor4 = ListenerSupervisor()
        val request4 = ImageRequest(imageView, ResourceImages.jpeg.uri) {
            registerListener(listenerSupervisor4)
            lifecycle(GlobalLifecycle)
        }
        val result4 = runBlocking {
            try {
                sketch.execute(request4)
            } catch (e: Exception) {
                Error(request4, null, e)
            }
        }
        Assert.assertTrue(result4 is Error)
        Assert.assertEquals(listOf<String>(), listenerSupervisor4.callbackActionList)
    }

    @Test
    fun testShutdown() {
        val sketch = newSketch()
        sketch.shutdown()
        sketch.shutdown()
    }

    @Test
    fun testSystemCallbacks() {
        val sketch = newSketch()
        Assert.assertNotNull(sketch.systemCallbacks)
    }
}
