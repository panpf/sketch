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

package com.github.panpf.sketch.core.common.test.cache.internal

import com.github.panpf.sketch.Image
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
import com.github.panpf.sketch.test.utils.createCacheValue
import com.github.panpf.sketch.test.utils.createImage
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.asOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

class MemoryCacheRequestInterceptorTest {

    @Test
    fun testIntercept() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val memoryCache = sketch.memoryCache

        val requestInterceptorList =
            listOf(MemoryCacheRequestInterceptor(), FakeRequestInterceptor())
        val executeRequest: suspend (ImageRequest) -> ImageData = { request ->
            withContext(Dispatchers.Main) {
                RequestInterceptorChain(
                    requestContext = request.toRequestContext(sketch),
                    interceptors = requestInterceptorList,
                    index = 0,
                ).proceed(request)
            }.getOrThrow()
        }

        memoryCache.clear()
        assertEquals(expected = 0, actual = memoryCache.size)

        /* ImageRequest */
        executeRequest(ImageRequest(context, ResourceImages.jpeg.uri) {
            memoryCachePolicy(ENABLED)
        }).asOrThrow<ImageData>()
        assertEquals(expected = 40000, actual = memoryCache.size)

        memoryCache.clear()
        assertEquals(expected = 0, actual = memoryCache.size)

        /* ImageRequest - ENABLED */
        val cacheImage: Image
        val imageData: ImageData
        val request = ImageRequest(context, ResourceImages.jpeg.uri) {
            target(TestCountTarget())
        }
        executeRequest(request.newRequest {
            memoryCachePolicy(ENABLED)
        }).asOrThrow<ImageData>().apply {
            assertEquals(expected = DataFrom.LOCAL, actual = dataFrom)
            imageData = this
            cacheImage = image
        }
        assertEquals(expected = 40000, actual = memoryCache.size)

        executeRequest(request.newRequest {
            memoryCachePolicy(ENABLED)
        }).asOrThrow<ImageData>().apply {
            assertEquals(expected = DataFrom.MEMORY_CACHE, actual = dataFrom)
        }
        assertEquals(expected = 40000, actual = memoryCache.size)

        memoryCache.clear()
        assertEquals(expected = 0, actual = memoryCache.size)

        /* ImageRequest - DISABLED */
        executeRequest(request.newRequest {
            memoryCachePolicy(DISABLED)
        }).asOrThrow<ImageData>().apply {
            assertEquals(expected = DataFrom.LOCAL, actual = dataFrom)
        }
        assertEquals(expected = 0, actual = memoryCache.size)

        memoryCache.put(
            key = request.toRequestContext(sketch).cacheKey,
            value = createCacheValue(
                image = cacheImage,
                extras = newCacheValueExtras(
                    imageInfo = imageData.imageInfo,
                    resize = imageData.resize,
                    transformeds = imageData.transformeds,
                    extras = imageData.extras,
                )
            )
        )
        assertEquals(expected = 40000, actual = memoryCache.size)
        executeRequest(request.newRequest {
            memoryCachePolicy(DISABLED)
        }).asOrThrow<ImageData>().apply {
            assertEquals(expected = DataFrom.LOCAL, actual = dataFrom)
        }
        assertEquals(expected = 40000, actual = memoryCache.size)

        memoryCache.clear()
        assertEquals(expected = 0, actual = memoryCache.size)

        /* ImageRequest - READ_ONLY */
        executeRequest(request.newRequest {
            memoryCachePolicy(READ_ONLY)
        }).asOrThrow<ImageData>().apply {
            assertEquals(expected = DataFrom.LOCAL, actual = dataFrom)
        }
        assertEquals(expected = 0, actual = memoryCache.size)

        memoryCache.put(
            key = request.toRequestContext(sketch).cacheKey,
            value = createCacheValue(
                image = cacheImage,
                extras = newCacheValueExtras(
                    imageInfo = imageData.imageInfo,
                    resize = imageData.resize,
                    transformeds = imageData.transformeds,
                    extras = imageData.extras,
                )
            )
        )
        assertEquals(expected = 40000, actual = memoryCache.size)
        executeRequest(request.newRequest {
            memoryCachePolicy(READ_ONLY)
        }).asOrThrow<ImageData>().apply {
            assertEquals(expected = DataFrom.MEMORY_CACHE, actual = dataFrom)
        }
        assertEquals(expected = 40000, actual = memoryCache.size)

        memoryCache.clear()
        assertEquals(expected = 0, actual = memoryCache.size)

        /* ImageRequest - WRITE_ONLY */
        executeRequest(request.newRequest {
            memoryCachePolicy(WRITE_ONLY)
        }).asOrThrow<ImageData>().apply {
            assertEquals(expected = DataFrom.LOCAL, actual = dataFrom)
        }
        assertEquals(expected = 40000, actual = memoryCache.size)

        executeRequest(request.newRequest {
            memoryCachePolicy(WRITE_ONLY)
        }).asOrThrow<ImageData>().apply {
            assertEquals(expected = DataFrom.LOCAL, actual = dataFrom)
        }
        assertEquals(expected = 40000, actual = memoryCache.size)

        memoryCache.clear()
        assertEquals(expected = 0, actual = memoryCache.size)

        /* Depth.MEMORY */
        assertFailsWith(DepthException::class) {
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

        assertNotSame(illegal = element1, actual = element11)
        assertNotSame(illegal = element1, actual = element2)
        assertNotSame(illegal = element2, actual = element11)

        assertEquals(expected = element1, actual = element1)
        assertEquals(expected = element1, actual = element11)
        assertEquals(expected = element1, actual = element2)
        assertEquals(expected = element2, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element1.hashCode())
        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertEquals(expected = element1.hashCode(), actual = element2.hashCode())
        assertEquals(expected = element2.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testSortWeight() {
        assertEquals(
            expected = 90,
            actual = MemoryCacheRequestInterceptor().sortWeight
        )
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "MemoryCacheRequestInterceptor(sortWeight=90)",
            actual = MemoryCacheRequestInterceptor().toString()
        )
    }

    class FakeRequestInterceptor : RequestInterceptor {

        override val key: String? = null

        override val sortWeight: Int = 0

        override suspend fun intercept(chain: Chain): Result<ImageData> = kotlin.runCatching {
            val image = createImage(100, 100)
            val imageInfo = ImageInfo(100, 100, "image/png")
            ImageData(
                image = image,
                imageInfo = imageInfo,
                resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
                dataFrom = DataFrom.LOCAL,
                transformeds = null,
                extras = null
            )
        }
    }
}