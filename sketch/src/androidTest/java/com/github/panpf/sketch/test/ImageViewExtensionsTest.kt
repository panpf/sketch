package com.github.panpf.sketch.test

import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.displayAssetImage
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.displayResourceImage
import com.github.panpf.sketch.dispose
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.result
import com.github.panpf.sketch.test.utils.DelayTransformation
import com.github.panpf.sketch.test.utils.ExifOrientationTestFileHelper
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ImageViewExtensionsTest {

    @Test
    fun testDisplayImage() {
        val context = getTestContext()

        val activity = TestActivity::class.launchActivity().getActivitySync()
        val imageView = activity.imageView

        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.displayImage(TestAssets.SAMPLE_JPEG_URI).job.join()
        }
        Assert.assertNotNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.displayImage(Uri.parse(TestAssets.SAMPLE_PNG_URI)).job.join()
        }
        Assert.assertNotNull(imageView.drawable)
        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.displayImage(null as Uri?).job.join()
        }
        Assert.assertNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.displayImage(R.drawable.ic_launcher).job.join()
        }
        Assert.assertNotNull(imageView.drawable)
        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.displayImage(null as Int?).job.join()
        }
        Assert.assertNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.displayResourceImage(R.drawable.test).job.join()
        }
        Assert.assertNotNull(imageView.drawable)
        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.displayResourceImage(null).job.join()
        }
        Assert.assertNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.displayResourceImage(context.packageName, R.drawable.test).job.join()
        }
        Assert.assertNotNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.displayAssetImage("sample_anim.gif").job.join()
        }
        Assert.assertNotNull(imageView.drawable)
        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.displayAssetImage(null).job.join()
        }
        Assert.assertNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        val file = ExifOrientationTestFileHelper(context, "exif_origin_clock_hor.jpeg", 2).files()
            .first().file
        runBlocking {
            imageView.displayImage(file).job.join()
        }
        Assert.assertNotNull(imageView.drawable)
        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.displayImage(null as File?).job.join()
        }
        Assert.assertNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.displayImage(null as String?).job.join()
        }
        Assert.assertNull(imageView.drawable)
    }

    @Test
    fun testDispose() {
        val activity = TestActivity::class.launchActivity().getActivitySync()
        val imageView = activity.imageView

        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.displayImage(TestAssets.SAMPLE_JPEG_URI).job.join()
        }
        Assert.assertNotNull(imageView.drawable)

        runBlocking(Dispatchers.Main) {
            imageView.setImageDrawable(null)
        }
        Assert.assertNull(imageView.drawable)
        runBlocking {
            imageView.displayImage(TestAssets.SAMPLE_PNG_URI) {
                resultCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                addTransformations(DelayTransformation {
                    imageView.dispose()
                })
            }.job.join()
        }
        Assert.assertNull(imageView.drawable)
    }

    @Test
    fun testResult() {
        val activity = TestActivity::class.launchActivity().getActivitySync()
        val imageView = activity.imageView

        Assert.assertNull(imageView.result)

        runBlocking {
            imageView.displayImage(TestAssets.SAMPLE_JPEG_URI).job.join()
        }
        Assert.assertTrue(imageView.result is DisplayResult.Success)

        runBlocking {
            imageView.displayImage("asset://fake.jpeg").job.join()
        }
        Assert.assertTrue(imageView.result is DisplayResult.Error)

        runBlocking {
            imageView.displayImage(TestAssets.SAMPLE_PNG_URI) {
                resultCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                addTransformations(DelayTransformation {
                    imageView.dispose()
                })
            }.job.join()
        }
        Assert.assertNull(imageView.result)
    }

    class TestActivity : FragmentActivity() {

        lateinit var imageView: ImageView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            imageView = ImageView(this)
            setContentView(imageView, LayoutParams(500, 500))
        }
    }
}