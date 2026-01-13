package com.github.panpf.sketch.core.common.test.fetch.internal

import com.github.panpf.sketch.fetch.internal.FetcherInterceptor
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.InterceptorChain
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeInterceptor
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

class FetcherInterceptorTest {

    @Test
    fun testIntercept() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val requestContext = request.toRequestContext(sketch)
        val chain = InterceptorChain(
            requestContext = requestContext,
            interceptors = listOf(FetcherInterceptor(), FakeInterceptor()),
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
        assertEquals(
            expected = 90,
            actual = FetcherInterceptor().sortWeight
        )
        assertEquals(
            expected = 90,
            actual = FetcherInterceptor.SORT_WEIGHT
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val ele1 = FetcherInterceptor()
        val ele11 = FetcherInterceptor()

        assertEquals(ele1, ele11)
        assertNotEquals(ele1, Any())
        assertNotEquals(ele1, null as Any?)

        assertEquals(ele1.hashCode(), ele11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            "FetcherInterceptor",
            FetcherInterceptor().toString()
        )
    }
}