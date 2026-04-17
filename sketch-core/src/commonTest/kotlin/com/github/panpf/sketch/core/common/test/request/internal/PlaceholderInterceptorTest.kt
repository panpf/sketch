package com.github.panpf.sketch.core.common.test.request.internal

import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.InterceptorChain
import com.github.panpf.sketch.request.internal.PlaceholderInterceptor
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.ErrorStateImage
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.FakeInterceptor
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.TestTarget
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlaceholderInterceptorTest {

    @Test
    fun testIntercept() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val interceptors = listOf(
            PlaceholderInterceptor(),
            FakeInterceptor()
        )
        val executeRequest: suspend (ImageRequest) -> ImageData = { request ->
            withContext(Dispatchers.Main) {
                InterceptorChain(
                    requestContext = request.toRequestContext(sketch),
                    interceptors = interceptors,
                    index = 0,
                ).proceed(request)
            }.getOrThrow()
        }

        val target1 = TestTarget()
        assertNull(target1.startImage)
        val request1 = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            target(target1)
        }
        assertNotNull(executeRequest(request1))
        assertNull(target1.startImage)

        val target2 = TestTarget()
        assertNull(target2.startImage)
        val request2 = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            target(target2)
            placeholder(ErrorStateImage())
        }
        assertNotNull(executeRequest(request2))
        assertNull(target2.startImage)

        val target3 = TestTarget()
        assertNull(target3.startImage)
        val request3 = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            target(target3)
            placeholder(FakeStateImage())
        }
        assertNotNull(executeRequest(request3))
        assertNotNull(target3.startImage)
        assertTrue(message = "${target3.startImage}", actual = target3.startImage is FakeImage)
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = PlaceholderInterceptor()
        val element11 = PlaceholderInterceptor()
        val element2 = PlaceholderInterceptor()

        assertNotSame(illegal = element1, actual = element11)
        assertNotSame(illegal = element1, actual = element2)
        assertNotSame(illegal = element2, actual = element11)

        assertEquals(expected = element1, actual = element11)
        assertEquals(expected = element1, actual = element2)
        assertEquals(expected = element2, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertEquals(expected = element1.hashCode(), actual = element2.hashCode())
        assertEquals(expected = element2.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testSortWeight() {
        assertEquals(
            expected = 30,
            actual = PlaceholderInterceptor().sortWeight
        )
        assertEquals(
            expected = 30,
            actual = PlaceholderInterceptor.SORT_WEIGHT
        )
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "PlaceholderInterceptor",
            actual = PlaceholderInterceptor().toString()
        )
    }
}