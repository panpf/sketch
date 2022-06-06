package com.github.panpf.sketch.test.target

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.target.ImageViewTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageViewTargetTest {

    @Test
    fun testDrawable() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))

        val imageView = ImageView(context)
        Assert.assertNull(imageView.drawable)

        val imageViewTarget = ImageViewTarget(imageView)
        Assert.assertNull(imageViewTarget.drawable)

        val countBitmap = CountBitmap(
            initBitmap = Bitmap.createBitmap(100, 100, RGB_565),
            requestKey = request.key,
            imageUri = request.uriString,
            imageInfo = ImageInfo(100, 100, "image/jpeg"),
            imageExifOrientation = 0,
            transformedList = null,
            logger = sketch.logger,
            bitmapPool = sketch.bitmapPool
        )
        val sketchCountBitmapDrawable =
            SketchCountBitmapDrawable(context.resources, countBitmap, LOCAL)
        val countBitmap2 = CountBitmap(
            initBitmap = Bitmap.createBitmap(100, 100, RGB_565),
            requestKey = request.key,
            imageUri = request.uriString,
            imageInfo = ImageInfo(100, 100, "image/jpeg"),
            imageExifOrientation = 0,
            transformedList = null,
            logger = sketch.logger,
            bitmapPool = sketch.bitmapPool
        )
        val sketchCountBitmapDrawable2 =
            SketchCountBitmapDrawable(context.resources, countBitmap2, LOCAL)

        runBlocking(Dispatchers.Main) {
            Assert.assertEquals(0, countBitmap.getDisplayedCount())
            Assert.assertEquals(0, countBitmap2.getDisplayedCount())
        }

        runBlocking(Dispatchers.Main) {
            imageViewTarget.drawable = sketchCountBitmapDrawable
        }

        Assert.assertSame(sketchCountBitmapDrawable, imageView.drawable)
        Assert.assertSame(sketchCountBitmapDrawable, imageViewTarget.drawable)
        runBlocking(Dispatchers.Main) {
            Assert.assertEquals(1, countBitmap.getDisplayedCount())
            Assert.assertEquals(0, countBitmap2.getDisplayedCount())
        }

        runBlocking(Dispatchers.Main) {
            imageViewTarget.drawable = sketchCountBitmapDrawable2
        }

        Assert.assertSame(sketchCountBitmapDrawable2, imageView.drawable)
        Assert.assertSame(sketchCountBitmapDrawable2, imageViewTarget.drawable)
        runBlocking(Dispatchers.Main) {
            Assert.assertEquals(0, countBitmap.getDisplayedCount())
            Assert.assertEquals(1, countBitmap2.getDisplayedCount())
        }

        runBlocking(Dispatchers.Main) {
            imageViewTarget.drawable = null
        }
        Assert.assertNull(imageView.drawable)
        Assert.assertNull(imageViewTarget.drawable)
    }
}