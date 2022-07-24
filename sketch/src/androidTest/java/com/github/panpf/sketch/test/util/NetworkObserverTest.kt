package com.github.panpf.sketch.test.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.NetworkObserver
import com.github.panpf.tools4a.network.ktx.isCellularNetworkConnected
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NetworkObserverTest {

    @Test
    fun test() {
        val context = getTestContext()
        NetworkObserver(context).apply {
            Assert.assertEquals(context.isCellularNetworkConnected(), isCellularNetworkConnected)
            shutdown()
        }
    }
}