package com.github.panpf.sketch.test.http

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.internal.ProgressListenerDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProgressListenerDelegateTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val request = LoadRequest(context, newAssetUri("sample.jpeg"))
        val scope = CoroutineScope(SupervisorJob())
        val completedList = mutableListOf<Long>()
        val listener = ProgressListener<LoadRequest> { _, _, completedLength ->
            Thread.sleep(100)
            completedList.add(completedLength)
        }
        val delegate = ProgressListenerDelegate(scope, listener)

        delegate.onUpdateProgress(request, 1000, 200)
        Thread.sleep(40)
        delegate.onUpdateProgress(request, 1000, 400)
        Thread.sleep(40)
        delegate.onUpdateProgress(request, 1000, 600)
        Thread.sleep(40)
        delegate.onUpdateProgress(request, 1000, 800)
        Thread.sleep(40)
        delegate.onUpdateProgress(request, 1000, 1000)
        Thread.sleep(150)
        Assert.assertTrue(completedList.size < 5)
    }
}