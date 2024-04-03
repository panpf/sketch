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
package com.github.panpf.sketch.core.test

import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.cache.internal.LruDiskCache
import com.github.panpf.sketch.cache.internal.LruMemoryCache
import com.github.panpf.sketch.cache.internal.ResultCacheDecodeInterceptor
import com.github.panpf.sketch.util.defaultMemoryCacheBytes
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
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.GlobalTargetLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.images.AssetImages
import com.github.panpf.sketch.test.utils.DelayTransformation
import com.github.panpf.sketch.test.utils.ListenerSupervisor
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.TestDecodeInterceptor
import com.github.panpf.sketch.test.utils.TestDecoder
import com.github.panpf.sketch.test.utils.TestFetcher
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.TestRequestInterceptor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.transform.internal.TransformationDecodeInterceptor
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Logger.Level.DEBUG
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

    private val errorUri = newAssetUri("error.jpeg")

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
            diskCache(
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
            httpStack(TestHttpStack(context1))
            build().apply {
                Assert.assertEquals(TestHttpStack(context1), httpStack)
                Assert.assertNotEquals(HurlStack.Builder().build(), httpStack)
            }

            build().apply {
                Assert.assertEquals(
                    listOf(
                        MemoryCacheRequestInterceptor(),
                        EngineRequestInterceptor()
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
        val (context, sketch) = getTestContextAndNewSketch()

        /* success */
        val listenerSupervisor1 = ListenerSupervisor()
        val request1 = ImageRequest(context, AssetImages.jpeg.uri) {
            listener(listenerSupervisor1)
        }
        val result1 = runBlocking {
            sketch.enqueue(request1).job.await()
        }
        Assert.assertTrue(result1 is ImageResult.Success)
        Assert.assertEquals(listOf("onStart", "onSuccess"), listenerSupervisor1.callbackActionList)

        /* error */
        val listenerSupervisor2 = ListenerSupervisor()
        val request2 = ImageRequest(context, errorUri) {
            listener(listenerSupervisor2)
        }
        val result2 = runBlocking {
            sketch.enqueue(request2).job.await()
        }
        Assert.assertTrue(result2 is ImageResult.Error)
        Assert.assertEquals(listOf("onStart", "onError"), listenerSupervisor2.callbackActionList)

        /* cancel */
        var disposable3: Disposable? = null
        val listenerSupervisor3 = ListenerSupervisor()
        val request3 = ImageRequest(context, AssetImages.jpeg.uri) {
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
    fun testExecute() {
        val (context, sketch) = getTestContextAndNewSketch()

        /* success */
        val listenerSupervisor1 = ListenerSupervisor()
        val request1 = ImageRequest(context, AssetImages.jpeg.uri) {
            listener(listenerSupervisor1)
        }
        val result1 = runBlocking {
            sketch.execute(request1)
        }
        Assert.assertTrue(result1 is ImageResult.Success)
        Assert.assertEquals(listOf("onStart", "onSuccess"), listenerSupervisor1.callbackActionList)

        /* error */
        val listenerSupervisor2 = ListenerSupervisor()
        val request2 = ImageRequest(context, errorUri) {
            listener(listenerSupervisor2)
        }
        val result2 = runBlocking {
            sketch.execute(request2)
        }
        Assert.assertTrue(result2 is ImageResult.Error)
        Assert.assertEquals(listOf("onStart", "onError"), listenerSupervisor2.callbackActionList)

        /* cancel */
        var deferred3: Deferred<ImageResult>? = null
        val listenerSupervisor3 = ListenerSupervisor {
            deferred3?.cancel()
        }
        val request3 = ImageRequest(context, AssetImages.jpeg.uri) {
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
        val listenerSupervisor4 = ListenerSupervisor()
        val request4 = ImageRequest(imageView, AssetImages.jpeg.uri) {
            listener(listenerSupervisor4)
            lifecycle(GlobalTargetLifecycle)
        }
        val result4 = runBlocking {
            try {
                sketch.execute(request4)
            } catch (e: Exception) {
                ImageResult.Error(request4, null, e)
            }
        }
        Assert.assertTrue(result4 is ImageResult.Error)
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