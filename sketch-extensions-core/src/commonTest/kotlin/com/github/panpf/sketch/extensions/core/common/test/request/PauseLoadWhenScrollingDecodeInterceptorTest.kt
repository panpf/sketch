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

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDecodeInterceptor
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.ignorePauseLoadWhenScrolling
import com.github.panpf.sketch.request.isIgnoredPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isPauseLoadWhenScrolling
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.SketchSize
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class PauseLoadWhenScrollingDecodeInterceptorTest {

    @Test
    fun test() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val interceptor = PauseLoadWhenScrollingDecodeInterceptor()

        try {
            ImageRequest(context, ResourceImages.jpeg.uri).let { request ->
                assertTrue(interceptor.enabled)
                assertFalse(PauseLoadWhenScrollingDecodeInterceptor.scrolling)
                assertFalse(request.isPauseLoadWhenScrolling)
                assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                val job = async {
                    interceptor.intercept(request.toDecodeInterceptorChain(sketch))
                }
                delay(1000)
                assertTrue(job.isCompleted)
            }

            ImageRequest(context, ResourceImages.jpeg.uri).let { request ->
                PauseLoadWhenScrollingDecodeInterceptor.scrolling = true
                assertTrue(interceptor.enabled)
                assertTrue(PauseLoadWhenScrollingDecodeInterceptor.scrolling)
                assertFalse(request.isPauseLoadWhenScrolling)
                assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                val job = async {
                    interceptor.intercept(request.toDecodeInterceptorChain(sketch))
                }
                delay(1000)
                assertTrue(job.isCompleted)
            }

            ImageRequest(context, ResourceImages.jpeg.uri) {
                pauseLoadWhenScrolling()
            }.let { request ->
                PauseLoadWhenScrollingDecodeInterceptor.scrolling = true
                assertTrue(interceptor.enabled)
                assertTrue(PauseLoadWhenScrollingDecodeInterceptor.scrolling)
                assertTrue(request.isPauseLoadWhenScrolling)
                assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                val job = async {
                    interceptor.intercept(request.toDecodeInterceptorChain(sketch))
                }
                delay(1000)
                assertFalse(job.isCompleted)
                PauseLoadWhenScrollingDecodeInterceptor.scrolling = false
                delay(1000)
                assertTrue(job.isCompleted)
            }

            ImageRequest(context, ResourceImages.jpeg.uri) {
                pauseLoadWhenScrolling()
                ignorePauseLoadWhenScrolling()
            }.let { request ->
                PauseLoadWhenScrollingDecodeInterceptor.scrolling = true
                assertTrue(interceptor.enabled)
                assertTrue(PauseLoadWhenScrollingDecodeInterceptor.scrolling)
                assertTrue(request.isPauseLoadWhenScrolling)
                assertTrue(request.isIgnoredPauseLoadWhenScrolling)
                val job = async {
                    interceptor.intercept(request.toDecodeInterceptorChain(sketch))
                }
                delay(1000)
                assertTrue(job.isCompleted)
            }
        } finally {
            PauseLoadWhenScrollingDecodeInterceptor.scrolling = false
        }
    }

    @Test
    fun testSortWeight() {
        PauseLoadWhenScrollingDecodeInterceptor().apply {
            assertEquals(0, sortWeight)
        }

        PauseLoadWhenScrollingDecodeInterceptor(30).apply {
            assertEquals(30, sortWeight)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = PauseLoadWhenScrollingDecodeInterceptor()
        val element11 = PauseLoadWhenScrollingDecodeInterceptor().apply { enabled = false }
        val element2 = PauseLoadWhenScrollingDecodeInterceptor(30)

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as PauseLoadWhenScrollingDecodeInterceptor?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        PauseLoadWhenScrollingDecodeInterceptor().apply {
            assertEquals(
                "PauseLoadWhenScrollingDecodeInterceptor(sortWeight=0,enabled=true)",
                toString()
            )
        }

        PauseLoadWhenScrollingDecodeInterceptor(30).apply {
            enabled = false
            assertEquals(
                "PauseLoadWhenScrollingDecodeInterceptor(sortWeight=30,enabled=false)",
                toString()
            )
        }
    }

    private suspend fun ImageRequest.toDecodeInterceptorChain(sketch: Sketch): DecodeInterceptor.Chain {
        return TestDecodeInterceptorChain(
            sketch = sketch,
            request = this,
            requestContext = this.toRequestContext(sketch),
            fetchResult = sketch.components.newFetcherOrThrow(this@toDecodeInterceptorChain).fetch()
                .getOrThrow()
        )
    }

    class TestDecodeInterceptorChain(
        override val sketch: Sketch,
        override val request: ImageRequest,
        override val requestContext: RequestContext,
        override val fetchResult: FetchResult?
    ) : DecodeInterceptor.Chain {

        private var finalRequest = request

        override suspend fun proceed(): Result<DecodeResult> {
            finalRequest = request
            return Result.success(
                DecodeResult(
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