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
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.SaveCellularTrafficRequestInterceptor
import com.github.panpf.sketch.request.ignoreSaveCellularTraffic
import com.github.panpf.sketch.request.isDepthFromSaveCellularTraffic
import com.github.panpf.sketch.request.isIgnoredSaveCellularTraffic
import com.github.panpf.sketch.request.isSaveCellularTraffic
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.request.supportSaveCellularTraffic
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.SketchSize
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SaveCellularTrafficRequestInterceptorTest {

    @Test
    fun testSupportSaveCellularTraffic() {
        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportSaveCellularTraffic()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[]," +
                        "requestInterceptorList=[SaveCellularTrafficRequestInterceptor]," +
                        "decodeInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportSaveCellularTraffic()
            supportSaveCellularTraffic()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[]," +
                        "requestInterceptorList=[SaveCellularTrafficRequestInterceptor,SaveCellularTrafficRequestInterceptor]," +
                        "decodeInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun test() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val interceptor = SaveCellularTrafficRequestInterceptor {
            true
        }
        val errorInterceptor = SaveCellularTrafficRequestInterceptor {
            false
        }

        // default
        ImageRequest(context, "http://sample.com/sample.jpeg").let { request ->
            val chain = TestRequestInterceptorChain(
                sketch = sketch,
                initialRequest = request,
                request = request,
                requestContext = request.toRequestContext(sketch)
            )

            assertTrue(interceptor.enabled)
            assertFalse(request.isSaveCellularTraffic)
            assertFalse(request.isIgnoredSaveCellularTraffic)
            assertEquals(NETWORK, request.depthHolder.depth)
            assertFalse(request.isDepthFromSaveCellularTraffic)

            interceptor.intercept(chain)
            assertEquals(NETWORK, chain.request.depthHolder.depth)
            assertFalse(chain.request.isDepthFromSaveCellularTraffic)
        }

        // success
        interceptor.enabled = true
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
        }.let { request ->
            val chain = TestRequestInterceptorChain(
                sketch = sketch,
                initialRequest = request,
                request = request,
                requestContext = request.toRequestContext(sketch)
            )

            assertTrue(interceptor.enabled)
            assertTrue(request.isSaveCellularTraffic)
            assertFalse(request.isIgnoredSaveCellularTraffic)
            assertEquals(NETWORK, request.depthHolder.depth)
            assertFalse(request.isDepthFromSaveCellularTraffic)

            interceptor.intercept(chain)
            assertEquals(Depth.LOCAL, chain.finalRequest.depthHolder.depth)
            assertTrue(chain.finalRequest.isDepthFromSaveCellularTraffic)
        }

        // enabled false
        interceptor.enabled = false
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
        }.let { request ->
            val chain = TestRequestInterceptorChain(
                sketch = sketch,
                initialRequest = request,
                request = request,
                requestContext = request.toRequestContext(sketch)
            )

            assertFalse(interceptor.enabled)
            assertTrue(request.isSaveCellularTraffic)
            assertFalse(request.isIgnoredSaveCellularTraffic)
            assertEquals(NETWORK, request.depthHolder.depth)
            assertFalse(request.isDepthFromSaveCellularTraffic)

            interceptor.intercept(chain)
            assertEquals(NETWORK, chain.finalRequest.depthHolder.depth)
            assertFalse(chain.finalRequest.isDepthFromSaveCellularTraffic)
        }

        // isSaveCellularTraffic false
        interceptor.enabled = true
        ImageRequest(context, "http://sample.com/sample.jpeg").let { request ->
            val chain = TestRequestInterceptorChain(
                sketch = sketch,
                initialRequest = request,
                request = request,
                requestContext = request.toRequestContext(sketch)
            )

            assertTrue(interceptor.enabled)
            assertFalse(request.isSaveCellularTraffic)
            assertFalse(request.isIgnoredSaveCellularTraffic)
            assertEquals(NETWORK, request.depthHolder.depth)
            assertFalse(request.isDepthFromSaveCellularTraffic)

            interceptor.intercept(chain)
            assertEquals(NETWORK, chain.finalRequest.depthHolder.depth)
            assertFalse(chain.finalRequest.isDepthFromSaveCellularTraffic)
        }

        // isIgnoredSaveCellularTraffic true
        interceptor.enabled = true
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
            ignoreSaveCellularTraffic()
        }.let { request ->
            val chain = TestRequestInterceptorChain(
                sketch = sketch,
                initialRequest = request,
                request = request,
                requestContext = request.toRequestContext(sketch)
            )

            assertTrue(interceptor.enabled)
            assertTrue(request.isSaveCellularTraffic)
            assertTrue(request.isIgnoredSaveCellularTraffic)
            assertEquals(NETWORK, request.depthHolder.depth)
            assertFalse(request.isDepthFromSaveCellularTraffic)

            interceptor.intercept(chain)
            assertEquals(NETWORK, chain.finalRequest.depthHolder.depth)
            assertFalse(chain.finalRequest.isDepthFromSaveCellularTraffic)
        }

        // isCellularNetworkConnected false
        errorInterceptor.enabled = true
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
        }.let { request ->
            val chain = TestRequestInterceptorChain(
                sketch = sketch,
                initialRequest = request,
                request = request,
                requestContext = request.toRequestContext(sketch)
            )

            assertTrue(errorInterceptor.enabled)
            assertTrue(request.isSaveCellularTraffic)
            assertFalse(request.isIgnoredSaveCellularTraffic)
            assertEquals(NETWORK, request.depthHolder.depth)
            assertFalse(request.isDepthFromSaveCellularTraffic)

            errorInterceptor.intercept(chain)
            assertEquals(NETWORK, chain.finalRequest.depthHolder.depth)
            assertFalse(chain.finalRequest.isDepthFromSaveCellularTraffic)
        }

        // depth MEMORY
        interceptor.enabled = true
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
            depth(MEMORY)
        }.let { request ->
            val chain = TestRequestInterceptorChain(
                sketch = sketch,
                initialRequest = request,
                request = request,
                requestContext = request.toRequestContext(sketch)
            )

            assertTrue(interceptor.enabled)
            assertTrue(request.isSaveCellularTraffic)
            assertFalse(request.isIgnoredSaveCellularTraffic)
            assertEquals(MEMORY, request.depthHolder.depth)
            assertFalse(request.isDepthFromSaveCellularTraffic)

            interceptor.intercept(chain)
            assertEquals(Depth.LOCAL, chain.finalRequest.depthHolder.depth)
            assertTrue(chain.finalRequest.isDepthFromSaveCellularTraffic)
        }

        // restore
        interceptor.enabled = true
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
        }.let { request ->

            assertTrue(interceptor.enabled)
            assertTrue(request.isSaveCellularTraffic)
            assertFalse(request.isIgnoredSaveCellularTraffic)
            assertEquals(NETWORK, request.depthHolder.depth)
            assertFalse(request.isDepthFromSaveCellularTraffic)

            val chain = TestRequestInterceptorChain(
                sketch = sketch,
                initialRequest = request,
                request = request,
                requestContext = request.toRequestContext(sketch)
            )
            interceptor.intercept(chain)
            assertEquals(Depth.LOCAL, chain.finalRequest.depthHolder.depth)
            assertTrue(chain.finalRequest.isDepthFromSaveCellularTraffic)

            interceptor.enabled = false
            val chain1 = TestRequestInterceptorChain(
                sketch,
                chain.finalRequest,
                chain.finalRequest,
                chain.finalRequest.toRequestContext(sketch)
            )
            interceptor.intercept(chain1)
            assertEquals(NETWORK, chain1.finalRequest.depthHolder.depth)
            assertFalse(chain1.finalRequest.isDepthFromSaveCellularTraffic)
        }
    }

    @Test
    fun testSortWeight() {
        SaveCellularTrafficRequestInterceptor().apply {
            assertEquals(0, sortWeight)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = SaveCellularTrafficRequestInterceptor()
        val element11 = SaveCellularTrafficRequestInterceptor().apply { enabled = false }


        assertEquals(element1, element11)
        assertNotEquals(element1, null as SaveCellularTrafficRequestInterceptor?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        SaveCellularTrafficRequestInterceptor().apply {
            assertEquals(
                "SaveCellularTrafficRequestInterceptor",
                toString()
            )
        }
    }

    class TestRequestInterceptorChain(
        override val sketch: Sketch,
        override val initialRequest: ImageRequest,
        override val request: ImageRequest,
        override val requestContext: RequestContext,
    ) : RequestInterceptor.Chain {

        var finalRequest = request

        override suspend fun proceed(request: ImageRequest): Result<ImageData> {
            finalRequest = request
            return Result.success(
                ImageData(
                    image = FakeImage(SketchSize(100, 100)),
                    imageInfo = ImageInfo(100, 100, "image/xml"),
                    dataFrom = LOCAL,
                    resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
                    transformeds = null,
                    extras = null,
                )
            )
        }
    }
}