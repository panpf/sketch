package com.github.panpf.sketch.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.ProgressListeners
import com.github.panpf.sketch.test.utils.DownloadProgressListenerSupervisor
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProgressListenersTest {

    @Test
    fun test() {
        val context = getTestContext()
        val request = DownloadRequest(context, "http://sample.com/sample.jpeg")

        val list = listOf(
            DownloadProgressListenerSupervisor("2"),
            DownloadProgressListenerSupervisor("3"),
            DownloadProgressListenerSupervisor("1"),
        )
        Assert.assertEquals(listOf<String>(), list.flatMap { it.callbackActionList })

        val listeners = ProgressListeners(*list.toTypedArray())
        Assert.assertEquals(list, listeners.progressListenerList)

        runBlocking(Dispatchers.Main) {
            listeners.onUpdateProgress(request, 100, 10)
        }
        Assert.assertEquals(
            listOf("10:2", "10:3", "10:1"),
            list.flatMap { it.callbackActionList })

        runBlocking(Dispatchers.Main) {
            listeners.onUpdateProgress(request, 100, 20)
        }
        Assert.assertEquals(
            listOf("10:2", "20:2", "10:3", "20:3", "10:1", "20:1"),
            list.flatMap { it.callbackActionList })
    }
}