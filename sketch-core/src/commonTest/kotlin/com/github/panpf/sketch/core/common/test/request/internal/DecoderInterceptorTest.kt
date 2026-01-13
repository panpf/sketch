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

package com.github.panpf.sketch.core.common.test.request.internal

import com.github.panpf.sketch.fetch.internal.FetcherInterceptor
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.DecoderInterceptor
import com.github.panpf.sketch.request.internal.InterceptorChain
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.TestHttpUriFetcher
import com.github.panpf.sketch.test.utils.runInNewSketchWithUse
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.asOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class DecoderInterceptorTest {

    @Test
    fun testIntercept() = runTest {
        runInNewSketchWithUse({
            components {
                add(TestHttpUriFetcher.Factory(it))
            }
        }) { context, sketch ->
            val executeRequest: suspend (ImageRequest) -> ImageData = { request ->
                val chain = InterceptorChain(
                    requestContext = request.toRequestContext(sketch),
                    interceptors = listOf(FetcherInterceptor(), DecoderInterceptor()),
                    index = 0,
                )
                withContext(Dispatchers.Main) {
                    chain.proceed(request)
                }.getOrThrow()
            }

            executeRequest(ImageRequest(context, ResourceImages.jpeg.uri)).asOrThrow<ImageData>()

            executeRequest(ImageRequest(context, TestHttpStack.testImages.first().uri))
                .asOrThrow<ImageData>()
        }
    }

    @Test
    fun testIntercept2() = runTest {
        runInNewSketchWithUse({
            components {
                add(TestHttpUriFetcher.Factory(it))
            }
        }) { context, sketch ->
            val executeRequest: suspend (ImageRequest) -> ImageData = { request ->
                val chain = InterceptorChain(
                    requestContext = request.toRequestContext(sketch),
                    interceptors = listOf(DecoderInterceptor()),
                    index = 0,
                )
                withContext(Dispatchers.Main) {
                    chain.proceed(request)
                }.getOrThrow()
            }

            assertFailsWith(Exception::class) {
                executeRequest(ImageRequest(context, ResourceImages.jpeg.uri))
            }
        }
    }

    @Test
    fun testSortWeight() {
        assertEquals(
            expected = 100,
            actual = DecoderInterceptor().sortWeight
        )
        assertEquals(
            expected = 100,
            actual = DecoderInterceptor.SORT_WEIGHT
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = DecoderInterceptor()
        val element11 = DecoderInterceptor()
        val element2 = DecoderInterceptor()

        assertEquals(element1, element11)
        assertEquals(element1, element2)
        assertEquals(element2, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            "DecoderInterceptor",
            DecoderInterceptor().toString()
        )
    }
}