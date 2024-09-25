package com.github.panpf.sketch.core.common.test.request.internal

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.internal.PairProgressListener
import com.github.panpf.sketch.test.utils.ProgressListenerSupervisor
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PairProgressListenerTest {

    @Test
    fun test() {
        val listener1 = ProgressListenerSupervisor()
        val listener2 = ProgressListenerSupervisor()
        assertEquals(expected = listOf(), actual = listener1.callbackActionList)
        assertEquals(expected = listOf(), actual = listener2.callbackActionList)

        val context = getTestContext()
        val request = ImageRequest(context, "http://sample.com/sample.jpeg")
        val pairListener = PairProgressListener(listener1, listener2)

        pairListener.onUpdateProgress(request, Progress(1000, 100))
        assertEquals(expected = listOf("100"), actual = listener1.callbackActionList)
        assertEquals(expected = listOf("100"), actual = listener2.callbackActionList)

        pairListener.onUpdateProgress(request, Progress(1000, 500))
        assertEquals(expected = listOf("100", "500"), actual = listener1.callbackActionList)
        assertEquals(expected = listOf("100", "500"), actual = listener2.callbackActionList)

        pairListener.onUpdateProgress(request, Progress(1000, 1000))
        assertEquals(expected = listOf("100", "500", "1000"), actual = listener1.callbackActionList)
        assertEquals(expected = listOf("100", "500", "1000"), actual = listener2.callbackActionList)
    }

    @Test
    fun testEqualsAndHashCode() {
        val listener1 = ProgressListenerSupervisor()
        val listener2 = ProgressListenerSupervisor()
        val listener3 = ProgressListenerSupervisor()
        val listener4 = ProgressListenerSupervisor()
        val element1 = PairProgressListener(listener1, listener2)
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
        val listener1 = ProgressListenerSupervisor()
        val listener2 = ProgressListenerSupervisor()
        assertEquals(
            expected = "PairProgressListener(first=$listener1, second=$listener2)",
            actual = PairProgressListener(listener1, listener2).toString()
        )
    }
}