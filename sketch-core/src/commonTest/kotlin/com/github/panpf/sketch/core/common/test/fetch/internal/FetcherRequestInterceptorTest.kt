package com.github.panpf.sketch.core.common.test.fetch.internal

import com.github.panpf.sketch.fetch.internal.FetcherRequestInterceptor
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestInterceptorChain
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeRequestInterceptor
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FetcherRequestInterceptorTest {

    @Test
    fun testIntercept() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val requestContext = request.toRequestContext(sketch)
        val chain = RequestInterceptorChain(
            requestContext = requestContext,
            interceptors = listOf(FetcherRequestInterceptor(), FakeRequestInterceptor()),
            index = 0
        )

        assertNull(requestContext.fetchResult)

        // runCatching
        runBlock {
            val result = withContext(Dispatchers.Main) {
                chain.proceed(request.newRequest("error://test.jpeg"))
            }
            assertFalse(result.isSuccess)
            assertNull(requestContext.fetchResult)
        }

        // normal fetch
        runBlock {
            val result = withContext(Dispatchers.Main) {
                chain.proceed(request)
            }
            assertTrue(result.isSuccess)
            assertNotNull(requestContext.fetchResult)
        }

        // if (requestContext.fetchResult == null)
        runBlock {
            val result = withContext(Dispatchers.Main) {
                chain.proceed(request.newRequest("error://test.jpeg"))
            }
            assertTrue(result.isSuccess)
            assertNotNull(requestContext.fetchResult)
        }
    }

    @Test
    fun testSortWeight() {
        FetcherRequestInterceptor().apply {
            assertEquals(99, sortWeight)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val ele1 = FetcherRequestInterceptor()
        val ele11 = FetcherRequestInterceptor()

        assertEquals(ele1, ele11)
        assertNotEquals(ele1, Any())
        assertNotEquals(ele1, null as Any?)

        assertEquals(ele1.hashCode(), ele11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            "FetcherRequestInterceptor(sortWeight=99)",
            FetcherRequestInterceptor().toString()
        )
    }
}