package com.github.panpf.sketch.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.MEMORY
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.Listeners
import com.github.panpf.sketch.test.utils.DownloadListenerSupervisor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.UnknownException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListenersTest {

    @Test
    fun test() {
        val context = getTestContext()
        val request = DownloadRequest(context, "http://sample.com/sample.jpeg")

        val list = listOf(
            DownloadListenerSupervisor("2"),
            DownloadListenerSupervisor("3"),
            DownloadListenerSupervisor("1"),
        )
        Assert.assertEquals(listOf<String>(), list.flatMap { it.callbackActionList })

        val listeners = Listeners(*list.toTypedArray())
        Assert.assertEquals(list, listeners.listenerList)

        runBlocking(Dispatchers.Main) {
            listeners.onStart(request)
        }
        Assert.assertEquals(
            listOf("onStart:2", "onStart:3", "onStart:1"),
            list.flatMap { it.callbackActionList })

        runBlocking(Dispatchers.Main) {
            listeners.onCancel(request)
        }
        Assert.assertEquals(
            listOf(
                "onStart:2",
                "onCancel:2",
                "onStart:3",
                "onCancel:3",
                "onStart:1",
                "onCancel:1",
            ), list.flatMap { it.callbackActionList })

        runBlocking(Dispatchers.Main) {
            listeners.onError(request, DownloadResult.Error(request, UnknownException("")))
        }
        Assert.assertEquals(
            listOf(
                "onStart:2",
                "onCancel:2",
                "onError:2",
                "onStart:3",
                "onCancel:3",
                "onError:3",
                "onStart:1",
                "onCancel:1",
                "onError:1",
            ),
            list.flatMap { it.callbackActionList })

        runBlocking(Dispatchers.Main) {
            listeners.onSuccess(
                request,
                DownloadResult.Success(request, DownloadData.Bytes(byteArrayOf(), MEMORY))
            )
        }
        Assert.assertEquals(
            listOf(
                "onStart:2",
                "onCancel:2",
                "onError:2",
                "onSuccess:2",
                "onStart:3",
                "onCancel:3",
                "onError:3",
                "onSuccess:3",
                "onStart:1",
                "onCancel:1",
                "onError:1",
                "onSuccess:1",
            ),
            list.flatMap { it.callbackActionList })
    }
}