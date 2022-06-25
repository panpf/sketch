package com.github.panpf.sketch.test.request.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.internal.CombinedProgressListener
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CombinedProgressListenerTest {

    @Test
    fun test() {
        val listenerCallbackList = mutableListOf<String>()
        Assert.assertEquals(listOf<String>(), listenerCallbackList)

        val listener1 = ProgressListener<DownloadRequest> { _, _, _ ->
            listenerCallbackList.add("onUpdateProgress1")
        }
        val listener2 = ProgressListener<DownloadRequest> { _, _, _ ->
            listenerCallbackList.add("onUpdateProgress2")
        }

        val context = getTestContext()
        val request = DownloadRequest(context, "http://sample.com/sample.jpeg")

        val combinedProgressListener = CombinedProgressListener(listener1, listener2)
        Assert.assertSame(listener1, combinedProgressListener.fromViewProgressListener)
        Assert.assertSame(listener2, combinedProgressListener.fromBuilderProgressListener)

        combinedProgressListener.onUpdateProgress(request, 10000, 2000)
        Assert.assertEquals(listOf("onUpdateProgress1", "onUpdateProgress2"), listenerCallbackList)
    }
}