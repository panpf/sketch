package com.github.panpf.sketch.core.common.test.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.github.panpf.sketch.test.utils.TestLifecycle
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.util.awaitStarted
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import com.github.panpf.sketch.util.removeAndAddObserver
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LifecyclesTest {

    @Suppress("DeferredResultUnused")
    @Test
    fun testAwaitStarted() {
        val lifecycle = TestLifecycle()
        assertEquals(Lifecycle.State.INITIALIZED, lifecycle.currentState)

        var started = false
        runTest {
            async(ioCoroutineDispatcher()) {
                lifecycle.awaitStarted()
                started = true
            }

            block(100)
            assertEquals(false, started)

            async(ioCoroutineDispatcher()) {
                lifecycle.currentState = Lifecycle.State.CREATED
            }
            block(100)
            assertEquals(false, started)

            async(ioCoroutineDispatcher()) {
                lifecycle.currentState = Lifecycle.State.STARTED
            }
            block(1000)
            assertEquals(true, started)
        }
    }

    @Test
    fun testRemoveAndAddObserver() {
        val lifecycle = TestLifecycle()
        val observer1 = LifecycleEventObserver { _, _ -> }
        val observer2 = LifecycleEventObserver { _, _ -> }
        assertEquals(expected = listOf(), actual = lifecycle.observers)
        lifecycle.addObserver(observer1)
        lifecycle.addObserver(observer2)
        assertEquals(expected = listOf(observer1, observer2), actual = lifecycle.observers)
        lifecycle.removeAndAddObserver(observer1)
        assertEquals(expected = listOf(observer2, observer1), actual = lifecycle.observers)
    }
}