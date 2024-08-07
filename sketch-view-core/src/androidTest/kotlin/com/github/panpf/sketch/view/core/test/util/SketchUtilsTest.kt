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
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SketchUtilsTest {

    @Test
    fun testRequestManagerOrNull() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val imageView = ImageView(context)

        Assert.assertNull(com.github.panpf.sketch.util.SketchUtils.requestManagerOrNull(imageView))
        imageView.loadImage(ResourceImages.jpeg.uri)
        Assert.assertNotNull(com.github.panpf.sketch.util.SketchUtils.requestManagerOrNull(imageView))
    }

    // TODO test dispose
    // TODO test getResult
    // TODO test restart

    @Test
    fun testGetRequest() {
        val activity = TestActivity::class.launchActivity().getActivitySync()
        val imageView = ImageView(activity)
        runBlocking(Dispatchers.Main) {
            activity.setContentView(imageView, LayoutParams(500, 500))
        }
        Thread.sleep(100)

        Assert.assertNull(com.github.panpf.sketch.util.SketchUtils.getRequest(imageView))
        imageView.loadImage(ResourceImages.jpeg.uri)
        Thread.sleep(100)
        Assert.assertNotNull(com.github.panpf.sketch.util.SketchUtils.getRequest(imageView))
    }

    @Test
    fun testGetSketch() {
        val activity = TestActivity::class.launchActivity().getActivitySync()
        val imageView = ImageView(activity)
        runBlocking(Dispatchers.Main) {
            activity.setContentView(imageView, LayoutParams(500, 500))
        }
        Thread.sleep(100)

        Assert.assertNull(com.github.panpf.sketch.util.SketchUtils.getSketch(imageView))
        imageView.loadImage(ResourceImages.jpeg.uri)
        Thread.sleep(100)
        Assert.assertNotNull(com.github.panpf.sketch.util.SketchUtils.getSketch(imageView))
    }
}