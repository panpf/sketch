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
package com.github.panpf.sketch.test.decode.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.BitmapResultCacheDecodeInterceptor
import com.github.panpf.sketch.decode.internal.EngineBitmapDecodeInterceptor
import com.github.panpf.sketch.decode.internal.resultCacheDataKey
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapResultCacheDecodeInterceptorTest {

    @Test
    fun testIntercept() {
        val (context, sketch) = getTestContextAndNewSketch()
        val resultCache = sketch.resultCache

        val interceptors =
            listOf(
                BitmapResultCacheDecodeInterceptor(),
                ExtrasTestBitmapDecodeInterceptor(),
                EngineBitmapDecodeInterceptor()
            )
        val executeRequest: (ImageRequest) -> BitmapDecodeResult = { request ->
            runBlocking {
                BitmapDecodeInterceptorChain(
                    sketch = sketch,
                    request = request,
                    requestContext = request.toRequestContext(),
                    fetchResult = null,
                    interceptors = interceptors,
                    index = 0
                ).proceed()
            }
        }

        val loadRequest = LoadRequest(context, newAssetUri("sample.jpeg")) {
            resizeSize(500, 500)
            resizePrecision(LESS_PIXELS)
            resultCachePolicy(ENABLED)
        }

        resultCache.clear()
        Assert.assertFalse(resultCache.exist(loadRequest.toRequestContext().resultCacheDataKey))
        executeRequest(loadRequest).also { result ->
            Assert.assertEquals(323, result.bitmap.width)
            Assert.assertEquals(484, result.bitmap.height)
            Assert.assertEquals(
                "ImageInfo(width=1291, height=1936, mimeType='image/jpeg', exifOrientation=NORMAL)",
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

        Assert.assertTrue(resultCache.exist(loadRequest.toRequestContext().resultCacheDataKey))
        executeRequest(loadRequest).also { result ->
            Assert.assertEquals(323, result.bitmap.width)
            Assert.assertEquals(484, result.bitmap.height)
            Assert.assertEquals(
                "ImageInfo(width=1291, height=1936, mimeType='image/jpeg', exifOrientation=NORMAL)",
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

        Assert.assertTrue(resultCache.exist(loadRequest.toRequestContext().resultCacheDataKey))
        executeRequest(loadRequest.newLoadRequest {
            resultCachePolicy(DISABLED)
        }).also { result ->
            Assert.assertEquals(323, result.bitmap.width)
            Assert.assertEquals(484, result.bitmap.height)
            Assert.assertEquals(
                "ImageInfo(width=1291, height=1936, mimeType='image/jpeg', exifOrientation=NORMAL)",
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

        Assert.assertTrue(resultCache.exist(loadRequest.toRequestContext().resultCacheDataKey))
        executeRequest(loadRequest.newLoadRequest {
            resultCachePolicy(WRITE_ONLY)
        }).also { result ->
            Assert.assertEquals(323, result.bitmap.width)
            Assert.assertEquals(484, result.bitmap.height)
            Assert.assertEquals(
                "ImageInfo(width=1291, height=1936, mimeType='image/jpeg', exifOrientation=NORMAL)",
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
        Assert.assertFalse(resultCache.exist(loadRequest.toRequestContext().resultCacheDataKey))
        executeRequest(loadRequest.newLoadRequest {
            resultCachePolicy(READ_ONLY)
        }).also { result ->
            Assert.assertEquals(323, result.bitmap.width)
            Assert.assertEquals(484, result.bitmap.height)
            Assert.assertEquals(
                "ImageInfo(width=1291, height=1936, mimeType='image/jpeg', exifOrientation=NORMAL)",
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
        Assert.assertFalse(resultCache.exist(loadRequest.toRequestContext().resultCacheDataKey))

        val loadRequest1 = LoadRequest(context, newAssetUri("sample.jpeg")) {
            resizeSize(2000, 2000)
            resizePrecision(LESS_PIXELS)
            resultCachePolicy(ENABLED)
        }
        resultCache.clear()
        Assert.assertFalse(resultCache.exist(loadRequest1.toRequestContext().resultCacheDataKey))
        executeRequest(loadRequest1).also { result ->
            Assert.assertEquals(1291, result.bitmap.width)
            Assert.assertEquals(1936, result.bitmap.height)
            Assert.assertEquals(
                "ImageInfo(width=1291, height=1936, mimeType='image/jpeg', exifOrientation=NORMAL)",
                result.imageInfo.toString()
            )
            Assert.assertEquals(DataFrom.LOCAL, result.dataFrom)
            Assert.assertEquals(null, result.transformedList?.joinToString())
            Assert.assertEquals(
                mapOf("key" to "hasExtras"),
                result.extras
            )
        }
        Assert.assertFalse(resultCache.exist(loadRequest1.toRequestContext().resultCacheDataKey))
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BitmapResultCacheDecodeInterceptor()
        val element11 = BitmapResultCacheDecodeInterceptor()
        val element2 = BitmapResultCacheDecodeInterceptor()

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
        BitmapResultCacheDecodeInterceptor().apply {
            Assert.assertEquals(80, sortWeight)
        }
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "BitmapResultCacheDecodeInterceptor(sortWeight=80)",
            BitmapResultCacheDecodeInterceptor().toString()
        )
    }

    class ExtrasTestBitmapDecodeInterceptor : BitmapDecodeInterceptor {

        override val key: String? = null
        override val sortWeight: Int = 0

        override suspend fun intercept(chain: BitmapDecodeInterceptor.Chain): BitmapDecodeResult {
            return chain.proceed().newResult {
                addExtras("key", "hasExtras")
            }
        }

        override fun toString(): String {
            return "ExtrasTestBitmapDecodeInterceptor(sortWeight=$sortWeight)"
        }
    }
}