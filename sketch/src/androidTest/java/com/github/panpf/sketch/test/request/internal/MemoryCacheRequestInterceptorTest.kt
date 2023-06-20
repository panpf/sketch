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
package com.github.panpf.sketch.test.request.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.drawable.BitmapDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.DataFrom.NETWORK
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.request.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.request.internal.RequestInterceptorChain
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MemoryCacheRequestInterceptorTest {

    @Test
    fun testIntercept() {
        val (context, sketch) = getTestContextAndNewSketch()
        val memoryCache = sketch.memoryCache

        val requestInterceptorList =
            listOf(MemoryCacheRequestInterceptor(), FakeRequestInterceptor())
        val executeRequest: (ImageRequest) -> ImageData = { request ->
            runBlocking(Dispatchers.Main) {
                RequestInterceptorChain(
                    sketch = sketch,
                    initialRequest = request,
                    request = request,
                    requestContext = request.toRequestContext(),
                    interceptors = requestInterceptorList,
                    index = 0,
                ).proceed(request)
            }.getOrThrow()
        }

        memoryCache.clear()
        Assert.assertEquals(0, memoryCache.size)

        /* DownloadRequest */
        executeRequest(DownloadRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            memoryCachePolicy(ENABLED)
        }).asOrThrow<DownloadData>()
        Assert.assertEquals(0, memoryCache.size)
        executeRequest(DownloadRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            memoryCachePolicy(ENABLED)
        }).asOrThrow<DownloadData>()
        Assert.assertEquals(0, memoryCache.size)

        /* LoadRequest */
        executeRequest(LoadRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            memoryCachePolicy(ENABLED)
        }).asOrThrow<LoadData>()
        Assert.assertEquals(0, memoryCache.size)
        executeRequest(LoadRequest(context, TestAssets.SAMPLE_JPEG_URI) {
            memoryCachePolicy(ENABLED)
        }).asOrThrow<LoadData>()
        Assert.assertEquals(0, memoryCache.size)

        /* DisplayRequest - ENABLED */
        val displayRequest = DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI)
        val countBitmapDrawable: SketchCountBitmapDrawable
        memoryCache.clear()
        Assert.assertEquals(0, memoryCache.size)
        executeRequest(displayRequest.newDisplayRequest {
            memoryCachePolicy(ENABLED)
        }).asOrThrow<DisplayData>().apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
            countBitmapDrawable = drawable.asOrThrow()
        }
        Assert.assertEquals(40000, memoryCache.size)

        executeRequest(displayRequest.newDisplayRequest {
            memoryCachePolicy(ENABLED)
        }).asOrThrow<DisplayData>().apply {
            Assert.assertEquals(DataFrom.MEMORY_CACHE, dataFrom)
        }
        Assert.assertEquals(40000, memoryCache.size)

        /* DisplayRequest - DISABLED */
        memoryCache.clear()
        Assert.assertEquals(0, memoryCache.size)
        executeRequest(displayRequest.newDisplayRequest {
            memoryCachePolicy(DISABLED)
        }).asOrThrow<DisplayData>().apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
            Assert.assertTrue(drawable !is SketchCountBitmapDrawable)
        }
        Assert.assertEquals(0, memoryCache.size)

        memoryCache.put(
            displayRequest.toRequestContext().cacheKey,
            MemoryCache.Value(
                countBitmapDrawable.countBitmap,
                imageUri = countBitmapDrawable.imageUri,
                requestKey = countBitmapDrawable.requestKey,
                requestCacheKey = countBitmapDrawable.requestCacheKey,
                imageInfo = countBitmapDrawable.imageInfo,
                transformedList = countBitmapDrawable.transformedList,
                extras = countBitmapDrawable.extras,
            )
        )
        Assert.assertEquals(40000, memoryCache.size)
        executeRequest(displayRequest.newDisplayRequest {
            memoryCachePolicy(DISABLED)
        }).asOrThrow<DisplayData>().apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }
        Assert.assertEquals(40000, memoryCache.size)

        /* DisplayRequest - READ_ONLY */
        memoryCache.clear()
        Assert.assertEquals(0, memoryCache.size)
        executeRequest(displayRequest.newDisplayRequest {
            memoryCachePolicy(READ_ONLY)
        }).asOrThrow<DisplayData>().apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
            Assert.assertTrue(drawable !is SketchCountBitmapDrawable)
        }
        Assert.assertEquals(0, memoryCache.size)

        memoryCache.put(
            displayRequest.toRequestContext().cacheKey,
            MemoryCache.Value(
                countBitmapDrawable.countBitmap,
                imageUri = countBitmapDrawable.imageUri,
                requestKey = countBitmapDrawable.requestKey,
                requestCacheKey = countBitmapDrawable.requestCacheKey,
                imageInfo = countBitmapDrawable.imageInfo,
                transformedList = countBitmapDrawable.transformedList,
                extras = countBitmapDrawable.extras,
            )
        )
        Assert.assertEquals(40000, memoryCache.size)
        executeRequest(displayRequest.newDisplayRequest {
            memoryCachePolicy(READ_ONLY)
        }).asOrThrow<DisplayData>().apply {
            Assert.assertEquals(DataFrom.MEMORY_CACHE, dataFrom)
            Assert.assertTrue(drawable is SketchCountBitmapDrawable)
        }
        Assert.assertEquals(40000, memoryCache.size)

        /* DisplayRequest - WRITE_ONLY */
        memoryCache.clear()
        Assert.assertEquals(0, memoryCache.size)
        executeRequest(displayRequest.newDisplayRequest {
            memoryCachePolicy(WRITE_ONLY)
        }).asOrThrow<DisplayData>().apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
            Assert.assertTrue(drawable !is SketchCountBitmapDrawable)
        }
        Assert.assertEquals(40000, memoryCache.size)

        executeRequest(displayRequest.newDisplayRequest {
            memoryCachePolicy(WRITE_ONLY)
        }).asOrThrow<DisplayData>().apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
            Assert.assertTrue(drawable !is SketchCountBitmapDrawable)
        }
        Assert.assertEquals(40000, memoryCache.size)

        /* Depth.MEMORY */
        memoryCache.clear()
        Assert.assertEquals(0, memoryCache.size)
        assertThrow(DepthException::class) {
            executeRequest(displayRequest.newDisplayRequest {
                memoryCachePolicy(ENABLED)
                depth(MEMORY)
            })
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = MemoryCacheRequestInterceptor()
        val element11 = MemoryCacheRequestInterceptor()
        val element2 = MemoryCacheRequestInterceptor()

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertEquals(element1, element2)
        Assert.assertEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertEquals(element1.hashCode(), element2.hashCode())
        Assert.assertEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testSortWeight() {
        MemoryCacheRequestInterceptor().apply {
            Assert.assertEquals(90, sortWeight)
        }
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "MemoryCacheRequestInterceptor(sortWeight=90)",
            MemoryCacheRequestInterceptor().toString()
        )
    }

    class FakeRequestInterceptor : RequestInterceptor {

        override val key: String? = null
        override val sortWeight: Int = 0

        override suspend fun intercept(chain: Chain): Result<ImageData> = kotlin.runCatching {
            when (chain.request) {
                is DisplayRequest -> {
                    val bitmap = Bitmap.createBitmap(100, 100, ARGB_8888)
                    val imageInfo = ImageInfo(100, 100, "image/png", 0)
                    val drawable = BitmapDrawable(chain.sketch.context.resources, bitmap)
                    DisplayData(drawable, imageInfo, DataFrom.LOCAL, null, null)
                }

                is LoadRequest -> {
                    val bitmap = Bitmap.createBitmap(100, 100, ARGB_8888)
                    val imageInfo = ImageInfo(100, 100, "image/jpeg", 0)
                    LoadData(bitmap, imageInfo, DataFrom.LOCAL, null, null)
                }

                is DownloadRequest -> {
                    DownloadData(byteArrayOf(), NETWORK)
                }

                else -> {
                    throw UnsupportedOperationException("Unsupported ImageRequest: ${chain.request::class.java}")
                }
            }
        }
    }
}