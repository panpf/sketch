package com.github.panpf.sketch3.test

import android.net.Uri
import android.os.Looper
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch3.Sketch3
import com.github.panpf.sketch3.common.cache.CachePolicy
import com.github.panpf.sketch3.download.DownloadRequest
import com.github.panpf.sketch3.download.internal.DownloadExecutor
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
    fun testCallListenerNormal() {
        val context = InstrumentationRegistry.getContext()
        val sketch3 = Sketch3.new(context) {
            httpStack(TestHttpStack(context))
        }

        val listenerList = mutableListOf<String>()
        val request = DownloadRequest.new(TestHttpStack.urls.first().uri) {
            listener(
                onStart = {
                    check(Looper.getMainLooper() === Looper.myLooper())
                    listenerList.add("onStart")
                },
                onSuccess = { _, _ ->
                    check(Looper.getMainLooper() === Looper.myLooper())
                    listenerList.add("onSuccess")
                },
                onCancel = {
                    check(Looper.getMainLooper() === Looper.myLooper())
                    listenerList.add("onCancel")
                },
                onError = { _, _ ->
                    check(Looper.getMainLooper() === Looper.myLooper())
                    listenerList.add("onError")
                },
            )
        }

        val downloadExecutor = DownloadExecutor(sketch3)
        runBlocking {
            downloadExecutor.execute(request)
        }
        Assert.assertEquals("onStart, onSuccess", listenerList.joinToString())
    }

    @Test
    fun testCallListenerCancel() {
        val context = InstrumentationRegistry.getContext()
        val sketch3 = Sketch3.new(context) {
            httpStack(TestHttpStack(context, readDelayMillis = 1000))
        }

        val listenerList = mutableListOf<String>()
        val request = DownloadRequest.new(TestHttpStack.urls.first().uri) {
            diskCachePolicy(CachePolicy.DISABLED)
            listener(
                onStart = {
                    check(Looper.getMainLooper() === Looper.myLooper())
                    listenerList.add("onStart")
                },
                onSuccess = { _, _ ->
                    check(Looper.getMainLooper() === Looper.myLooper())
                    listenerList.add("onSuccess")
                },
                onCancel = {
                    check(Looper.getMainLooper() === Looper.myLooper())
                    listenerList.add("onCancel")
                },
                onError = { _, _ ->
                    check(Looper.getMainLooper() === Looper.myLooper())
                    listenerList.add("onError")
                },
            )
        }

        val downloadExecutor = DownloadExecutor(sketch3)
        runBlocking {
            val job = launch {
                downloadExecutor.execute(request)
            }
            delay(1000)
            job.cancelAndJoin()
            delay(1000)
        }
        Assert.assertEquals("onStart, onCancel", listenerList.joinToString())
    }

    @Test
    fun testCallListenerException() {
        val context = InstrumentationRegistry.getContext()
        val sketch3 = Sketch3.new(context) {
            httpStack(TestHttpStack(context, readDelayMillis = 1000))
        }

        val testUri = TestHttpStack.TestUri(Uri.parse("http://fake.jpeg"), 43235)
        val listenerList = mutableListOf<String>()
        val request = DownloadRequest.new(testUri.uri) {
            diskCachePolicy(CachePolicy.DISABLED)
            listener(
                onStart = {
                    check(Looper.getMainLooper() === Looper.myLooper())
                    listenerList.add("onStart")
                },
                onSuccess = { _, _ ->
                    check(Looper.getMainLooper() === Looper.myLooper())
                    listenerList.add("onSuccess")
                },
                onCancel = {
                    check(Looper.getMainLooper() === Looper.myLooper())
                    listenerList.add("onCancel")
                },
                onError = { _, _ ->
                    check(Looper.getMainLooper() === Looper.myLooper())
                    listenerList.add("onError")
                },
            )
        }

        val downloadExecutor = DownloadExecutor(sketch3)
        runBlocking {
            val job = launch {
                downloadExecutor.execute(request)
            }
            job.join()
        }
        Assert.assertEquals("onStart, onError", listenerList.joinToString())
    }
}