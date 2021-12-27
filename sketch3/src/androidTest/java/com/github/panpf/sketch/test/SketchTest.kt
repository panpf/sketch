package com.github.panpf.sketch.test

import android.net.Uri
import android.os.Looper
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ExecuteResult
import com.github.panpf.sketch.common.Listener
import com.github.panpf.sketch.common.cache.CachePolicy
import com.github.panpf.sketch.download.DownloadResult
import com.github.panpf.sketch.download.DownloadRequest
import com.github.panpf.sketch.test.internal.TestHttpStack
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SketchTest {

    @Test
    fun testEnqueueDownload() {
        val context = InstrumentationRegistry.getContext()

        /*
         * success
         */
        val normalSketch = Sketch.new(context) {
            httpStack(TestHttpStack(context))
        }
        val normalDownloadListenerSupervisor = DownloadListenerSupervisor()
        val normalCallbackActionList = normalDownloadListenerSupervisor.callbackActionList
        val normalRequest = DownloadRequest.new(TestHttpStack.urls.first().uri)
        val normalDisposable =
            normalSketch.enqueueDownload(normalRequest, normalDownloadListenerSupervisor)
        runBlocking {
            normalDisposable.job.await()
        }.apply {
            Assert.assertTrue(this is ExecuteResult.Success)
        }
        Assert.assertEquals("onStart, onSuccess", normalCallbackActionList.joinToString())

        /*
         * cancel
         */
        val slowSketch = Sketch.new(context) {
            httpStack(TestHttpStack(context, readDelayMillis = 1000))
        }
        val cancelDownloadListenerSupervisor = DownloadListenerSupervisor()
        val cancelCallbackActionList = cancelDownloadListenerSupervisor.callbackActionList
        val cancelRequest = DownloadRequest.new(TestHttpStack.urls.first().uri) {
            diskCachePolicy(CachePolicy.DISABLED)
        }
        val cancelDisposable =
            slowSketch.enqueueDownload(cancelRequest, cancelDownloadListenerSupervisor)
        runBlocking {
            delay(1000)
            cancelDisposable.dispose()
            cancelDisposable.job.join()
        }
        Assert.assertEquals("onStart, onCancel", cancelCallbackActionList.joinToString())

        /*
         * error
         */
        val errorDownloadListenerSupervisor = DownloadListenerSupervisor()
        val errorCallbackActionList = errorDownloadListenerSupervisor.callbackActionList
        val errorTestUri = TestHttpStack.TestUri(Uri.parse("http://fake.jpeg"), 43235)
        val errorRequest = DownloadRequest.new(errorTestUri.uri) {
            diskCachePolicy(CachePolicy.DISABLED)
        }
        val errorDisposable =
            slowSketch.enqueueDownload(errorRequest, errorDownloadListenerSupervisor)
        runBlocking {
            errorDisposable.job.await()
        }.apply {
            Assert.assertTrue(this is ExecuteResult.Error)
        }
        Assert.assertEquals("onStart, onError", errorCallbackActionList.joinToString())
    }

    @Test
    fun testExecuteDownload() {
        val context = InstrumentationRegistry.getContext()

        /*
         * success
         */
        val normalSketch = Sketch.new(context) {
            httpStack(TestHttpStack(context))
        }
        val normalRequest = DownloadRequest.new(TestHttpStack.urls.first().uri)
        runBlocking {
            normalSketch.executeDownload(normalRequest)
        }.apply {
            Assert.assertTrue(this is ExecuteResult.Success)
        }

        /*
         * cancel
         */
        val slowSketch = Sketch.new(context) {
            httpStack(TestHttpStack(context, readDelayMillis = 1000))
        }
        val cancelRequest = DownloadRequest.new(TestHttpStack.urls.first().uri) {
            diskCachePolicy(CachePolicy.DISABLED)
        }
        runBlocking {
            val job = launch {
                slowSketch.executeDownload(cancelRequest)
            }
            delay(1000)
            job.cancelAndJoin()
        }

        /*
         * error
         */
        val errorTestUri = TestHttpStack.TestUri(Uri.parse("http://fake.jpeg"), 43235)
        val errorRequest = DownloadRequest.new(errorTestUri.uri) {
            diskCachePolicy(CachePolicy.DISABLED)
        }
        runBlocking {
            slowSketch.executeDownload(errorRequest)
        }.apply {
            Assert.assertTrue(this is ExecuteResult.Error)
        }
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