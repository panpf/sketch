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
package com.github.panpf.sketch.test.request.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.EngineRequestInterceptor
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.internal.RequestInterceptorChain
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.TestHttpUriFetcher
import com.github.panpf.sketch.test.utils.TestRequest
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.test.utils.newSketch
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EngineRequestInterceptorTest {

    @Test
    fun testIntercept() {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val executeRequest: (ImageRequest) -> ImageData = { request ->
            runBlocking(Dispatchers.Main) {
                RequestInterceptorChain(
                    sketch = sketch,
                    initialRequest = request,
                    request = request,
                    requestContext = RequestContext(request),
                    interceptors = listOf(EngineRequestInterceptor()),
                    index = 0,
                ).proceed(request)
            }
        }

        executeRequest(DisplayRequest(context, TestAssets.SAMPLE_JPEG_URI)).asOrThrow<DisplayData>()

        executeRequest(LoadRequest(context, TestAssets.SAMPLE_JPEG_URI)).asOrThrow<LoadData>()

        executeRequest(DownloadRequest(context, TestHttpStack.testImages.first().uriString))
            .asOrThrow<DownloadData>()

        assertThrow(UnsupportedOperationException::class) {
            executeRequest(TestRequest(context, TestAssets.SAMPLE_JPEG_URI))
        }

        val sketch1 = newSketch {
            components {
                addFetcher(TestHttpUriFetcher.Factory())
            }
        }
        val executeRequest1: (ImageRequest) -> ImageData = { request ->
            runBlocking(Dispatchers.Main) {
                RequestInterceptorChain(
                    sketch = sketch1,
                    initialRequest = request,
                    request = request,
                    requestContext = RequestContext(request),
                    interceptors = listOf(EngineRequestInterceptor()),
                    index = 0,
                ).proceed(request)
            }
        }

        assertThrow(UnsupportedOperationException::class) {
            executeRequest1(DownloadRequest(context, TestHttpStack.testImages.first().uriString))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = EngineRequestInterceptor()
        val element11 = EngineRequestInterceptor()
        val element2 = EngineRequestInterceptor()

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertEquals(element1, element2)
        Assert.assertEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertEquals(element1.hashCode(), element2.hashCode())
        Assert.assertEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        Assert.assertEquals("EngineRequestInterceptor", EngineRequestInterceptor().toString())
    }
}