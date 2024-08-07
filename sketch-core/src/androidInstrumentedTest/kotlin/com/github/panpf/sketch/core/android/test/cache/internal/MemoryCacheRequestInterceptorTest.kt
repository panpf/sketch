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
package com.github.panpf.sketch.core.android.test.cache.internal

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.AndroidBitmapImage
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.cache.AndroidBitmapImageValue
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.cache.internal.MemoryCacheRequestInterceptor
import com.github.panpf.sketch.cache.newCacheValueExtras
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.request.internal.RequestInterceptorChain
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestCountTarget
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MemoryCacheRequestInterceptorTest {

    @Test
    fun testIntercept() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val memoryCache = sketch.memoryCache

        val requestInterceptorList =
            listOf(MemoryCacheRequestInterceptor(), FakeRequestInterceptor())
        val executeRequest: (ImageRequest) -> ImageData = { request ->
            runBlocking(Dispatchers.Main) {
                RequestInterceptorChain(
                    sketch = sketch,
                    initialRequest = request,
                    request = request,
                    requestContext = request.toRequestContext(sketch),
                    interceptors = requestInterceptorList,
                    index = 0,
                ).proceed(request)
            }.getOrThrow()
        }

        memoryCache.clear()
        Assert.assertEquals(0, memoryCache.size)

        /* ImageRequest */
        executeRequest(ImageRequest(context, ResourceImages.jpeg.uri) {
            memoryCachePolicy(ENABLED)
        }).asOrThrow<ImageData>()
        Assert.assertEquals(40000, memoryCache.size)

        memoryCache.clear()
        Assert.assertEquals(0, memoryCache.size)

        /* ImageRequest - ENABLED */
        val cacheImage: AndroidBitmapImage
        val imageData: ImageData
        val request = ImageRequest(context, ResourceImages.jpeg.uri) {
            target(TestCountTarget())
        }
        executeRequest(request.newRequest {
            memoryCachePolicy(ENABLED)
        }).asOrThrow<ImageData>().apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
            imageData = this
            cacheImage = image.asOrThrow()
        }
        Assert.assertEquals(40000, memoryCache.size)

        executeRequest(request.newRequest {
            memoryCachePolicy(ENABLED)
        }).asOrThrow<ImageData>().apply {
            Assert.assertEquals(DataFrom.MEMORY_CACHE, dataFrom)
        }
        Assert.assertEquals(40000, memoryCache.size)

        memoryCache.clear()
        Assert.assertEquals(0, memoryCache.size)

        /* ImageRequest - DISABLED */
        executeRequest(request.newRequest {
            memoryCachePolicy(DISABLED)
        }).asOrThrow<ImageData>().apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
            Assert.assertTrue(image is AndroidBitmapImage)
        }
        Assert.assertEquals(0, memoryCache.size)

        memoryCache.put(
            request.toRequestContext(sketch).cacheKey,
            AndroidBitmapImageValue(
                cacheImage,
                newCacheValueExtras(
                    imageInfo = imageData.imageInfo,
                    resize = imageData.resize,
                    transformeds = imageData.transformeds,
                    extras = imageData.extras,
                )
            )
        )
        Assert.assertEquals(40000, memoryCache.size)
        executeRequest(request.newRequest {
            memoryCachePolicy(DISABLED)
        }).asOrThrow<ImageData>().apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
        }
        Assert.assertEquals(40000, memoryCache.size)

        memoryCache.clear()
        Assert.assertEquals(0, memoryCache.size)

        /* ImageRequest - READ_ONLY */
        executeRequest(request.newRequest {
            memoryCachePolicy(READ_ONLY)
        }).asOrThrow<ImageData>().apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
            Assert.assertTrue(image is AndroidBitmapImage)
        }
        Assert.assertEquals(0, memoryCache.size)

        memoryCache.put(
            request.toRequestContext(sketch).cacheKey,
            AndroidBitmapImageValue(
                cacheImage,
                newCacheValueExtras(
                    imageInfo = imageData.imageInfo,
                    resize = imageData.resize,
                    transformeds = imageData.transformeds,
                    extras = imageData.extras,
                )
            )
        )
        Assert.assertEquals(40000, memoryCache.size)
        executeRequest(request.newRequest {
            memoryCachePolicy(READ_ONLY)
        }).asOrThrow<ImageData>().apply {
            Assert.assertEquals(DataFrom.MEMORY_CACHE, dataFrom)
            Assert.assertTrue(image is AndroidBitmapImage)
        }
        Assert.assertEquals(40000, memoryCache.size)

        memoryCache.clear()
        Assert.assertEquals(0, memoryCache.size)

        /* ImageRequest - WRITE_ONLY */
        executeRequest(request.newRequest {
            memoryCachePolicy(WRITE_ONLY)
        }).asOrThrow<ImageData>().apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
            Assert.assertTrue(image is AndroidBitmapImage)
        }
        Assert.assertEquals(40000, memoryCache.size)

        executeRequest(request.newRequest {
            memoryCachePolicy(WRITE_ONLY)
        }).asOrThrow<ImageData>().apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)
            Assert.assertTrue(image is AndroidBitmapImage)
        }
        Assert.assertEquals(40000, memoryCache.size)

        memoryCache.clear()
        Assert.assertEquals(0, memoryCache.size)

        /* Depth.MEMORY */
        assertThrow(DepthException::class) {
            executeRequest(request.newRequest {
                memoryCachePolicy(ENABLED)
                depth(MEMORY)
            })
        }
    }

    // TODO test repeat block

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
            val bitmap = Bitmap.createBitmap(100, 100, ARGB_8888)
            val imageInfo = ImageInfo(100, 100, "image/png")
            ImageData(
                bitmap.asSketchImage(),
                imageInfo = imageInfo,
                resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
                dataFrom = DataFrom.LOCAL,
                transformeds = null,
                extras = null
            )
        }
    }
}