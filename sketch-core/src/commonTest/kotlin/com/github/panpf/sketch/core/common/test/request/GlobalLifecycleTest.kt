package com.github.panpf.sketch.core.common.test.request

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.github.panpf.sketch.request.GlobalLifecycle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GlobalLifecycleTest {

    @Test
    fun test() {
        GlobalLifecycle.apply {
            assertEquals(expected = Lifecycle.State.RESUMED, actual = currentState)
            assertEquals(expected = "GlobalLifecycle", actual = toString())

            val observer = LifecycleEventObserver { owner, _ ->
                assertSame(expected = GlobalLifecycle.owner, actual = owner)
            }
            addObserver(observer)
            removeObserver(observer)
        }
    }
}