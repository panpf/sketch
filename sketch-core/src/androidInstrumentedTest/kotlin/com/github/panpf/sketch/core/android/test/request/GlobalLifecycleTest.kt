package com.github.panpf.sketch.core.android.test.request

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.GlobalLifecycle
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GlobalLifecycleTest {

    @Test
    fun test() {
        GlobalLifecycle.apply {
            Assert.assertEquals(Lifecycle.State.RESUMED, currentState)
            Assert.assertEquals("GlobalLifecycle", toString())

            val observer = LifecycleEventObserver { owner, _ ->
                Assert.assertSame(GlobalLifecycle.owner, owner)
            }
            addObserver(observer)
            removeObserver(observer)
        }
    }
}