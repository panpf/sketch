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
package com.github.panpf.sketch.extensions.test.request

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.annotation.MainThread
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.extensions.test.toRequestContext
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDisplayInterceptor
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.ignorePauseLoadWhenScrolling
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.isDepthFromPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isIgnoredPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isPauseLoadWhenScrolling
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.sketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PauseLoadWhenScrollingDisplayInterceptorTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val interceptor = PauseLoadWhenScrollingDisplayInterceptor()

        try {// default
            DisplayRequest(context, "http://sample.com/sample.jpeg").let { request ->
                val chain =
                    TestRequestInterceptorChain(
                        sketch,
                        request,
                        request,
                        request.toRequestContext()
                    )

                Assert.assertTrue(interceptor.enabled)
                Assert.assertFalse(PauseLoadWhenScrollingDisplayInterceptor.scrolling)
                Assert.assertFalse(request.isPauseLoadWhenScrolling)
                Assert.assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                Assert.assertEquals(Depth.NETWORK, request.depth)
                Assert.assertFalse(request.isDepthFromPauseLoadWhenScrolling)

                runBlocking {
                    interceptor.intercept(chain)
                }
                Assert.assertEquals(Depth.NETWORK, chain.request.depth)
                Assert.assertFalse(chain.request.isDepthFromPauseLoadWhenScrolling)
            }

            // success
            interceptor.enabled = true
            PauseLoadWhenScrollingDisplayInterceptor.scrolling = true
            DisplayRequest(context, "http://sample.com/sample.jpeg") {
                pauseLoadWhenScrolling()
            }.let { request ->
                val chain =
                    TestRequestInterceptorChain(
                        sketch,
                        request,
                        request,
                        request.toRequestContext()
                    )

                Assert.assertTrue(interceptor.enabled)
                Assert.assertTrue(PauseLoadWhenScrollingDisplayInterceptor.scrolling)
                Assert.assertTrue(request.isPauseLoadWhenScrolling)
                Assert.assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                Assert.assertEquals(Depth.NETWORK, request.depth)
                Assert.assertFalse(request.isDepthFromPauseLoadWhenScrolling)

                runBlocking {
                    interceptor.intercept(chain)
                }
                Assert.assertEquals(MEMORY, chain.finalRequest.depth)
                Assert.assertTrue(chain.finalRequest.isDepthFromPauseLoadWhenScrolling)
            }

            // Request type error
            interceptor.enabled = true
            PauseLoadWhenScrollingDisplayInterceptor.scrolling = true
            LoadRequest(context, "http://sample.com/sample.jpeg") {
                pauseLoadWhenScrolling()
            }.let { request ->
                val chain =
                    TestRequestInterceptorChain(
                        sketch,
                        request,
                        request,
                        request.toRequestContext()
                    )

                Assert.assertTrue(interceptor.enabled)
                Assert.assertTrue(PauseLoadWhenScrollingDisplayInterceptor.scrolling)
                Assert.assertTrue(request.isPauseLoadWhenScrolling)
                Assert.assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                Assert.assertEquals(Depth.NETWORK, request.depth)
                Assert.assertFalse(request.isDepthFromPauseLoadWhenScrolling)

                runBlocking {
                    interceptor.intercept(chain)
                }
                Assert.assertEquals(Depth.NETWORK, chain.finalRequest.depth)
                Assert.assertFalse(chain.finalRequest.isDepthFromPauseLoadWhenScrolling)
            }

            // enabled false
            interceptor.enabled = false
            PauseLoadWhenScrollingDisplayInterceptor.scrolling = true
            DisplayRequest(context, "http://sample.com/sample.jpeg") {
                pauseLoadWhenScrolling()
            }.let { request ->
                val chain =
                    TestRequestInterceptorChain(
                        sketch,
                        request,
                        request,
                        request.toRequestContext()
                    )

                Assert.assertFalse(interceptor.enabled)
                Assert.assertTrue(PauseLoadWhenScrollingDisplayInterceptor.scrolling)
                Assert.assertTrue(request.isPauseLoadWhenScrolling)
                Assert.assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                Assert.assertEquals(Depth.NETWORK, request.depth)
                Assert.assertFalse(request.isDepthFromPauseLoadWhenScrolling)

                runBlocking {
                    interceptor.intercept(chain)
                }
                Assert.assertEquals(Depth.NETWORK, chain.finalRequest.depth)
                Assert.assertFalse(chain.finalRequest.isDepthFromPauseLoadWhenScrolling)
            }

            // scrolling false
            interceptor.enabled = true
            PauseLoadWhenScrollingDisplayInterceptor.scrolling = false
            DisplayRequest(context, "http://sample.com/sample.jpeg") {
                pauseLoadWhenScrolling()
            }.let { request ->
                val chain = TestRequestInterceptorChain(
                    sketch,
                    request,
                    request,
                    request.toRequestContext()
                )

                Assert.assertTrue(interceptor.enabled)
                Assert.assertFalse(PauseLoadWhenScrollingDisplayInterceptor.scrolling)
                Assert.assertTrue(request.isPauseLoadWhenScrolling)
                Assert.assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                Assert.assertEquals(Depth.NETWORK, request.depth)
                Assert.assertFalse(request.isDepthFromPauseLoadWhenScrolling)

                runBlocking {
                    interceptor.intercept(chain)
                }
                Assert.assertEquals(Depth.NETWORK, chain.finalRequest.depth)
                Assert.assertFalse(chain.finalRequest.isDepthFromPauseLoadWhenScrolling)
            }

            // isPauseLoadWhenScrolling false
            interceptor.enabled = true
            PauseLoadWhenScrollingDisplayInterceptor.scrolling = true
            DisplayRequest(context, "http://sample.com/sample.jpeg").let { request ->
                val chain =
                    TestRequestInterceptorChain(
                        sketch,
                        request,
                        request,
                        request.toRequestContext()
                    )

                Assert.assertTrue(interceptor.enabled)
                Assert.assertTrue(PauseLoadWhenScrollingDisplayInterceptor.scrolling)
                Assert.assertFalse(request.isPauseLoadWhenScrolling)
                Assert.assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                Assert.assertEquals(Depth.NETWORK, request.depth)
                Assert.assertFalse(request.isDepthFromPauseLoadWhenScrolling)

                runBlocking {
                    interceptor.intercept(chain)
                }
                Assert.assertEquals(Depth.NETWORK, chain.finalRequest.depth)
                Assert.assertFalse(chain.finalRequest.isDepthFromPauseLoadWhenScrolling)
            }

            // isIgnoredPauseLoadWhenScrolling true
            interceptor.enabled = true
            PauseLoadWhenScrollingDisplayInterceptor.scrolling = true
            DisplayRequest(context, "http://sample.com/sample.jpeg") {
                pauseLoadWhenScrolling()
                ignorePauseLoadWhenScrolling()
            }.let { request ->
                val chain =
                    TestRequestInterceptorChain(
                        sketch,
                        request,
                        request,
                        request.toRequestContext()
                    )

                Assert.assertTrue(interceptor.enabled)
                Assert.assertTrue(PauseLoadWhenScrollingDisplayInterceptor.scrolling)
                Assert.assertTrue(request.isPauseLoadWhenScrolling)
                Assert.assertTrue(request.isIgnoredPauseLoadWhenScrolling)
                Assert.assertEquals(Depth.NETWORK, request.depth)
                Assert.assertFalse(request.isDepthFromPauseLoadWhenScrolling)

                runBlocking {
                    interceptor.intercept(chain)
                }
                Assert.assertEquals(Depth.NETWORK, chain.finalRequest.depth)
                Assert.assertFalse(chain.finalRequest.isDepthFromPauseLoadWhenScrolling)
            }

            // depth MEMORY
            interceptor.enabled = true
            PauseLoadWhenScrollingDisplayInterceptor.scrolling = true
            DisplayRequest(context, "http://sample.com/sample.jpeg") {
                pauseLoadWhenScrolling()
                depth(MEMORY)
            }.let { request ->
                val chain =
                    TestRequestInterceptorChain(
                        sketch,
                        request,
                        request,
                        request.toRequestContext()
                    )

                Assert.assertTrue(interceptor.enabled)
                Assert.assertTrue(PauseLoadWhenScrollingDisplayInterceptor.scrolling)
                Assert.assertTrue(request.isPauseLoadWhenScrolling)
                Assert.assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                Assert.assertEquals(MEMORY, request.depth)
                Assert.assertFalse(request.isDepthFromPauseLoadWhenScrolling)

                runBlocking {
                    interceptor.intercept(chain)
                }
                Assert.assertEquals(MEMORY, chain.finalRequest.depth)
                Assert.assertFalse(chain.finalRequest.isDepthFromPauseLoadWhenScrolling)
            }

            // restore
            interceptor.enabled = true
            PauseLoadWhenScrollingDisplayInterceptor.scrolling = true
            DisplayRequest(context, "http://sample.com/sample.jpeg") {
                pauseLoadWhenScrolling()
            }.let { request ->
                Assert.assertTrue(interceptor.enabled)
                Assert.assertTrue(PauseLoadWhenScrollingDisplayInterceptor.scrolling)
                Assert.assertTrue(request.isPauseLoadWhenScrolling)
                Assert.assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                Assert.assertEquals(Depth.NETWORK, request.depth)
                Assert.assertFalse(request.isDepthFromPauseLoadWhenScrolling)

                val chain =
                    TestRequestInterceptorChain(
                        sketch,
                        request,
                        request,
                        request.toRequestContext()
                    )
                runBlocking {
                    interceptor.intercept(chain)
                }
                Assert.assertEquals(MEMORY, chain.finalRequest.depth)
                Assert.assertTrue(chain.finalRequest.isDepthFromPauseLoadWhenScrolling)

                interceptor.enabled = false
                val chain1 =
                    TestRequestInterceptorChain(
                        sketch,
                        chain.finalRequest,
                        chain.finalRequest,
                        chain.finalRequest.toRequestContext()
                    )
                runBlocking {
                    interceptor.intercept(chain1)
                }
                Assert.assertEquals(Depth.NETWORK, chain1.finalRequest.depth)
                Assert.assertFalse(chain1.finalRequest.isDepthFromPauseLoadWhenScrolling)
            }
        } finally {
            PauseLoadWhenScrollingDisplayInterceptor.scrolling = false
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = PauseLoadWhenScrollingDisplayInterceptor()
        val element11 = PauseLoadWhenScrollingDisplayInterceptor()
        val element2 = PauseLoadWhenScrollingDisplayInterceptor().apply { enabled = false }

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        PauseLoadWhenScrollingDisplayInterceptor().apply {
            Assert.assertEquals("PauseLoadWhenScrollingDisplayInterceptor($enabled)", toString())
        }
    }

    class TestRequestInterceptorChain(
        override val sketch: Sketch,
        override val initialRequest: ImageRequest,
        override val request: ImageRequest,
        override val requestContext: RequestContext,
    ) : RequestInterceptor.Chain {

        var finalRequest = request

        @MainThread
        override suspend fun proceed(request: ImageRequest): ImageData {
            finalRequest = request
            return DisplayData(
                drawable = ColorDrawable(Color.BLUE),
                imageInfo = ImageInfo(100, 100, "image/xml", 0),
                dataFrom = LOCAL,
                transformedList = null,
                extras = null,
            )
        }
    }
}