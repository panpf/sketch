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

package com.github.panpf.sketch.extensions.core.common.test.request

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.internal.ResultCacheInterceptor
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.PauseLoadWhenScrollingInterceptor
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.ignorePauseLoadWhenScrolling
import com.github.panpf.sketch.request.isIgnoredPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isPauseLoadWhenScrolling
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.request.supportPauseLoadWhenScrolling
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchSize
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PauseLoadWhenScrollingInterceptorTest {

    @Test
    fun testSupportPauseLoadWhenScrolling() {
        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportPauseLoadWhenScrolling()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[]," +
                        "interceptors=[PauseLoadWhenScrollingInterceptor]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportPauseLoadWhenScrolling()
            supportPauseLoadWhenScrolling()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[]," +
                        "interceptors=[PauseLoadWhenScrollingInterceptor,PauseLoadWhenScrollingInterceptor]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun test() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val interceptor = PauseLoadWhenScrollingInterceptor()

        try {
            ImageRequest(context, ComposeResImageFiles.jpeg.uri).let { request ->
                assertTrue(interceptor.enabled)
                assertFalse(PauseLoadWhenScrollingInterceptor.scrolling)
                assertFalse(request.isPauseLoadWhenScrolling)
                assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                val job = async {
                    interceptor.intercept(request.toInterceptorChain(sketch))
                }
                delay(1000)
                assertTrue(job.isCompleted)
            }

            ImageRequest(context, ComposeResImageFiles.jpeg.uri).let { request ->
                PauseLoadWhenScrollingInterceptor.scrolling = true
                assertTrue(interceptor.enabled)
                assertTrue(PauseLoadWhenScrollingInterceptor.scrolling)
                assertFalse(request.isPauseLoadWhenScrolling)
                assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                val job = async {
                    interceptor.intercept(request.toInterceptorChain(sketch))
                }
                delay(1000)
                assertTrue(job.isCompleted)
            }

            ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
                pauseLoadWhenScrolling()
            }.let { request ->
                PauseLoadWhenScrollingInterceptor.scrolling = true
                assertTrue(interceptor.enabled)
                assertTrue(PauseLoadWhenScrollingInterceptor.scrolling)
                assertTrue(request.isPauseLoadWhenScrolling)
                assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                val job = async {
                    interceptor.intercept(request.toInterceptorChain(sketch))
                }
                delay(1000)
                assertFalse(job.isCompleted)
                PauseLoadWhenScrollingInterceptor.scrolling = false
                delay(1000)
                assertTrue(job.isCompleted)
            }

            ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
                pauseLoadWhenScrolling()
                ignorePauseLoadWhenScrolling()
            }.let { request ->
                PauseLoadWhenScrollingInterceptor.scrolling = true
                assertTrue(interceptor.enabled)
                assertTrue(PauseLoadWhenScrollingInterceptor.scrolling)
                assertTrue(request.isPauseLoadWhenScrolling)
                assertTrue(request.isIgnoredPauseLoadWhenScrolling)
                val job = async {
                    interceptor.intercept(request.toInterceptorChain(sketch))
                }
                delay(1000)
                assertTrue(job.isCompleted)
            }
        } finally {
            PauseLoadWhenScrollingInterceptor.scrolling = false
        }
    }

    @Test
    fun testSortWeight() {
        assertEquals(
            expected = ResultCacheInterceptor.SORT_WEIGHT - 1,
            actual = PauseLoadWhenScrollingInterceptor().sortWeight
        )
        assertEquals(
            expected = ResultCacheInterceptor.SORT_WEIGHT - 1,
            actual = PauseLoadWhenScrollingInterceptor.SORT_WEIGHT
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = PauseLoadWhenScrollingInterceptor()
        val element11 = PauseLoadWhenScrollingInterceptor().apply { enabled = false }

        assertEquals(element1, element11)
        assertNotEquals(element1, null as PauseLoadWhenScrollingInterceptor?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        PauseLoadWhenScrollingInterceptor().apply {
            assertEquals(
                "PauseLoadWhenScrollingInterceptor",
                toString()
            )
        }
    }

    private suspend fun ImageRequest.toInterceptorChain(sketch: Sketch): Interceptor.Chain {
        val fetchResult = sketch.components.newFetcherOrThrow(
            this@toInterceptorChain.toRequestContext(sketch, Size.Empty)
        ).fetch()
            .getOrThrow()
        val requestContext = this.toRequestContext(sketch)
        requestContext.fetchResult = fetchResult
        return TestInterceptorChain(
            sketch = sketch,
            request = this,
            requestContext = requestContext,
        )
    }

    class TestInterceptorChain(
        override val sketch: Sketch,
        override val request: ImageRequest,
        override val requestContext: RequestContext,
    ) : Interceptor.Chain {

        private var finalRequest = request

        override suspend fun proceed(request: ImageRequest): Result<ImageData> {
            finalRequest = request
            return Result.success(
                ImageData(
                    image = FakeImage(SketchSize(100, 100)),
                    imageInfo = ImageInfo(100, 100, "image/xml"),
                    dataFrom = LOCAL,
                    resize = Resize(100, 100, LESS_PIXELS, CENTER_CROP),
                    transformeds = null,
                    extras = null
                )
            )
        }
    }
}