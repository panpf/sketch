package com.github.panpf.sketch.extensions.test.request

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.annotation.MainThread
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.SaveCellularTrafficDisplayInterceptor
import com.github.panpf.sketch.request.ignoreSaveCellularTraffic
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.isDepthFromSaveCellularTraffic
import com.github.panpf.sketch.request.isIgnoredSaveCellularTraffic
import com.github.panpf.sketch.request.isSaveCellularTraffic
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.sketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SaveCellularTrafficDisplayInterceptorTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val interceptor = SaveCellularTrafficDisplayInterceptor {
            true
        }
        val errorInterceptor = SaveCellularTrafficDisplayInterceptor {
            false
        }

        // default
        DisplayRequest(context, "http://sample.com/sample.jpeg").let { request ->
            val chain =
                TestRequestInterceptorChain(sketch, request, request, RequestContext(request))

            Assert.assertTrue(interceptor.enabled)
            Assert.assertFalse(request.isSaveCellularTraffic)
            Assert.assertFalse(request.isIgnoredSaveCellularTraffic)
            Assert.assertEquals(NETWORK, request.depth)
            Assert.assertFalse(request.isDepthFromSaveCellularTraffic)

            runBlocking {
                interceptor.intercept(chain)
            }
            Assert.assertEquals(NETWORK, chain.request.depth)
            Assert.assertFalse(chain.request.isDepthFromSaveCellularTraffic)
        }

        // success
        interceptor.enabled = true
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
        }.let { request ->
            val chain =
                TestRequestInterceptorChain(sketch, request, request, RequestContext(request))

            Assert.assertTrue(interceptor.enabled)
            Assert.assertTrue(request.isSaveCellularTraffic)
            Assert.assertFalse(request.isIgnoredSaveCellularTraffic)
            Assert.assertEquals(NETWORK, request.depth)
            Assert.assertFalse(request.isDepthFromSaveCellularTraffic)

            runBlocking {
                interceptor.intercept(chain)
            }
            Assert.assertEquals(Depth.LOCAL, chain.finalRequest.depth)
            Assert.assertTrue(chain.finalRequest.isDepthFromSaveCellularTraffic)
        }

        // Request type error
        interceptor.enabled = true
        LoadRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
        }.let { request ->
            val chain =
                TestRequestInterceptorChain(sketch, request, request, RequestContext(request))

            Assert.assertTrue(interceptor.enabled)
            Assert.assertTrue(request.isSaveCellularTraffic)
            Assert.assertFalse(request.isIgnoredSaveCellularTraffic)
            Assert.assertEquals(NETWORK, request.depth)
            Assert.assertFalse(request.isDepthFromSaveCellularTraffic)

            runBlocking {
                interceptor.intercept(chain)
            }
            Assert.assertEquals(NETWORK, chain.finalRequest.depth)
            Assert.assertFalse(chain.finalRequest.isDepthFromSaveCellularTraffic)
        }

        // enabled false
        interceptor.enabled = false
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
        }.let { request ->
            val chain =
                TestRequestInterceptorChain(sketch, request, request, RequestContext(request))

            Assert.assertFalse(interceptor.enabled)
            Assert.assertTrue(request.isSaveCellularTraffic)
            Assert.assertFalse(request.isIgnoredSaveCellularTraffic)
            Assert.assertEquals(NETWORK, request.depth)
            Assert.assertFalse(request.isDepthFromSaveCellularTraffic)

            runBlocking {
                interceptor.intercept(chain)
            }
            Assert.assertEquals(NETWORK, chain.finalRequest.depth)
            Assert.assertFalse(chain.finalRequest.isDepthFromSaveCellularTraffic)
        }

        // isSaveCellularTraffic false
        interceptor.enabled = true
        DisplayRequest(context, "http://sample.com/sample.jpeg").let { request ->
            val chain =
                TestRequestInterceptorChain(sketch, request, request, RequestContext(request))

            Assert.assertTrue(interceptor.enabled)
            Assert.assertFalse(request.isSaveCellularTraffic)
            Assert.assertFalse(request.isIgnoredSaveCellularTraffic)
            Assert.assertEquals(NETWORK, request.depth)
            Assert.assertFalse(request.isDepthFromSaveCellularTraffic)

            runBlocking {
                interceptor.intercept(chain)
            }
            Assert.assertEquals(NETWORK, chain.finalRequest.depth)
            Assert.assertFalse(chain.finalRequest.isDepthFromSaveCellularTraffic)
        }

        // isIgnoredSaveCellularTraffic true
        interceptor.enabled = true
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
            ignoreSaveCellularTraffic()
        }.let { request ->
            val chain =
                TestRequestInterceptorChain(sketch, request, request, RequestContext(request))

            Assert.assertTrue(interceptor.enabled)
            Assert.assertTrue(request.isSaveCellularTraffic)
            Assert.assertTrue(request.isIgnoredSaveCellularTraffic)
            Assert.assertEquals(NETWORK, request.depth)
            Assert.assertFalse(request.isDepthFromSaveCellularTraffic)

            runBlocking {
                interceptor.intercept(chain)
            }
            Assert.assertEquals(NETWORK, chain.finalRequest.depth)
            Assert.assertFalse(chain.finalRequest.isDepthFromSaveCellularTraffic)
        }

        // isCellularNetworkConnected false
        errorInterceptor.enabled = true
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
        }.let { request ->
            val chain =
                TestRequestInterceptorChain(sketch, request, request, RequestContext(request))

            Assert.assertTrue(errorInterceptor.enabled)
            Assert.assertTrue(request.isSaveCellularTraffic)
            Assert.assertFalse(request.isIgnoredSaveCellularTraffic)
            Assert.assertEquals(NETWORK, request.depth)
            Assert.assertFalse(request.isDepthFromSaveCellularTraffic)

            runBlocking {
                errorInterceptor.intercept(chain)
            }
            Assert.assertEquals(NETWORK, chain.finalRequest.depth)
            Assert.assertFalse(chain.finalRequest.isDepthFromSaveCellularTraffic)
        }

        // depth MEMORY
        interceptor.enabled = true
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic()
            depth(MEMORY)
        }.let { request ->
            val chain =
                TestRequestInterceptorChain(sketch, request, request, RequestContext(request))

            Assert.assertTrue(interceptor.enabled)
            Assert.assertTrue(request.isSaveCellularTraffic)
            Assert.assertFalse(request.isIgnoredSaveCellularTraffic)
            Assert.assertEquals(MEMORY, request.depth)
            Assert.assertFalse(request.isDepthFromSaveCellularTraffic)

            runBlocking {
                interceptor.intercept(chain)
            }
            Assert.assertEquals(Depth.LOCAL, chain.finalRequest.depth)
            Assert.assertTrue(chain.finalRequest.isDepthFromSaveCellularTraffic)
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
                ColorDrawable(Color.BLUE),
                ImageInfo(100, 100, "image/xml", 0),
                LOCAL,
                null
            )
        }
    }
}