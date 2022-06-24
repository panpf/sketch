package com.github.panpf.sketch.test.request

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.isSketchGlobalLifecycle
import com.github.panpf.tools4j.test.ktx.assertThrow
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
                Assert.assertSame(GlobalLifecycle, owner.lifecycle)
            }
            addObserver(observer)
            removeObserver(observer)

            assertThrow(IllegalArgumentException::class) {
                addObserver(object : LifecycleObserver {})
            }

            Assert.assertTrue(isSketchGlobalLifecycle())
        }
    }
}