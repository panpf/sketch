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

package com.github.panpf.sketch.core.common.test.request

import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.internal.newCacheKey
import com.github.panpf.sketch.request.resolveSize
import com.github.panpf.sketch.resize.LongImagePrecisionDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.AppendCacheKeyMapper
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.screenSize
import com.github.panpf.sketch.util.times
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class RequestContextTest {

    @Test
    fun testRequestContext() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(201, 303)
        }
        RequestContext(sketch, request).apply {
            assertEquals(expected = Size(201, 303), actual = size)
            assertSame(expected = request, actual = initialRequest)
            assertSame(expected = request, actual = request)
        }
    }

    @Test
    fun testResolveSize() = runTest {
        val context = getTestContext()
        assertEquals(
            expected = Size(201, 303).times(1f),
            actual = resolveSize(ImageRequest(context, ResourceImages.jpeg.uri) {
                size(201, 303)
            })
        )

        assertEquals(
            expected = Size(201, 303).times(1.5f),
            actual = resolveSize(ImageRequest(context, ResourceImages.jpeg.uri) {
                size(201, 303)
                sizeMultiplier(1.5f)
            })
        )
    }

    @Test
    fun testRequest() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request0 = ImageRequest(context, ResourceImages.jpeg.uri)
        request0.toRequestContext(sketch).apply {
            assertSame(request0, request)
            assertEquals(listOf(request0), requestList)

            val request1 = request0.newRequest()
            setNewRequest(request1)
            assertSame(request0, request)
            assertEquals(listOf(request0), requestList)

            val request2 = request0.newRequest {
                depth(LOCAL)
            }
            setNewRequest(request2)
            assertSame(request2, request)
            assertEquals(listOf(request0, request2), requestList)

            val request3 = request2.newRequest {
                memoryCachePolicy(DISABLED)
            }
            setNewRequest(request3)
            assertSame(request3, request)
            assertEquals(listOf(request0, request2, request3), requestList)
        }
    }

    @Test
    fun testSize() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        RequestContext(
            sketch = sketch,
            request = ImageRequest(context, ResourceImages.jpeg.uri) {
                size(201, 303)
            }
        ).apply {
            assertEquals(expected = Size(201, 303), actual = size)
            setNewRequest(ImageRequest(context, ResourceImages.jpeg.uri) {
                size(400, 500)
            })
            assertEquals(expected = Size(400, 500), actual = size)
        }
    }

    @Test
    fun testLogKey() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request1 = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(201, 303)
        }
        val request2 = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(400, 500)
        }
        RequestContext(sketch = sketch, request = request1).apply {
            assertEquals(expected = request1.newCacheKey(Size(201, 303)), actual = logKey)
            setNewRequest(request2)
            assertEquals(expected = request1.newCacheKey(Size(201, 303)), actual = logKey)
            assertNotEquals(illegal = request1.key, actual = request2.key)
        }
    }

    @Test
    fun testCacheKeys() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val uri = ResourceImages.jpeg.uri
        val requestContext = ImageRequest(context, uri).toRequestContext(sketch)
        val screenSize = context.screenSize()
        assertEquals(
            expected = "${uri}?_size=${screenSize}&_precision=Fixed(LESS_PIXELS)&_scale=Fixed(CENTER_CROP)",
            actual = requestContext.cacheKey
        )
        assertEquals(
            expected = "${uri}?_size=${screenSize}&_precision=Fixed(LESS_PIXELS)&_scale=Fixed(CENTER_CROP)",
            actual = requestContext.memoryCacheKey
        )
        assertEquals(
            expected = "${uri}?_size=${screenSize}&_precision=Fixed(LESS_PIXELS)&_scale=Fixed(CENTER_CROP)",
            actual = requestContext.resultCacheKey
        )
        assertEquals(
            expected = uri,
            actual = requestContext.downloadCacheKey
        )

        // test CacheKeyMapper
        requestContext.setNewRequest(ImageRequest(context, uri) {
            memoryCacheKeyMapper(AppendCacheKeyMapper("&from=memory"))
            resultCacheKeyMapper(AppendCacheKeyMapper("&from=result"))
            downloadCacheKeyMapper(AppendCacheKeyMapper("&from=download"))
        })
        assertEquals(
            expected = "${uri}?_size=${screenSize}&_precision=Fixed(LESS_PIXELS)&_scale=Fixed(CENTER_CROP)",
            actual = requestContext.cacheKey
        )
        assertEquals(
            expected = "${uri}?_size=${screenSize}&_precision=Fixed(LESS_PIXELS)&_scale=Fixed(CENTER_CROP)&from=memory",
            actual = requestContext.memoryCacheKey
        )
        assertEquals(
            expected = "${uri}?_size=${screenSize}&_precision=Fixed(LESS_PIXELS)&_scale=Fixed(CENTER_CROP)&from=result",
            actual = requestContext.resultCacheKey
        )
        assertEquals(
            expected = "${uri}&from=download",
            actual = requestContext.downloadCacheKey
        )

        // test CacheKeyMapper + memoryCacheKey + resultCacheKey + downloadCacheKey
        requestContext.setNewRequest(ImageRequest(context, uri) {
            memoryCacheKeyMapper(AppendCacheKeyMapper("&from=memory"))
            resultCacheKeyMapper(AppendCacheKeyMapper("&from=result"))
            downloadCacheKeyMapper(AppendCacheKeyMapper("&from=download"))

            memoryCacheKey("memoryCacheKey1")
            resultCacheKey("resultCacheKey1")
            downloadCacheKey("downloadCacheKey1")
        })
        assertEquals(
            expected = "${uri}?_size=${screenSize}&_precision=Fixed(LESS_PIXELS)&_scale=Fixed(CENTER_CROP)",
            actual = requestContext.cacheKey
        )
        assertEquals(
            expected = "memoryCacheKey1",
            actual = requestContext.memoryCacheKey
        )
        assertEquals(
            expected = "resultCacheKey1",
            actual = requestContext.resultCacheKey
        )
        assertEquals(
            expected = "downloadCacheKey1",
            actual = requestContext.downloadCacheKey
        )
    }

    @Test
    fun testComputeResize() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val requestContext = RequestContext(
            sketch = sketch,
            request = ImageRequest(context, ResourceImages.jpeg.uri) {
                size(201, 303)
                precision(LongImagePrecisionDecider())
                scale(LongImageScaleDecider())
            }
        )
        assertEquals(
            expected = Resize(201, 303, Precision.LESS_PIXELS, Scale.CENTER_CROP),
            actual = requestContext.computeResize(Size(1000, 2000))
        )
        assertEquals(
            expected = Resize(201, 303, Precision.SAME_ASPECT_RATIO, Scale.START_CROP),
            actual = requestContext.computeResize(Size(10000, 2000))
        )
    }
}