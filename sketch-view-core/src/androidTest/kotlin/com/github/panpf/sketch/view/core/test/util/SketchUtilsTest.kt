package com.github.panpf.sketch.view.core.test.util

import android.graphics.drawable.BitmapDrawable
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.disposeLoad
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.test.singleton.loadImage
import com.github.panpf.sketch.test.utils.DelayDecodeInterceptor
import com.github.panpf.sketch.test.utils.MediumImageViewTestActivity
import com.github.panpf.sketch.test.utils.TestActivity
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class SketchUtilsTest {

    @Test
    fun testRequestManagerOrNull() = runTest {
        TestActivity::class.launchActivity().use { scenario ->
            val activity = scenario.getActivitySync()
            val imageView = ImageView(activity)
            withContext(Dispatchers.Main) {
                activity.setContentView(imageView, LayoutParams(500, 500))
            }
            assertNull(SketchUtils.requestManagerOrNull(imageView))

            imageView.loadImage(ResourceImages.jpeg.uri)
            block(100)
            assertNotNull(SketchUtils.requestManagerOrNull(imageView))
        }
    }

    @Test
    fun testDispose() = runTest {
        MediumImageViewTestActivity::class.launchActivity().use { scenario ->
            val activity = scenario.getActivitySync()
            val imageView = activity.imageView

            assertNull(imageView.drawable)
            imageView.loadImage(ResourceImages.jpeg.uri).job.join()
            assertNotNull(imageView.drawable)

            withContext(Dispatchers.Main) {
                imageView.setImageDrawable(null)
            }
            assertNull(imageView.drawable)
            imageView.loadImage(ResourceImages.png.uri) {
                resultCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                components {
                    addDecodeInterceptor(DelayDecodeInterceptor(1000) {
                        SketchUtils.dispose(imageView)
                    })
                }
            }.job.join()
            assertNull(imageView.drawable)
        }
    }

    @Test
    fun testGetResult() = runTest {
        MediumImageViewTestActivity::class.launchActivity().use { scenario ->
            val activity = scenario.getActivitySync()
            val imageView = activity.imageView

            assertNull(SketchUtils.getResult(imageView))

            imageView.loadImage(ResourceImages.jpeg.uri).job.join()
            assertTrue(SketchUtils.getResult(imageView) is ImageResult.Success)

            imageView.loadImage("file:///android_asset/fake.jpeg").job.join()
            assertTrue(SketchUtils.getResult(imageView) is ImageResult.Error)

            imageView.loadImage(ResourceImages.png.uri) {
                resultCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                components {
                    addDecodeInterceptor(DelayDecodeInterceptor(1000) {
                        imageView.disposeLoad()
                    })
                }
            }.job.join()
            assertNull(SketchUtils.getResult(imageView))
        }
    }

    @Test
    fun testRestart() = runTest {
        TestActivity::class.launchActivity().use { scenario ->
            val activity = scenario.getActivitySync()
            val imageView = ImageView(activity)
            withContext(Dispatchers.Main) {
                activity.setContentView(imageView, LayoutParams(500, 500))
            }
            assertNull(SketchUtils.requestManagerOrNull(imageView))

            imageView.loadImage(ResourceImages.jpeg.uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(DISABLED)
            }
            block(1000)
            val drawable1 = imageView.drawable
            assertTrue(
                actual = drawable1 is BitmapDrawable,
                message = "drawable1: $drawable1"
            )

            SketchUtils.restart(imageView)
            block(1000)
            val drawable2 = imageView.drawable
            assertTrue(
                actual = drawable2 is BitmapDrawable,
                message = "drawable2: $drawable2"
            )

            assertNotSame(illegal = drawable1, actual = drawable2)
        }
    }

    @Test
    fun testGetRequest() = runTest {
        TestActivity::class.launchActivity().use { scenario ->
            val activity = scenario.getActivitySync()
            val imageView = ImageView(activity)
            withContext(Dispatchers.Main) {
                activity.setContentView(imageView, LayoutParams(500, 500))
            }
            Thread.sleep(100)

            assertNull(SketchUtils.getRequest(imageView))
            imageView.loadImage(ResourceImages.jpeg.uri)
            Thread.sleep(100)
            assertNotNull(SketchUtils.getRequest(imageView))
        }
    }

    @Test
    fun testGetSketch() = runTest {
        TestActivity::class.launchActivity().use { scenario ->
            val activity = scenario.getActivitySync()
            val imageView = ImageView(activity)
            withContext(Dispatchers.Main) {
                activity.setContentView(imageView, LayoutParams(500, 500))
            }
            Thread.sleep(100)

            assertNull(SketchUtils.getSketch(imageView))
            imageView.loadImage(ResourceImages.jpeg.uri)
            Thread.sleep(100)
            assertNotNull(SketchUtils.getSketch(imageView))
        }
    }
}