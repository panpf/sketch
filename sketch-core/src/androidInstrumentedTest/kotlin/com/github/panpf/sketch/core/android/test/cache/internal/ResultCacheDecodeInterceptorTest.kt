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

import androidx.test.ext.junit.runners.AndroidJUnit4
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
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.exist
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResultCacheDecodeInterceptorTest {

    @Test
    fun testIntercept() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val resultCache = sketch.resultCache

        val interceptors =
            listOf(
                ResultCacheDecodeInterceptor(),
                ExtrasTestDecodeInterceptor(),
                EngineDecodeInterceptor()
            )
        val executeRequest: (ImageRequest) -> DecodeResult = { request ->
            runBlocking {
                DecodeInterceptorChain(
                    sketch = sketch,
                    request = request,
                    requestContext = request.toRequestContext(sketch),
                    fetchResult = null,
                    interceptors = interceptors,
                    index = 0
                ).proceed()
            }.getOrThrow()
        }

        val request = ImageRequest(context, MyImages.jpeg.uri) {
            size(500, 500)
            precision(LESS_PIXELS)
            resultCachePolicy(ENABLED)
        }

        resultCache.clear()
        Assert.assertFalse(resultCache.exist(request.toRequestContext(sketch).resultCacheKey))
        executeRequest(request).also { result ->
            Assert.assertEquals(323, result.image.getBitmapOrThrow().width)
            Assert.assertEquals(484, result.image.getBitmapOrThrow().height)
            Assert.assertEquals(
                "ImageInfo(size=1291x1936, mimeType='image/jpeg')",
                result.imageInfo.toString()
            )
            Assert.assertEquals(DataFrom.LOCAL, result.dataFrom)
            Assert.assertEquals(
                "InSampledTransformed(4)",
                result.transformedList?.joinToString()
            )
            Assert.assertEquals(
                mapOf("key" to "hasExtras"),
                result.extras
            )
        }

        Assert.assertTrue(resultCache.exist(request.toRequestContext(sketch).resultCacheKey))
        executeRequest(request).also { result ->
            Assert.assertEquals(323, result.image.getBitmapOrThrow().width)
            Assert.assertEquals(484, result.image.getBitmapOrThrow().height)
            Assert.assertEquals(
                "ImageInfo(size=1291x1936, mimeType='image/jpeg')",
                result.imageInfo.toString()
            )
            Assert.assertEquals(DataFrom.RESULT_CACHE, result.dataFrom)
            Assert.assertEquals(
                "InSampledTransformed(4)",
                result.transformedList?.joinToString()
            )
            Assert.assertEquals(
                mapOf("key" to "hasExtras"),
                result.extras
            )
        }

        Assert.assertTrue(resultCache.exist(request.toRequestContext(sketch).resultCacheKey))
        executeRequest(request.newRequest {
            resultCachePolicy(DISABLED)
        }).also { result ->
            Assert.assertEquals(323, result.image.getBitmapOrThrow().width)
            Assert.assertEquals(484, result.image.getBitmapOrThrow().height)
            Assert.assertEquals(
                "ImageInfo(size=1291x1936, mimeType='image/jpeg')",
                result.imageInfo.toString()
            )
            Assert.assertEquals(DataFrom.LOCAL, result.dataFrom)
            Assert.assertEquals(
                "InSampledTransformed(4)",
                result.transformedList?.joinToString()
            )
            Assert.assertEquals(
                mapOf("key" to "hasExtras"),
                result.extras
            )
        }

        Assert.assertTrue(resultCache.exist(request.toRequestContext(sketch).resultCacheKey))
        executeRequest(request.newRequest {
            resultCachePolicy(WRITE_ONLY)
        }).also { result ->
            Assert.assertEquals(323, result.image.getBitmapOrThrow().width)
            Assert.assertEquals(484, result.image.getBitmapOrThrow().height)
            Assert.assertEquals(
                "ImageInfo(size=1291x1936, mimeType='image/jpeg')",
                result.imageInfo.toString()
            )
            Assert.assertEquals(DataFrom.LOCAL, result.dataFrom)
            Assert.assertEquals(
                "InSampledTransformed(4)",
                result.transformedList?.joinToString()
            )
            Assert.assertEquals(
                mapOf("key" to "hasExtras"),
                result.extras
            )
        }

        resultCache.clear()
        Assert.assertFalse(resultCache.exist(request.toRequestContext(sketch).resultCacheKey))
        executeRequest(request.newRequest {
            resultCachePolicy(READ_ONLY)
        }).also { result ->
            Assert.assertEquals(323, result.image.getBitmapOrThrow().width)
            Assert.assertEquals(484, result.image.getBitmapOrThrow().height)
            Assert.assertEquals(
                "ImageInfo(size=1291x1936, mimeType='image/jpeg')",
                result.imageInfo.toString()
            )
            Assert.assertEquals(DataFrom.LOCAL, result.dataFrom)
            Assert.assertEquals(
                "InSampledTransformed(4)",
                result.transformedList?.joinToString()
            )
            Assert.assertEquals(
                mapOf("key" to "hasExtras"),
                result.extras
            )
        }
        Assert.assertFalse(resultCache.exist(request.toRequestContext(sketch).resultCacheKey))

        val request1 = ImageRequest(context, MyImages.jpeg.uri) {
            size(2000, 2000)
            precision(LESS_PIXELS)
            resultCachePolicy(ENABLED)
        }
        resultCache.clear()
        Assert.assertFalse(resultCache.exist(request1.toRequestContext(sketch).resultCacheKey))
        executeRequest(request1).also { result ->
            Assert.assertEquals(1291, result.image.getBitmapOrThrow().width)
            Assert.assertEquals(1936, result.image.getBitmapOrThrow().height)
            Assert.assertEquals(
                "ImageInfo(size=1291x1936, mimeType='image/jpeg')",
                result.imageInfo.toString()
            )
            Assert.assertEquals(DataFrom.LOCAL, result.dataFrom)
            Assert.assertEquals(null, result.transformedList?.joinToString())
            Assert.assertEquals(
                mapOf("key" to "hasExtras"),
                result.extras
            )
        }
        Assert.assertFalse(resultCache.exist(request1.toRequestContext(sketch).resultCacheKey))
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ResultCacheDecodeInterceptor()
        val element11 = ResultCacheDecodeInterceptor()
        val element2 = ResultCacheDecodeInterceptor()

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
        ResultCacheDecodeInterceptor().apply {
            Assert.assertEquals(80, sortWeight)
        }
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "ResultCacheDecodeInterceptor(sortWeight=80)",
            ResultCacheDecodeInterceptor().toString()
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