package com.github.panpf.sketch.core.android.test

import android.widget.ImageView
import com.github.panpf.sketch.Sketch.Builder
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult.Error
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.ListenerSupervisor
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SketchAndroidTest {

    @Test
    fun testBuilder() {
        val activity = TestActivity::class.launchActivity().getActivitySync()
        Builder(activity).build().apply {
            assertNotEquals(activity, context)
            assertEquals(activity.applicationContext, context)
        }
    }

    @Test
    fun testExecute() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        /* ViewTarget */
        val imageView = ImageView(context)
        val listenerSupervisor4 = ListenerSupervisor()
        val request4 = ImageRequest(imageView, ResourceImages.jpeg.uri) {
            registerListener(listenerSupervisor4)
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

    @Test
    fun testPlatformComponents() {
        // TODO test
    }
}