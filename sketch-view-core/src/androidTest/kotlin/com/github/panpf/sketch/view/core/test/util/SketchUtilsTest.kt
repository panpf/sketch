package com.github.panpf.sketch.view.core.test.util

import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.test.singleton.loadImage
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class SketchUtilsTest {

    @Test
    fun testRequestManagerOrNull() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val imageView = ImageView(context)

        assertNull(com.github.panpf.sketch.util.SketchUtils.requestManagerOrNull(imageView))
        imageView.loadImage(ResourceImages.jpeg.uri)
        assertNotNull(com.github.panpf.sketch.util.SketchUtils.requestManagerOrNull(imageView))
    }

    @Test
    fun testDispose() {
        // TODO test
    }

    @Test
    fun testGetResult() {
        // TODO test
    }

    @Test
    fun testRestart() {
        // TODO test
    }

    @Test
    fun testGetRequest() {
        val activity = TestActivity::class.launchActivity().getActivitySync()
        val imageView = ImageView(activity)
        runBlocking(Dispatchers.Main) {
            activity.setContentView(imageView, LayoutParams(500, 500))
        }
        Thread.sleep(100)

        assertNull(com.github.panpf.sketch.util.SketchUtils.getRequest(imageView))
        imageView.loadImage(ResourceImages.jpeg.uri)
        Thread.sleep(100)
        assertNotNull(com.github.panpf.sketch.util.SketchUtils.getRequest(imageView))
    }

    @Test
    fun testGetSketch() {
        val activity = TestActivity::class.launchActivity().getActivitySync()
        val imageView = ImageView(activity)
        runBlocking(Dispatchers.Main) {
            activity.setContentView(imageView, LayoutParams(500, 500))
        }
        Thread.sleep(100)

        assertNull(com.github.panpf.sketch.util.SketchUtils.getSketch(imageView))
        imageView.loadImage(ResourceImages.jpeg.uri)
        Thread.sleep(100)
        assertNotNull(com.github.panpf.sketch.util.SketchUtils.getSketch(imageView))
    }
}