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

import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CachePolicy.READ_ONLY
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.cache.createImageSerializer
import com.github.panpf.sketch.cache.internal.ResultCacheRequestInterceptor
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.internal.FetcherRequestInterceptor
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.RequestInterceptorChain
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.MyCacheKeyMapper
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.createBitmapImage
import com.github.panpf.sketch.test.utils.current
import com.github.panpf.sketch.test.utils.exist
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import okio.buffer
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResultCacheRequestInterceptorTest {

    @Test
    fun testIntercept() = runTest {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return@runTest
        }
        val (context, sketch) = getTestContextAndSketch()
        val resultCache = sketch.resultCache

        val executeRequest: suspend (ImageRequest) -> ImageData = { request ->
            withContext(Dispatchers.Main) {
                RequestInterceptorChain(
                    requestContext = request.toRequestContext(sketch),
                    interceptors = listOf(
                        ResultCacheRequestInterceptor(),
                        ExtrasTestRequestInterceptor(),
                        FetcherRequestInterceptor(),
                        EngineRequestInterceptor()
                    ),
                    index = 0
                ).proceed(request).getOrThrow()
            }
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
    fun testResultCacheKey() = runTest {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return@runTest
        }
        val (context, sketch) = getTestContextAndSketch()
        val resultCache = sketch.resultCache

        val executeRequest: suspend (ImageRequest) -> ImageData? = { request ->
            withContext(Dispatchers.Main) {
                RequestInterceptorChain(
                    requestContext = request.toRequestContext(sketch),
                    interceptors = listOf(
                        ResultCacheRequestInterceptor(),
                        ExtrasTestRequestInterceptor(),
                        FetcherRequestInterceptor(),
                        EngineRequestInterceptor()
                    ),
                    index = 0
                ).proceed(request).getOrNull()
            }
        }

        resultCache.clear()
        assertEquals(expected = 0, actual = resultCache.size)

        val editor = resultCache.openEditor(key = "resultCacheKey1")!!
        resultCache.fileSystem.sink(editor.data).buffer().use {
            val bitmapImage = createBitmapImage(100, 100)
            createImageSerializer().compress(bitmapImage, it)
        }
        resultCache.fileSystem.sink(editor.metadata).buffer().use {
            val metadata = ResultCacheRequestInterceptor.Metadata(
                imageInfo = ImageInfo(width = 100, height = 100, mimeType = "image/png"),
                resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
                transformeds = null,
                extras = null
            )
            it.writeUtf8(metadata.toMetadataString())
        }
        editor.commit()
        assertEquals(expected = 273, actual = resultCache.size)

        executeRequest(ImageRequest(context, "http://sample.com/sample.jpeg") {
            resultCachePolicy(ENABLED)
            depth(Depth.LOCAL)
        }).apply {
            assertNull(this)
        }

        executeRequest(ImageRequest(context, "http://sample.com/sample.jpeg") {
            resultCachePolicy(ENABLED)
            depth(Depth.LOCAL)
            resultCacheKey("resultCacheKey1")
        }).apply {
            assertNotNull(this)
        }

        executeRequest(ImageRequest(context, "http://sample.com/sample.jpeg") {
            resultCachePolicy(ENABLED)
            depth(Depth.LOCAL)
            resultCacheKeyMapper(MyCacheKeyMapper("resultCacheKey1"))
        }).apply {
            assertNotNull(this)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ResultCacheRequestInterceptor()
        val element11 = ResultCacheRequestInterceptor()
        val element2 = ResultCacheRequestInterceptor()

        assertNotSame(illegal = element1, actual = element11)
        assertNotSame(illegal = element1, actual = element2)
        assertNotSame(illegal = element2, actual = element11)

        assertEquals(expected = element1, actual = element11)
        assertEquals(expected = element1, actual = element2)
        assertEquals(expected = element2, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertEquals(expected = element1.hashCode(), actual = element2.hashCode())
        assertEquals(expected = element2.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testSortWeight() {
        assertEquals(
            expected = 95,
            actual = ResultCacheRequestInterceptor().sortWeight
        )
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ResultCacheRequestInterceptor",
            actual = ResultCacheRequestInterceptor().toString()
        )
    }

    class ExtrasTestRequestInterceptor : RequestInterceptor {

        override val key: String? = null
        override val sortWeight: Int = 0

        override suspend fun intercept(chain: RequestInterceptor.Chain): Result<ImageData> {
            val request = chain.request
            val imageData = chain.proceed(request).let {
                it.getOrNull() ?: return it
            }
            val newDecodeResult = imageData.newImageData {
                addExtras("key", "hasExtras")
            }
            return Result.success(newDecodeResult)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String {
            return "ExtrasTestRequestInterceptor"
        }
    }
}