package com.github.panpf.sketch.test

import android.os.Looper
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.test.util.TestHttpStack
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
        val normalRequest = DownloadRequest.new(TestHttpStack.testUris.first().uriString) {
            listener(normalDownloadListenerSupervisor)
        }
        val normalDisposable =
            normalSketch.enqueueDownload(normalRequest)
        runBlocking {
            normalDisposable.job.await()
        }.apply {
            Assert.assertTrue(this is DownloadResult.Success)
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
        val cancelRequest = DownloadRequest.new(TestHttpStack.testUris.first().uriString) {
            diskCachePolicy(CachePolicy.DISABLED)
            listener(cancelDownloadListenerSupervisor)
        }
        val cancelDisposable =
            slowSketch.enqueueDownload(cancelRequest)
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
        val errorTestUri = TestHttpStack.TestUri("http://fake.jpeg", 43235)
        val errorRequest = DownloadRequest.new(errorTestUri.uriString) {
            diskCachePolicy(CachePolicy.DISABLED)
            listener(errorDownloadListenerSupervisor)
        }
        val errorDisposable =
            slowSketch.enqueueDownload(errorRequest)
        runBlocking {
            errorDisposable.job.await()
        }.apply {
            Assert.assertTrue(this is DownloadResult.Error)
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
        val normalRequest = DownloadRequest.new(TestHttpStack.testUris.first().uriString)
        runBlocking {
            normalSketch.executeDownload(normalRequest)
        }.apply {
            Assert.assertTrue(this is DownloadResult.Success)
        }

        /*
         * cancel
         */
        val slowSketch = Sketch.new(context) {
            httpStack(TestHttpStack(context, readDelayMillis = 1000))
        }
        val cancelRequest = DownloadRequest.new(TestHttpStack.testUris.first().uriString) {
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
        val errorTestUri = TestHttpStack.TestUri("http://fake.jpeg", 43235)
        val errorRequest = DownloadRequest.new(errorTestUri.uriString) {
            diskCachePolicy(CachePolicy.DISABLED)
        }
        runBlocking {
            slowSketch.executeDownload(errorRequest)
        }.apply {
            Assert.assertTrue(this is DownloadResult.Error)
        }
    }

    private class DownloadListenerSupervisor : Listener<DownloadRequest, DownloadData> {

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

        override fun onSuccess(request: DownloadRequest, data: DownloadData) {
            super.onSuccess(request, data)
            check(Looper.getMainLooper() === Looper.myLooper())
            callbackActionList.add("onSuccess")
        }
    }
}