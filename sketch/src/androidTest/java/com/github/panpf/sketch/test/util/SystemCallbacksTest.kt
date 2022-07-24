package com.github.panpf.sketch.test.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.util.SystemCallbacks
import com.github.panpf.tools4a.network.ktx.isCellularNetworkConnected
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.ref.WeakReference

@RunWith(AndroidJUnit4::class)
class SystemCallbacksTest {

    @Test
    fun test() {
        val (context, sketch) = getTestContextAndNewSketch()
        SystemCallbacks(context, WeakReference(sketch)).apply {
            Assert.assertEquals(context.isCellularNetworkConnected(), isCellularNetworkConnected)

            Assert.assertFalse(isShutdown)
            shutdown()
            Assert.assertTrue(isShutdown)
        }
    }
}