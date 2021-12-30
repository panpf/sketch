package com.github.panpf.sketch.test.request.internal

import android.net.Uri
import android.os.Looper
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ExecuteResult
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.internal.DownloadExecutor
import com.github.panpf.sketch.test.util.TestHttpStack
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DownloadExecutorTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getContext()

        /*
         * success
         */
        val normalSketch = Sketch.new(context) {
            httpStack(TestHttpStack(context))
        }
        val normalDownloadListenerSupervisor = DownloadListenerSupervisor()
        val normalListenerActionList = normalDownloadListenerSupervisor.callbackActionList
        val normalRequest = DownloadRequest.new(TestHttpStack.urls.first().uri)
        runBlocking {
            DownloadExecutor(normalSketch).execute(
                normalRequest, normalDownloadListenerSupervisor, null
            )
        }.apply {
            Assert.assertTrue(this is ExecuteResult.Success)
        }
        Assert.assertEquals("onStart, onSuccess", normalListenerActionList.joinToString())

        /*
         * cancel
         */
        val slowSketch = Sketch.new(context) {
            httpStack(TestHttpStack(context, readDelayMillis = 1000))
        }
        val cancelDownloadListenerSupervisor = DownloadListenerSupervisor()
        val cancelListenerList = cancelDownloadListenerSupervisor.callbackActionList
        val cancelRequest = DownloadRequest.new(TestHttpStack.urls.first().uri) {
            diskCachePolicy(CachePolicy.DISABLED)
        }
        runBlocking {
            val job = launch {
                DownloadExecutor(slowSketch).execute(
                    cancelRequest, cancelDownloadListenerSupervisor, null
                )
            }
            delay(1000)
            job.cancelAndJoin()
        }
        Assert.assertEquals("onStart, onCancel", cancelListenerList.joinToString())

        /*
         * error
         */
        val errorDownloadListenerSupervisor = DownloadListenerSupervisor()
        val errorTestUri = TestHttpStack.TestUri(Uri.parse("http://fake.jpeg"), 43235)
        val errorListenerActionList = errorDownloadListenerSupervisor.callbackActionList
        val errorRequest = DownloadRequest.new(errorTestUri.uri) {
            diskCachePolicy(CachePolicy.DISABLED)
        }
        runBlocking {
            DownloadExecutor(slowSketch).execute(
                errorRequest, errorDownloadListenerSupervisor, null
            )
        }.apply {
            Assert.assertTrue(this is ExecuteResult.Error)
        }
        Assert.assertEquals("onStart, onError", errorListenerActionList.joinToString())
    }

    private class DownloadListenerSupervisor : Listener<DownloadRequest, DownloadResult> {

        val callbackActionList = mutableListOf<String>()

        override fun onStart(request: DownloadRequest) {
            super.onStart(request)
            check(Looper.getMainLooper() === Looper.myLooper())
            callbackActionList.add("onStart")
        }

        override fun onCancel(request: DownloadRequest) {
            super.onCancel(request)
            check(Looper.getMainLooper() === Looper.myLooper())
            callbackActionList.add("onCancel")
        }

        override fun onError(request: DownloadRequest, throwable: Throwable) {
            super.onError(request, throwable)
            check(Looper.getMainLooper() === Looper.myLooper())
            callbackActionList.add("onError")
        }

        override fun onSuccess(request: DownloadRequest, result: DownloadResult) {
            super.onSuccess(request, result)
            check(Looper.getMainLooper() === Looper.myLooper())
            callbackActionList.add("onSuccess")
        }
    }
}