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

package com.github.panpf.sketch.core.test.cache.internal

import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.cache.internal.ResultCacheDecodeInterceptor
import com.github.panpf.sketch.cache.resultCacheKey
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.internal.DecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.exist
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class ResultCacheDecodeInterceptorTest {

    @Test
    fun testIntercept() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val resultCache = sketch.resultCache

        val interceptors = listOf(
            ResultCacheDecodeInterceptor(),
            ExtrasTestDecodeInterceptor(),
            EngineDecodeInterceptor()
        )
        val executeRequest: suspend (ImageRequest) -> DecodeResult = { request ->
            DecodeInterceptorChain(
                sketch = sketch,
                request = request,
                requestContext = request.toRequestContext(sketch),
                fetchResult = null,
                interceptors = interceptors,
                index = 0
            ).proceed().getOrThrow()
        }

        val request = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(500, 500)
            precision(LESS_PIXELS)
            resultCachePolicy(ENABLED)
        }

        resultCache.clear()
        assertFalse(resultCache.exist(request.toRequestContext(sketch).resultCacheKey))
        executeRequest(request).also { result ->
            assertEquals(expected = 323, actual = result.image.width)
            assertEquals(expected = 484, actual = result.image.height)
            assertEquals(
                expected = "ImageInfo(size=1291x1936, mimeType='image/jpeg')",
                actual = result.imageInfo.toString()
            )
            assertEquals(
                expected = DataFrom.LOCAL,
                actual = result.dataFrom
            )
            assertEquals(
                expected = "InSampledTransformed(4)",
                actual = result.transformeds?.joinToString()
            )
            assertEquals(
                expected = mapOf("key" to "hasExtras"),
                actual = result.extras
            )
        }

        assertTrue(resultCache.exist(request.toRequestContext(sketch).resultCacheKey))
        executeRequest(request).also { result ->
            assertEquals(expected = 323, actual = result.image.width)
            assertEquals(expected = 484, actual = result.image.height)
            assertEquals(
                expected = "ImageInfo(size=1291x1936, mimeType='image/jpeg')",
                actual = result.imageInfo.toString()
            )
            assertEquals(
                expected = DataFrom.RESULT_CACHE,
                actual = result.dataFrom
            )
            assertEquals(
                expected = "InSampledTransformed(4)",
                actual = result.transformeds?.joinToString()
            )
            assertEquals(
                expected = mapOf("key" to "hasExtras"),
                actual = result.extras
            )
        }

        assertTrue(resultCache.exist(request.toRequestContext(sketch).resultCacheKey))
        executeRequest(request.newRequest {
            resultCachePolicy(DISABLED)
        }).also { result ->
            assertEquals(expected = 323, actual = result.image.width)
            assertEquals(expected = 484, actual = result.image.height)
            assertEquals(
                expected = "ImageInfo(size=1291x1936, mimeType='image/jpeg')",
                actual = result.imageInfo.toString()
            )
            assertEquals(
                expected = DataFrom.LOCAL,
                actual = result.dataFrom
            )
            assertEquals(
                expected = "InSampledTransformed(4)",
                actual = result.transformeds?.joinToString()
            )
            assertEquals(
                expected = mapOf("key" to "hasExtras"),
                actual = result.extras
            )
        }

        assertTrue(resultCache.exist(request.toRequestContext(sketch).resultCacheKey))
        executeRequest(request.newRequest {
            resultCachePolicy(WRITE_ONLY)
        }).also { result ->
            assertEquals(expected = 323, actual = result.image.width)
            assertEquals(expected = 484, actual = result.image.height)
            assertEquals(
                expected = "ImageInfo(size=1291x1936, mimeType='image/jpeg')",
                actual = result.imageInfo.toString()
            )
            assertEquals(
                expected = DataFrom.LOCAL,
                actual = result.dataFrom
            )
            assertEquals(
                expected = "InSampledTransformed(4)",
                actual = result.transformeds?.joinToString()
            )
            assertEquals(
                expected = mapOf("key" to "hasExtras"),
                actual = result.extras
            )
        }

        resultCache.clear()
        assertFalse(resultCache.exist(request.toRequestContext(sketch).resultCacheKey))
        executeRequest(request.newRequest {
            resultCachePolicy(READ_ONLY)
        }).also { result ->
            assertEquals(expected = 323, actual = result.image.width)
            assertEquals(expected = 484, actual = result.image.height)
            assertEquals(
                expected = "ImageInfo(size=1291x1936, mimeType='image/jpeg')",
                actual = result.imageInfo.toString()
            )
            assertEquals(
                expected = DataFrom.LOCAL,
                actual = result.dataFrom
            )
            assertEquals(
                expected = "InSampledTransformed(4)",
                actual = result.transformeds?.joinToString()
            )
            assertEquals(
                expected = mapOf("key" to "hasExtras"),
                actual = result.extras
            )
        }
        assertFalse(resultCache.exist(request.toRequestContext(sketch).resultCacheKey))

        val request1 = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(2000, 2000)
            precision(LESS_PIXELS)
            resultCachePolicy(ENABLED)
        }
        resultCache.clear()
        assertFalse(resultCache.exist(request1.toRequestContext(sketch).resultCacheKey))
        executeRequest(request1).also { result ->
            assertEquals(expected = 1291, actual = result.image.width)
            assertEquals(expected = 1936, actual = result.image.height)
            assertEquals(
                expected = "ImageInfo(size=1291x1936, mimeType='image/jpeg')",
                actual = result.imageInfo.toString()
            )
            assertEquals(
                expected = DataFrom.LOCAL,
                actual = result.dataFrom
            )
            assertEquals(
                expected = null,
                actual = result.transformeds?.joinToString()
            )
            assertEquals(
                expected = mapOf("key" to "hasExtras"),
                actual = result.extras
            )
        }
        assertFalse(resultCache.exist(request1.toRequestContext(sketch).resultCacheKey))
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ResultCacheDecodeInterceptor()
        val element11 = ResultCacheDecodeInterceptor()
        val element2 = ResultCacheDecodeInterceptor()

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
            expected = 80,
            actual = ResultCacheDecodeInterceptor().sortWeight
        )
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ResultCacheDecodeInterceptor(sortWeight=80)",
            actual = ResultCacheDecodeInterceptor().toString()
        )
    }

    class ExtrasTestDecodeInterceptor : DecodeInterceptor {

        override val key: String? = null

        override val sortWeight: Int = 0

        override suspend fun intercept(chain: DecodeInterceptor.Chain): Result<DecodeResult> {
            val decodeResult = chain.proceed().let {
                it.getOrNull() ?: return it
            }
            val newDecodeResult = decodeResult.newResult {
                addExtras("key", "hasExtras")
            }
            return Result.success(newDecodeResult)
        }

        override fun toString(): String {
            return "ExtrasTestDecodeInterceptor(sortWeight=$sortWeight)"
        }
    }
}