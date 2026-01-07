package com.github.panpf.sketch.core.common.test.request.internal

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.PlaceholderRequestInterceptor
import com.github.panpf.sketch.request.internal.RequestInterceptorChain
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.ErrorStateImage
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.FakeRequestInterceptor
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.TestTarget
import com.github.panpf.sketch.test.utils.current
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

class PlaceholderRequestInterceptorTest {

    @Test
    fun testIntercept() = runTest {
        if (Platform.current == Platform.iOS) {
            // Will get stuck forever in iOS test environment.
            return@runTest
        }
        val (context, sketch) = getTestContextAndSketch()

        val requestInterceptorList = listOf(
            PlaceholderRequestInterceptor(),
            FakeRequestInterceptor()
        )
        val executeRequest: suspend (ImageRequest) -> ImageData = { request ->
            withContext(Dispatchers.Main) {
                RequestInterceptorChain(
                    requestContext = request.toRequestContext(sketch),
                    interceptors = requestInterceptorList,
                    index = 0,
                ).proceed(request)
            }.getOrThrow()
        }

        val target1 = TestTarget()
        assertNull(target1.startImage)
        val request1 = ImageRequest(context, ResourceImages.jpeg.uri) {
            target(target1)
        }
        assertNotNull(executeRequest(request1))
        assertNull(target1.startImage)

        val target2 = TestTarget()
        assertNull(target2.startImage)
        val request2 = ImageRequest(context, ResourceImages.jpeg.uri) {
            target(target2)
            placeholder(ErrorStateImage())
        }
        assertNotNull(executeRequest(request2))
        assertNull(target2.startImage)

        val target3 = TestTarget()
        assertNull(target3.startImage)
        val request3 = ImageRequest(context, ResourceImages.jpeg.uri) {
            target(target3)
            placeholder(FakeStateImage())
        }
        assertNotNull(executeRequest(request3))
        assertNotNull(target3.startImage)
        assertTrue(message = "${target3.startImage}", actual = target3.startImage is FakeImage)
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = PlaceholderRequestInterceptor()
        val element11 = PlaceholderRequestInterceptor()
        val element2 = PlaceholderRequestInterceptor()

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
            expected = 93,
            actual = PlaceholderRequestInterceptor().sortWeight
        )
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "PlaceholderRequestInterceptor(sortWeight=93)",
            actual = PlaceholderRequestInterceptor().toString()
        )
    }
}