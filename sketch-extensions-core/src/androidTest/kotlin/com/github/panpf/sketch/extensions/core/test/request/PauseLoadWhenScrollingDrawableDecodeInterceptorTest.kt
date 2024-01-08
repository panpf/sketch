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
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.DrawableDecodeInterceptor
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.PauseLoadWhenScrollingDrawableDecodeInterceptor
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
class PauseLoadWhenScrollingDrawableDecodeInterceptorTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val interceptor = PauseLoadWhenScrollingDrawableDecodeInterceptor()

        try {
            ImageRequest(context, newResourceUri(android.R.drawable.ic_delete)).let { request ->
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

            ImageRequest(context, newResourceUri(android.R.drawable.ic_delete)).let { request ->
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

            ImageRequest(context, newResourceUri(android.R.drawable.ic_delete)) {
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

            ImageRequest(context, newResourceUri(android.R.drawable.ic_delete)) {
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

    @Test
    fun testSortWeight() {
        PauseLoadWhenScrollingDrawableDecodeInterceptor().apply {
            Assert.assertEquals(0, sortWeight)
        }

        PauseLoadWhenScrollingDrawableDecodeInterceptor(30).apply {
            Assert.assertEquals(30, sortWeight)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = PauseLoadWhenScrollingDrawableDecodeInterceptor()
        val element11 = PauseLoadWhenScrollingDrawableDecodeInterceptor().apply { enabled = false }
        val element2 = PauseLoadWhenScrollingDrawableDecodeInterceptor(30)

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
        PauseLoadWhenScrollingDrawableDecodeInterceptor().apply {
            Assert.assertEquals(
                "PauseLoadWhenScrollingDrawableDecodeInterceptor(sortWeight=0,enabled=true)",
                toString()
            )
        }

        PauseLoadWhenScrollingDrawableDecodeInterceptor(30).apply {
            enabled = false
            Assert.assertEquals(
                "PauseLoadWhenScrollingDrawableDecodeInterceptor(sortWeight=30,enabled=false)",
                toString()
            )
        }
    }

    private fun ImageRequest.toDrawableDecodeInterceptorChain(sketch: Sketch): DrawableDecodeInterceptor.Chain {
        return TestDrawableDecodeInterceptorChain(
            sketch = sketch,
            request = this,
            requestContext = this.toRequestContext(sketch),
            fetchResult = runBlocking {
                sketch.components.newFetcherOrThrow(this@toDrawableDecodeInterceptorChain).fetch()
                    .getOrThrow()
            }
        )
    }

    class TestDrawableDecodeInterceptorChain(
        override val sketch: Sketch,
        override val request: ImageRequest,
        override val requestContext: RequestContext,
        override val fetchResult: FetchResult?
    ) : DrawableDecodeInterceptor.Chain {

        private var finalRequest = request

        @MainThread
        override suspend fun proceed(): Result<DrawableDecodeResult> {
            finalRequest = request
            return Result.success(
                DrawableDecodeResult(
                    drawable = ColorDrawable(Color.BLUE),
                    imageInfo = ImageInfo(100, 100, "image/xml", 0),
                    dataFrom = LOCAL,
                    transformedList = null,
                    extras = null
                )
            )
        }
    }
}