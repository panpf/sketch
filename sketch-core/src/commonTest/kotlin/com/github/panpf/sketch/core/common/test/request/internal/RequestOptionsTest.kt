package com.github.panpf.sketch.core.common.test.request.internal

import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.internal.RequestOptions
import com.github.panpf.sketch.test.utils.ListenerSupervisor
import com.github.panpf.sketch.test.utils.ProgressListenerSupervisor
import com.github.panpf.sketch.test.utils.TestLifecycle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RequestOptionsTest {

    @Test
    fun testBuilder() {
        RequestOptions.Builder().build().apply {
            assertEquals(expected = null, actual = listener)
            assertEquals(expected = null, actual = progressListener)
            assertEquals(expected = null, actual = lifecycleResolver)
        }

        val listener1 = ListenerSupervisor()
        val progressListener1 = ProgressListenerSupervisor()
        val lifecycle1 = LifecycleResolver(TestLifecycle())
        RequestOptions.Builder().apply {
            addListener(listener1)
            addProgressListener(progressListener1)
            lifecycle(lifecycle1)
        }.build().apply {
            assertEquals(expected = listener1, actual = listener)
            assertEquals(expected = progressListener1, actual = progressListener)
            assertEquals(expected = lifecycle1, actual = lifecycleResolver)
        }

        RequestOptions.Builder().apply {
            addListener(listener1)
            addProgressListener(progressListener1)
            lifecycle(lifecycle1)
            removeListener(listener1)
            removeProgressListener(progressListener1)
            lifecycle(null)
        }.build().apply {
            assertEquals(expected = null, actual = listener)
            assertEquals(expected = null, actual = progressListener)
            assertEquals(expected = null, actual = lifecycleResolver)
        }
    }

    @Test
    fun testNewBuilder() {
        val listener1 = ListenerSupervisor()
        val progressListener1 = ProgressListenerSupervisor()
        val lifecycle1 = LifecycleResolver(TestLifecycle())
        RequestOptions.Builder().build().apply {
            assertEquals(expected = null, actual = listener)
            assertEquals(expected = null, actual = progressListener)
            assertEquals(expected = null, actual = lifecycleResolver)
        }.newBuilder().build().apply {
            assertEquals(expected = null, actual = listener)
            assertEquals(expected = null, actual = progressListener)
            assertEquals(expected = null, actual = lifecycleResolver)
        }.newBuilder().apply {
            addListener(listener1)
            addProgressListener(progressListener1)
            lifecycle(lifecycle1)
        }.build().apply {
            assertEquals(expected = listener1, actual = listener)
            assertEquals(expected = progressListener1, actual = progressListener)
            assertEquals(expected = lifecycle1, actual = lifecycleResolver)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val listener1 = ListenerSupervisor()
        val progressListener1 = ProgressListenerSupervisor()
        val lifecycle1 = LifecycleResolver(TestLifecycle())
        val listener2 = ListenerSupervisor()
        val progressListener2 = ProgressListenerSupervisor()
        val lifecycle2 = LifecycleResolver(TestLifecycle())
        val element1 = RequestOptions(listener1, progressListener1, lifecycle1)
        val element11 = element1.copy()
        val element2 = element1.copy(listener = listener2)
        val element3 = element1.copy(progressListener = progressListener2)
        val element4 = element1.copy(lifecycleResolver = lifecycle2)

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = element2)
        assertNotEquals(illegal = element1, actual = element3)
        assertNotEquals(illegal = element1, actual = element4)
        assertNotEquals(illegal = element2, actual = element3)
        assertNotEquals(illegal = element2, actual = element4)
        assertNotEquals(illegal = element3, actual = element4)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element2.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element3.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element4.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element3.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element4.hashCode())
        assertNotEquals(illegal = element3.hashCode(), actual = element4.hashCode())
    }

    @Test
    fun testToString() {
        val listener1 = ListenerSupervisor()
        val progressListener1 = ProgressListenerSupervisor()
        val lifecycle1 = LifecycleResolver(TestLifecycle())
        assertEquals(
            expected = "RequestOptions(listener=$listener1, progressListener=$progressListener1, lifecycleResolver=$lifecycle1)",
            actual = RequestOptions(listener1, progressListener1, lifecycle1).toString()
        )
    }
}