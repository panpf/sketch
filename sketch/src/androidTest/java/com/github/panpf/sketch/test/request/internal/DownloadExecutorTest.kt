//package com.github.panpf.sketch.test.request.internal
//
//import android.os.Looper
//import androidx.test.platform.app.InstrumentationRegistry
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.github.panpf.sketch.Sketch
//import com.github.panpf.sketch.cache.CachePolicy
//import com.github.panpf.sketch.request.DownloadRequest
//import com.github.panpf.sketch.request.DownloadResult
//import com.github.panpf.sketch.request.internal.DownloadExecutor
//import com.github.panpf.sketch.test.utils.TestHttpStack
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.cancelAndJoin
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
//import org.junit.Assert
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class DownloadExecutorTest {
//
//    @Test
//    fun test() {
//        val context = InstrumentationRegistry.getInstrumentation().context
//
//        /*
//         * success
//         */
//        val normalSketch = Sketch.Builder(context).apply {
//            httpStack(TestHttpStack(context))
//        }.build()
//        val normalDownloadListenerSupervisor = DownloadListenerSupervisor()
//        val normalListenerActionList = normalDownloadListenerSupervisor.callbackActionList
//        val normalRequest = DownloadRequest(context, TestHttpStack.testUris.first().uriString) {
//            listener(normalDownloadListenerSupervisor)
//        }
//        runBlocking(Dispatchers.Main) {
//            DownloadExecutor(normalSketch).execute(normalRequest)
//        }.apply {
//            Assert.assertTrue(this is DownloadResult.Success)
//        }
//        Assert.assertEquals("onStart, onSuccess", normalListenerActionList.joinToString())
//
//        /*
//         * cancel
//         */
//        val slowSketch = Sketch.Builder(context).apply {
//            httpStack(TestHttpStack(context, readDelayMillis = 1000))
//        }.build()
//        val cancelDownloadListenerSupervisor = DownloadListenerSupervisor()
//        val cancelListenerList = cancelDownloadListenerSupervisor.callbackActionList
//        val cancelRequest = DownloadRequest(context, TestHttpStack.testUris.first().uriString) {
//            networkContentDiskCachePolicy(CachePolicy.DISABLED)
//            listener(cancelDownloadListenerSupervisor)
//        }
//        runBlocking(Dispatchers.Main) {
//            val job = launch {
//                DownloadExecutor(slowSketch).execute(cancelRequest)
//            }
//            delay(1000)
//            job.cancelAndJoin()
//        }
//        Assert.assertEquals("onStart, onCancel", cancelListenerList.joinToString())
//
//        /*
//         * error
//         */
//        val errorDownloadListenerSupervisor = DownloadListenerSupervisor()
//        val errorTestUri = TestHttpStack.TestUri("http://fake.jpeg", 43235)
//        val errorListenerActionList = errorDownloadListenerSupervisor.callbackActionList
//        val errorRequest = DownloadRequest(context, errorTestUri.uriString) {
//            networkContentDiskCachePolicy(CachePolicy.DISABLED)
//            listener(errorDownloadListenerSupervisor)
//        }
//        runBlocking(Dispatchers.Main) {
//            DownloadExecutor(slowSketch).execute(errorRequest)
//        }.apply {
//            Assert.assertTrue(this is DownloadResult.Error)
//        }
//        Assert.assertEquals("onStart, onError", errorListenerActionList.joinToString())
//    }
//
//    private class DownloadListenerSupervisor :
//        Listener<DownloadRequest, DownloadResult.Success, DownloadResult.Error> {
//
//        val callbackActionList = mutableListOf<String>()
//
//        override fun onStart(request: DownloadRequest) {
//            super.onStart(request)
//            check(Looper.getMainLooper() === Looper.myLooper())
//            callbackActionList.add("onStart")
//        }
//
//        override fun onCancel(request: DownloadRequest) {
//            super.onCancel(request)
//            check(Looper.getMainLooper() === Looper.myLooper())
//            callbackActionList.add("onCancel")
//        }
//
//        override fun onError(request: DownloadRequest, result: DownloadResult.Error) {
//            super.onError(request, result)
//            check(Looper.getMainLooper() === Looper.myLooper())
//            callbackActionList.add("onError")
//        }
//
//        override fun onSuccess(request: DownloadRequest, result: DownloadResult.Success) {
//            super.onSuccess(request, result)
//            check(Looper.getMainLooper() === Looper.myLooper())
//            callbackActionList.add("onSuccess")
//        }
//    }
//}