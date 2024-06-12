package com.github.panpf.sketch.core.android.test.lifecycle

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.lifecycle.GlobalPlatformLifecycle
import com.github.panpf.sketch.lifecycle.PlatformLifecycle
import com.github.panpf.sketch.lifecycle.PlatformLifecycleEventObserver
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GlobalPlatformLifecycleTest {

    @Test
    fun test() {
        GlobalPlatformLifecycle.apply {
            Assert.assertEquals(PlatformLifecycle.State.RESUMED, currentState)
            Assert.assertEquals("GlobalPlatformLifecycle", toString())

            val observer = PlatformLifecycleEventObserver { owner, _ ->
                Assert.assertSame(GlobalPlatformLifecycle.owner, owner)
            }
            addObserver(observer)
            removeObserver(observer)
        }
    }
}