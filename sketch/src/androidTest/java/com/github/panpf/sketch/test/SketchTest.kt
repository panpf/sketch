package com.github.panpf.sketch.test

import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.cache.internal.LruDiskCache
import com.github.panpf.sketch.cache.internal.LruMemoryCache
import com.github.panpf.sketch.cache.internal.defaultMemoryCacheBytes
import com.github.panpf.sketch.decode.internal.BitmapEngineDecodeInterceptor
import com.github.panpf.sketch.decode.internal.BitmapResultCacheDecodeInterceptor
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
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.test.utils.DelayTransformation
import com.github.panpf.sketch.test.utils.DisplayListenerSupervisor
import com.github.panpf.sketch.test.utils.DownloadListenerSupervisor
import com.github.panpf.sketch.test.utils.LoadListenerSupervisor
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.TestBitmapDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestBitmapDecoder
import com.github.panpf.sketch.test.utils.TestDrawableDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDrawableDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.transform.internal.BitmapTransformationDecodeInterceptor
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Logger.Level.DEBUG
import com.github.panpf.sketch.util.UnknownException
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.roundToLong

@RunWith(AndroidJUnit4::class)
class SketchTest {

    @Test
    fun testBuilder() {
        val context1 = getTestContext()

        val activity = TestActivity::class.launchActivity().getActivitySync()
        Sketch.Builder(activity).build().apply {
            Assert.assertNotEquals(activity, context)
            Assert.assertEquals(activity.applicationContext, context)
        }

        Sketch.Builder(context1).apply {
            build().apply {
                Assert.assertEquals(Logger(), logger)
            }
            logger(Logger(DEBUG))
            build().apply {
                Assert.assertEquals(Logger(DEBUG), logger)
                Assert.assertNotEquals(Logger(), logger)
            }

            build().apply {
                Assert.assertEquals(
                    LruMemoryCache((context1.defaultMemoryCacheBytes() * 0.66f).roundToLong()),
                    memoryCache
                )
            }
            memoryCache(LruMemoryCache((context1.defaultMemoryCacheBytes() * 0.5f).roundToLong()))
            build().apply {
                Assert.assertEquals(
                    LruMemoryCache((context1.defaultMemoryCacheBytes() * 0.5f).roundToLong()),
                    memoryCache
                )
                Assert.assertNotEquals(
                    LruMemoryCache((context1.defaultMemoryCacheBytes() * 0.66f).roundToLong()),
                    memoryCache
                )
            }

            build().apply {
                Assert.assertEquals(
                    LruBitmapPool((context1.defaultMemoryCacheBytes() * 0.33f).roundToLong()),
                    bitmapPool
                )
            }
            bitmapPool(LruBitmapPool((context1.defaultMemoryCacheBytes() * 0.5f).roundToLong()))
            build().apply {
                Assert.assertEquals(
                    LruBitmapPool((context1.defaultMemoryCacheBytes() * 0.5f).roundToLong()),
                    bitmapPool
                )
                Assert.assertNotEquals(
                    LruBitmapPool((context1.defaultMemoryCacheBytes() * 0.33f).roundToLong()),
                    bitmapPool
                )
            }

            build().apply {
                Assert.assertEquals(
                    LruDiskCache.ForDownloadBuilder(context1).build(),
                    downloadCache
                )
            }
            downloadCache(
                LruDiskCache.ForDownloadBuilder(context1).maxSize(maxSize = 250 * 1024 * 1024)
                    .build()
            )
            build().apply {
                Assert.assertEquals(
                    LruDiskCache.ForDownloadBuilder(context1)
                        .maxSize(maxSize = 250 * 1024 * 1024)
                        .build(),
                    downloadCache
                )
                Assert.assertNotEquals(
                    LruDiskCache.ForDownloadBuilder(context1).build(),
                    downloadCache
                )
            }

            build().apply {
                Assert.assertEquals(
                    LruDiskCache.ForResultBuilder(context1).build(),
                    resultCache
                )
            }
            resultCache(
                LruDiskCache.ForResultBuilder(context1).maxSize(maxSize = 250 * 1024 * 1024)
                    .build()
            )
            build().apply {
                Assert.assertEquals(
                    LruDiskCache.ForResultBuilder(context1)
                        .maxSize(maxSize = 250 * 1024 * 1024)
                        .build(),
                    resultCache
                )
                Assert.assertNotEquals(
                    LruDiskCache.ForResultBuilder(context1).build(),
                    resultCache
                )
            }

            build().apply {
                Assert.assertEquals(
                    ComponentRegistry.Builder().apply {
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
                        addBitmapDecodeInterceptor(BitmapEngineDecodeInterceptor())
                        addDrawableDecodeInterceptor(DrawableEngineDecodeInterceptor())
                    }.build(),
                    components.registry
                )
            }
            components {
                addFetcher(TestFetcher.Factory())
                addBitmapDecoder(TestBitmapDecoder.Factory())
                addDrawableDecoder(TestDrawableDecoder.Factory())
            }
            build().apply {
                Assert.assertEquals(
                    ComponentRegistry.Builder().apply {
                        addFetcher(TestFetcher.Factory())
                        addBitmapDecoder(TestBitmapDecoder.Factory())
                        addDrawableDecoder(TestDrawableDecoder.Factory())
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
                        addBitmapDecodeInterceptor(BitmapEngineDecodeInterceptor())
                        addDrawableDecodeInterceptor(DrawableEngineDecodeInterceptor())
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
                        addBitmapDecoder(XmlDrawableBitmapDecoder.Factory())
                        addBitmapDecoder(DefaultBitmapDecoder.Factory())
                        addDrawableDecoder(DefaultDrawableDecoder.Factory())
                        addRequestInterceptor(EngineRequestInterceptor())
                        addBitmapDecodeInterceptor(BitmapResultCacheDecodeInterceptor())
                        addBitmapDecodeInterceptor(BitmapTransformationDecodeInterceptor())
                        addBitmapDecodeInterceptor(BitmapEngineDecodeInterceptor())
                        addDrawableDecodeInterceptor(DrawableEngineDecodeInterceptor())
                    }.build(),
                    components.registry
                )
            }

            build().apply {
                Assert.assertEquals(HurlStack.Builder().build(), httpStack)
            }
            httpStack(TestHttpStack(context1))
            build().apply {
                Assert.assertEquals(TestHttpStack(context1), httpStack)
                Assert.assertNotEquals(HurlStack.Builder().build(), httpStack)
            }

            build().apply {
                Assert.assertEquals(
                    listOf(MemoryCacheRequestInterceptor(), EngineRequestInterceptor()),
                    components.requestInterceptorList
                )
            }
            components {
                addRequestInterceptor(TestRequestInterceptor())
            }
            build().apply {
                Assert.assertEquals(
                    listOf(TestRequestInterceptor(), MemoryCacheRequestInterceptor(), EngineRequestInterceptor()),
                    components.requestInterceptorList
                )
                Assert.assertNotEquals(
                    listOf(MemoryCacheRequestInterceptor(), EngineRequestInterceptor()),
                    components.requestInterceptorList
                )
            }

            build().apply {
                Assert.assertEquals(
                    listOf(
                        BitmapResultCacheDecodeInterceptor(),
                        BitmapTransformationDecodeInterceptor(),
                        BitmapEngineDecodeInterceptor()
                    ),
                    components.bitmapDecodeInterceptorList
                )
            }
            components {
                addBitmapDecodeInterceptor(TestBitmapDecodeInterceptor())
            }
            build().apply {
                Assert.assertEquals(
                    listOf(
                        TestBitmapDecodeInterceptor(),
                        BitmapResultCacheDecodeInterceptor(),
                        BitmapTransformationDecodeInterceptor(),
                        BitmapEngineDecodeInterceptor()
                    ),
                    components.bitmapDecodeInterceptorList
                )
                Assert.assertNotEquals(
                    listOf(
                        BitmapResultCacheDecodeInterceptor(),
                        BitmapTransformationDecodeInterceptor(),
                        BitmapEngineDecodeInterceptor()
                    ),
                    components.bitmapDecodeInterceptorList
                )
            }

            build().apply {
                Assert.assertEquals(
                    listOf(DrawableEngineDecodeInterceptor()),
                    components.drawableDecodeInterceptorList
                )
            }
            components {
                addDrawableDecodeInterceptor(TestDrawableDecodeInterceptor())
            }
            build().apply {
                Assert.assertEquals(
                    listOf(TestDrawableDecodeInterceptor(), DrawableEngineDecodeInterceptor()),
                    components.drawableDecodeInterceptorList
                )
                Assert.assertNotEquals(
                    listOf(DrawableEngineDecodeInterceptor()),
                    components.drawableDecodeInterceptorList
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
    fun testDisplayEnqueue() {
        val (context, sketch) = getTestContextAndNewSketch()

        /* success */
        val listenerSupervisor1 = DisplayListenerSupervisor()
        val request1 = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            listener(listenerSupervisor1)
        }
        val result1 = runBlocking {
            sketch.enqueue(request1).job.await()
        }
        Assert.assertTrue(result1 is DisplayResult.Success)
        Assert.assertEquals(listOf("onStart", "onSuccess"), listenerSupervisor1.callbackActionList)

        /* error */
        val listenerSupervisor2 = DisplayListenerSupervisor()
        val request2 = DisplayRequest(context, TestAssets.ERROR_URI) {
            listener(listenerSupervisor2)
        }
        val result2 = runBlocking {
            sketch.enqueue(request2).job.await()
        }
        Assert.assertTrue(result2 is DisplayResult.Error)
        Assert.assertEquals(listOf("onStart", "onError"), listenerSupervisor2.callbackActionList)

        /* cancel */
        var disposable3: Disposable<DisplayResult>? = null
        val listenerSupervisor3 = DisplayListenerSupervisor()
        val request3 = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            // Make the execution slower, cancellation can take effect
            addTransformations(DelayTransformation {
                disposable3?.job?.cancel()
            })
            listener(listenerSupervisor3)
        }
        runBlocking {
            disposable3 = sketch.enqueue(request3)
            disposable3?.job?.join()
        }
        Assert.assertEquals(listOf("onStart", "onCancel"), listenerSupervisor3.callbackActionList)
    }

    @Test
    fun testDisplayExecute() {
        val (context, sketch) = getTestContextAndNewSketch()

        /* success */
        val listenerSupervisor1 = DisplayListenerSupervisor()
        val request1 = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            listener(listenerSupervisor1)
        }
        val result1 = runBlocking {
            sketch.execute(request1)
        }
        Assert.assertTrue(result1 is DisplayResult.Success)
        Assert.assertEquals(listOf("onStart", "onSuccess"), listenerSupervisor1.callbackActionList)

        /* error */
        val listenerSupervisor2 = DisplayListenerSupervisor()
        val request2 = DisplayRequest(context, TestAssets.ERROR_URI) {
            listener(listenerSupervisor2)
        }
        val result2 = runBlocking {
            sketch.execute(request2)
        }
        Assert.assertTrue(result2 is DisplayResult.Error)
        Assert.assertEquals(listOf("onStart", "onError"), listenerSupervisor2.callbackActionList)

        /* cancel */
        var deferred3: Deferred<DisplayResult>? = null
        val listenerSupervisor3 = DisplayListenerSupervisor {
            deferred3?.cancel()
        }
        val request3 = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            listener(listenerSupervisor3)
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
        val listenerSupervisor4 = DisplayListenerSupervisor()
        val request4 = DisplayRequest(imageView, TestAssets.SAMPLE_JPEG_URI) {
            listener(listenerSupervisor4)
        }
        val result4 = runBlocking {
            try {
                sketch.execute(request4)
            } catch (e: Exception) {
                DisplayResult.Error(request4, null, UnknownException("", e))
            }
        }
        Assert.assertTrue(result4 is DisplayResult.Error)
        Assert.assertEquals(listOf<String>(), listenerSupervisor4.callbackActionList)
    }

    @Test
    fun testLoadEnqueue() {
        val (context, sketch) = getTestContextAndNewSketch()

        /* success */
        val listenerSupervisor1 = LoadListenerSupervisor()
        val request1 = LoadRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            listener(listenerSupervisor1)
        }
        val result1 = runBlocking {
            sketch.enqueue(request1).job.await()
        }
        Assert.assertTrue(result1 is LoadResult.Success)
        Assert.assertEquals(listOf("onStart", "onSuccess"), listenerSupervisor1.callbackActionList)

        /* error */
        val listenerSupervisor2 = LoadListenerSupervisor()
        val request2 = LoadRequest(context, TestAssets.ERROR_URI) {
            listener(listenerSupervisor2)
        }
        val result2 = runBlocking {
            sketch.enqueue(request2).job.await()
        }
        Assert.assertTrue(result2 is LoadResult.Error)
        Assert.assertEquals(listOf("onStart", "onError"), listenerSupervisor2.callbackActionList)

        /* cancel */
        var disposable3: Disposable<LoadResult>? = null
        val listenerSupervisor3 = LoadListenerSupervisor {
            disposable3?.job?.cancel()
        }
        val request3 = LoadRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            listener(listenerSupervisor3)
        }
        runBlocking {
            disposable3 = sketch.enqueue(request3)
            disposable3?.job?.join()
        }
        Assert.assertEquals(listOf("onStart", "onCancel"), listenerSupervisor3.callbackActionList)
    }

    @Test
    fun testLoadExecute() {
        val (context, sketch) = getTestContextAndNewSketch()

        /* success */
        val listenerSupervisor1 = LoadListenerSupervisor()
        val request1 = LoadRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            listener(listenerSupervisor1)
        }
        val result1 = runBlocking {
            sketch.execute(request1)
        }
        Assert.assertTrue(result1 is LoadResult.Success)
        Assert.assertEquals(listOf("onStart", "onSuccess"), listenerSupervisor1.callbackActionList)

        /* error */
        val listenerSupervisor2 = LoadListenerSupervisor()
        val request2 = LoadRequest(context, TestAssets.ERROR_URI) {
            listener(listenerSupervisor2)
        }
        val result2 = runBlocking {
            sketch.execute(request2)
        }
        Assert.assertTrue(result2 is LoadResult.Error)
        Assert.assertEquals(listOf("onStart", "onError"), listenerSupervisor2.callbackActionList)

        /* cancel */
        var deferred3: Deferred<LoadResult>? = null
        val listenerSupervisor3 = LoadListenerSupervisor {
            deferred3?.cancel()
        }
        val request3 = LoadRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            listener(listenerSupervisor3)
        }
        runBlocking {
            deferred3 = async {
                sketch.execute(request3)
            }
            deferred3?.join()
        }
        Assert.assertEquals(listOf("onStart", "onCancel"), listenerSupervisor3.callbackActionList)
    }

    @Test
    fun testDownloadEnqueue() {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        /* success */
        val listenerSupervisor1 = DownloadListenerSupervisor()
        val request1 = DownloadRequest(context, TestHttpStack.testImages.first().uriString) {
            listener(listenerSupervisor1)
        }
        val result1 = runBlocking {
            sketch.enqueue(request1).job.await()
        }
        Assert.assertTrue(result1 is DownloadResult.Success)
        Assert.assertEquals(
            listOf("onStart", "onSuccess"),
            listenerSupervisor1.callbackActionList
        )

        /* error */
        val listenerSupervisor2 = DownloadListenerSupervisor()
        val request2 = DownloadRequest(context, TestAssets.ERROR_URI) {
            listener(listenerSupervisor2)
        }
        val result2 = runBlocking {
            sketch.enqueue(request2).job.await()
        }
        Assert.assertTrue(result2 is DownloadResult.Error)
        Assert.assertEquals(
            listOf("onStart", "onError"),
            listenerSupervisor2.callbackActionList
        )

        /* cancel */
        val sketch1 = newSketch {
            httpStack(TestHttpStack(it, readDelayMillis = 500))
        }
        var disposable3: Disposable<DownloadResult>? = null
        val listenerSupervisor3 = DownloadListenerSupervisor {
            disposable3?.job?.cancel()
        }
        val request3 = DownloadRequest(context, TestHttpStack.testImages.first().uriString) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            listener(listenerSupervisor3)
        }
        runBlocking {
            disposable3 = sketch1.enqueue(request3)
            disposable3?.job?.join()
        }
        Assert.assertEquals(
            listOf("onStart", "onCancel"),
            listenerSupervisor3.callbackActionList
        )
    }

    @Test
    fun testDownloadExecute() {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        /* success */
        val listenerSupervisor1 = DownloadListenerSupervisor()
        val request1 = DownloadRequest(context, TestHttpStack.testImages.first().uriString) {
            listener(listenerSupervisor1)
        }
        val result1 = runBlocking {
            sketch.execute(request1)
        }
        Assert.assertTrue(result1 is DownloadResult.Success)
        Assert.assertEquals(
            listOf("onStart", "onSuccess"),
            listenerSupervisor1.callbackActionList
        )

        /* error */
        val listenerSupervisor2 = DownloadListenerSupervisor()
        val request2 = DownloadRequest(context, TestAssets.ERROR_URI) {
            listener(listenerSupervisor2)
        }
        val result2 = runBlocking {
            sketch.execute(request2)
        }
        Assert.assertTrue(result2 is DownloadResult.Error)
        Assert.assertEquals(
            listOf("onStart", "onError"),
            listenerSupervisor2.callbackActionList
        )

        /* cancel */
        val sketch1 = newSketch {
            httpStack(TestHttpStack(it, readDelayMillis = 500))
        }
        var deferred3: Deferred<DownloadResult>? = null
        val listenerSupervisor3 = DownloadListenerSupervisor {
            deferred3?.cancel()
        }
        val request3 = DownloadRequest(context, TestHttpStack.testImages.first().uriString) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            listener(listenerSupervisor3)
        }
        runBlocking {
            deferred3 = async {
                sketch1.execute(request3)
            }
            deferred3?.join()
        }
        Assert.assertEquals(
            listOf("onStart", "onCancel"),
            listenerSupervisor3.callbackActionList
        )
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