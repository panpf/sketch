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
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class RequestContextTest {

    @Test
    fun testRequestContext() {
        // TODO test
    }

    @Test
    fun testResolveSize() {
        // TODO test
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

    // TODO test logKey
    // TODO test registerCompletedListener

    @Test
    fun testCacheKey() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        ImageRequest(context, ResourceImages.jpeg.uri).toRequestContext(sketch).apply {
            val cacheKey0 = cacheKey

            setNewRequest(request.newRequest())
            val cacheKey1 = cacheKey
            assertSame(cacheKey0, cacheKey1)

            setNewRequest(request.newRequest {
                size(100, 300)
            })
            val cacheKey2 = cacheKey
            assertNotEquals(cacheKey1, cacheKey2)

            setNewRequest(request.newRequest {
                precision(Precision.EXACTLY)
            })
            val cacheKey3 = cacheKey
            assertNotEquals(cacheKey2, cacheKey3)

            setNewRequest(request.newRequest())
            val cacheKey4 = cacheKey
            assertSame(cacheKey3, cacheKey4)
        }
    }
}