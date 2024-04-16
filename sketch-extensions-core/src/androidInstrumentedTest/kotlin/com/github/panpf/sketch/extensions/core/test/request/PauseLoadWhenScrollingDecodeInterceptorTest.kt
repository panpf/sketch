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
package com.github.panpf.sketch.extensions.core.test.request

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.annotation.MainThread
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDecodeInterceptor
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.request.ignorePauseLoadWhenScrolling
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.isIgnoredPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isPauseLoadWhenScrolling
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PauseLoadWhenScrollingDecodeInterceptorTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val interceptor = PauseLoadWhenScrollingDecodeInterceptor()

        try {
            ImageRequest(context, newResourceUri(android.R.drawable.ic_delete)).let { request ->
                Assert.assertTrue(interceptor.enabled)
                Assert.assertFalse(PauseLoadWhenScrollingDecodeInterceptor.scrolling)
                Assert.assertFalse(request.isPauseLoadWhenScrolling)
                Assert.assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                runBlocking {
                    val job = async {
                        interceptor.intercept(request.toDrawableDecodeInterceptorChain(sketch))
                    }
                    delay(1000)
                    Assert.assertTrue(job.isCompleted)
                }
            }

            ImageRequest(context, newResourceUri(android.R.drawable.ic_delete)).let { request ->
                PauseLoadWhenScrollingDecodeInterceptor.scrolling = true
                Assert.assertTrue(interceptor.enabled)
                Assert.assertTrue(PauseLoadWhenScrollingDecodeInterceptor.scrolling)
                Assert.assertFalse(request.isPauseLoadWhenScrolling)
                Assert.assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                runBlocking {
                    val job = async {
                        interceptor.intercept(request.toDrawableDecodeInterceptorChain(sketch))
                    }
                    delay(1000)
                    Assert.assertTrue(job.isCompleted)
                }
            }

            ImageRequest(context, newResourceUri(android.R.drawable.ic_delete)) {
                pauseLoadWhenScrolling()
            }.let { request ->
                PauseLoadWhenScrollingDecodeInterceptor.scrolling = true
                Assert.assertTrue(interceptor.enabled)
                Assert.assertTrue(PauseLoadWhenScrollingDecodeInterceptor.scrolling)
                Assert.assertTrue(request.isPauseLoadWhenScrolling)
                Assert.assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                runBlocking {
                    val job = async {
                        interceptor.intercept(request.toDrawableDecodeInterceptorChain(sketch))
                    }
                    delay(1000)
                    Assert.assertFalse(job.isCompleted)
                    PauseLoadWhenScrollingDecodeInterceptor.scrolling = false
                    delay(1000)
                    Assert.assertTrue(job.isCompleted)
                }
            }

            ImageRequest(context, newResourceUri(android.R.drawable.ic_delete)) {
                pauseLoadWhenScrolling()
                ignorePauseLoadWhenScrolling()
            }.let { request ->
                PauseLoadWhenScrollingDecodeInterceptor.scrolling = true
                Assert.assertTrue(interceptor.enabled)
                Assert.assertTrue(PauseLoadWhenScrollingDecodeInterceptor.scrolling)
                Assert.assertTrue(request.isPauseLoadWhenScrolling)
                Assert.assertTrue(request.isIgnoredPauseLoadWhenScrolling)
                runBlocking {
                    val job = async {
                        interceptor.intercept(request.toDrawableDecodeInterceptorChain(sketch))
                    }
                    delay(1000)
                    Assert.assertTrue(job.isCompleted)
                }
            }
        } finally {
            PauseLoadWhenScrollingDecodeInterceptor.scrolling = false
        }
    }

    @Test
    fun testSortWeight() {
        PauseLoadWhenScrollingDecodeInterceptor().apply {
            Assert.assertEquals(0, sortWeight)
        }

        PauseLoadWhenScrollingDecodeInterceptor(30).apply {
            Assert.assertEquals(30, sortWeight)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = PauseLoadWhenScrollingDecodeInterceptor()
        val element11 = PauseLoadWhenScrollingDecodeInterceptor().apply { enabled = false }
        val element2 = PauseLoadWhenScrollingDecodeInterceptor(30)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        PauseLoadWhenScrollingDecodeInterceptor().apply {
            Assert.assertEquals(
                "PauseLoadWhenScrollingDecodeInterceptor(sortWeight=0,enabled=true)",
                toString()
            )
        }

        PauseLoadWhenScrollingDecodeInterceptor(30).apply {
            enabled = false
            Assert.assertEquals(
                "PauseLoadWhenScrollingDecodeInterceptor(sortWeight=30,enabled=false)",
                toString()
            )
        }
    }

    private fun ImageRequest.toDrawableDecodeInterceptorChain(sketch: Sketch): DecodeInterceptor.Chain {
        return TestDecodeInterceptorChain(
            sketch = sketch,
            request = this,
            requestContext = this.toRequestContext(sketch),
            fetchResult = runBlocking {
                sketch.components.newFetcherOrThrow(this@toDrawableDecodeInterceptorChain).fetch()
                    .getOrThrow()
            }
        )
    }

    class TestDecodeInterceptorChain(
        override val sketch: Sketch,
        override val request: ImageRequest,
        override val requestContext: RequestContext,
        override val fetchResult: FetchResult?
    ) : DecodeInterceptor.Chain {

        private var finalRequest = request

        @MainThread
        override suspend fun proceed(): Result<DecodeResult> {
            finalRequest = request
            return Result.success(
                DecodeResult(
                    image = ColorDrawable(Color.BLUE).asSketchImage(),
                    imageInfo = ImageInfo(100, 100, "image/xml", 0),
                    dataFrom = LOCAL,
                    transformedList = null,
                    extras = null
                )
            )
        }
    }
}