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

        // default
        DisplayRequest(context, "http://sample.com/sample.jpeg").let { request ->
            val chain =
                TestRequestInterceptorChain(sketch, request, request, RequestContext())

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
                TestRequestInterceptorChain(sketch, request, request, RequestContext())

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
                TestRequestInterceptorChain(sketch, request, request, RequestContext())

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
                TestRequestInterceptorChain(sketch, request, request, RequestContext())

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
            val chain =
                TestRequestInterceptorChain(sketch, request, request, RequestContext())

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
                TestRequestInterceptorChain(sketch, request, request, RequestContext())

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
                TestRequestInterceptorChain(sketch, request, request, RequestContext())

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
                TestRequestInterceptorChain(sketch, request, request, RequestContext())

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
            return DisplayData(ColorDrawable(Color.BLUE), ImageInfo(100, 100, "image/xml"), LOCAL)
        }
    }
}