package com.github.panpf.sketch.view.core.test

import android.widget.ImageView
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult.Error
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.ListenerSupervisor
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExecuteTest {

    @Test
    fun testExecute() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        /* ViewTarget */
        val imageView = ImageView(context)
        val listenerSupervisor4 = ListenerSupervisor()
        val request4 = ImageRequest(imageView, ComposeResImageFiles.jpeg.uri) {
            addListener(listenerSupervisor4)
            lifecycle(GlobalLifecycle)
        }
        val result4 = try {
            sketch.execute(request4)
        } catch (e: Exception) {
            Error(request4, null, e)
        }
        assertTrue(result4 is Error)
        assertEquals(listOf(), listenerSupervisor4.callbackActionList)
    }
}