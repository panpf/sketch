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
import com.github.panpf.sketch.decode.DrawableDecodeInterceptor
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.extensions.test.toRequestContext
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDrawableDecodeInterceptor
import com.github.panpf.sketch.request.ignorePauseLoadWhenScrolling
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.isIgnoredPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isPauseLoadWhenScrolling
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.sketch
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PauseLoadWhenScrollingDrawableDecodeInterceptorTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val interceptor = PauseLoadWhenScrollingDrawableDecodeInterceptor()

        try {
            DisplayRequest(context, newResourceUri(android.R.drawable.ic_delete)).let { request ->
                Assert.assertTrue(interceptor.enabled)
                Assert.assertFalse(PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling)
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

            DisplayRequest(context, newResourceUri(android.R.drawable.ic_delete)).let { request ->
                PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling = true
                Assert.assertTrue(interceptor.enabled)
                Assert.assertTrue(PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling)
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

            DisplayRequest(context, newResourceUri(android.R.drawable.ic_delete)) {
                pauseLoadWhenScrolling()
            }.let { request ->
                PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling = true
                Assert.assertTrue(interceptor.enabled)
                Assert.assertTrue(PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling)
                Assert.assertTrue(request.isPauseLoadWhenScrolling)
                Assert.assertFalse(request.isIgnoredPauseLoadWhenScrolling)
                runBlocking {
                    val job = async {
                        interceptor.intercept(request.toDrawableDecodeInterceptorChain(sketch))
                    }
                    delay(1000)
                    Assert.assertFalse(job.isCompleted)
                    PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling = false
                    delay(1000)
                    Assert.assertTrue(job.isCompleted)
                }
            }

            DisplayRequest(context, newResourceUri(android.R.drawable.ic_delete)) {
                pauseLoadWhenScrolling()
                ignorePauseLoadWhenScrolling()
            }.let { request ->
                PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling = true
                Assert.assertTrue(interceptor.enabled)
                Assert.assertTrue(PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling)
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
            PauseLoadWhenScrollingDrawableDecodeInterceptor.scrolling = false
        }
    }

    private fun DisplayRequest.toDrawableDecodeInterceptorChain(sketch: Sketch): DrawableDecodeInterceptor.Chain {
        return TestDrawableDecodeInterceptorChain(
            sketch = sketch,
            request = this,
            requestContext = this.toRequestContext(),
            fetchResult = runBlocking {
                sketch.components.newFetcher(this@toDrawableDecodeInterceptorChain).fetch()
            }
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = PauseLoadWhenScrollingDrawableDecodeInterceptor()
        val element11 = PauseLoadWhenScrollingDrawableDecodeInterceptor()
        val element2 = PauseLoadWhenScrollingDrawableDecodeInterceptor().apply { enabled = false }

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
        PauseLoadWhenScrollingDrawableDecodeInterceptor().apply {
            Assert.assertEquals(
                "PauseLoadWhenScrollingDrawableDecodeInterceptor($enabled)",
                toString()
            )
        }
    }

    class TestDrawableDecodeInterceptorChain(
        override val sketch: Sketch,
        override val request: ImageRequest,
        override val requestContext: RequestContext,
        override val fetchResult: FetchResult?
    ) : DrawableDecodeInterceptor.Chain {

        var finalRequest = request

        @MainThread
        override suspend fun proceed(): DrawableDecodeResult {
            finalRequest = request
            return DrawableDecodeResult(
                drawable = ColorDrawable(Color.BLUE),
                imageInfo = ImageInfo(100, 100, "image/xml", 0),
                dataFrom = LOCAL,
                transformedList = null,
                extras = null
            )
        }
    }
}