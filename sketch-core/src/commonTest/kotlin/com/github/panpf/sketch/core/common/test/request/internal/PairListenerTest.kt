package com.github.panpf.sketch.core.common.test.request.internal

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.internal.PairListener
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.ListenerSupervisor
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PairListenerTest {

    @Test
    fun test() {
        val listener1 = ListenerSupervisor()
        val listener2 = ListenerSupervisor()
        assertEquals(expected = listOf(), actual = listener1.callbackActionList)
        assertEquals(expected = listOf(), actual = listener2.callbackActionList)

        val context = getTestContext()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val pairListener = PairListener(listener1, listener2)

        pairListener.onStart(request)
        assertEquals(expected = listOf("onStart"), actual = listener1.callbackActionList)
        assertEquals(expected = listOf("onStart"), actual = listener2.callbackActionList)

        pairListener.onCancel(request)
        assertEquals(
            expected = listOf("onStart", "onCancel"),
            actual = listener1.callbackActionList
        )
        assertEquals(
            expected = listOf("onStart", "onCancel"),
            actual = listener2.callbackActionList
        )

        val successResult = ImageResult.Success(
            request = request,
            image = FakeImage(100, 100),
            cacheKey = "cacheKey",
            imageInfo = ImageInfo(100, 100, "image/jpeg"),
            dataFrom = DataFrom.LOCAL,
            resize = Resize(200, 200),
            transformeds = null,
            extras = null
        )
        pairListener.onSuccess(request, successResult)
        assertEquals(
            expected = listOf("onStart", "onCancel", "onSuccess"),
            actual = listener1.callbackActionList
        )
        assertEquals(
            expected = listOf("onStart", "onCancel", "onSuccess"),
            actual = listener2.callbackActionList
        )

        val errorResult = ImageResult.Error(
            request = request,
            image = FakeImage(100, 100),
            throwable = Exception("test")
        )
        pairListener.onError(request, errorResult)
        assertEquals(
            expected = listOf("onStart", "onCancel", "onSuccess", "onError"),
            actual = listener1.callbackActionList
        )
        assertEquals(
            expected = listOf("onStart", "onCancel", "onSuccess", "onError"),
            actual = listener2.callbackActionList
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val listener1 = ListenerSupervisor()
        val listener2 = ListenerSupervisor()
        val listener3 = ListenerSupervisor()
        val listener4 = ListenerSupervisor()
        val element1 = PairListener(listener1, listener2)
        val element11 = element1.copy()
        val element2 = element1.copy(first = listener3)
        val element3 = element1.copy(second = listener4)

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = element2)
        assertNotEquals(illegal = element1, actual = element3)
        assertNotEquals(illegal = element2, actual = element3)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element2.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element3.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element3.hashCode())
    }

    @Test
    fun testToString() {
        val listener1 = ListenerSupervisor()
        val listener2 = ListenerSupervisor()
        assertEquals(
            expected = "PairListener(first=$listener1, second=$listener2)",
            actual = PairListener(listener1, listener2).toString()
        )
    }
}