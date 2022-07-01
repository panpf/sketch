package com.github.panpf.sketch.test.transition

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.datasource.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.target.ImageViewDisplayTarget
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.UnknownException
import com.github.panpf.tools4j.reflect.ktx.setFieldValue
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrossfadeTransitionTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))

        val imageView = ImageView(context)
        Assert.assertNull(imageView.drawable)

        val imageViewTarget = ImageViewDisplayTarget(imageView)
        Assert.assertNull(imageViewTarget.drawable)

        runBlocking(Dispatchers.Main) {
            imageViewTarget.drawable = ColorDrawable(Color.GREEN)
        }

        Assert.assertEquals(Color.GREEN, (imageView.drawable as ColorDrawable).color)

        val resultDrawable =
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 200, RGB_565))
        val result = DisplayResult.Success(
            request = request,
            drawable = resultDrawable,
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            imageExifOrientation = 0,
            dataFrom = LOCAL,
            transformedList = null
        )

        assertThrow(IllegalArgumentException::class) {
            CrossfadeTransition(imageViewTarget, result, durationMillis = -1)
        }

        imageViewTarget.setFieldValue("isStarted", true)
        CrossfadeTransition(imageViewTarget, result).apply {
            Assert.assertEquals(100, durationMillis)
            Assert.assertTrue(fitScale)
            Assert.assertFalse(preferExactIntrinsicSize)
        }.transition()

        (imageView.drawable as CrossfadeDrawable).apply {
            Assert.assertEquals(Color.GREEN, (start as ColorDrawable).color)
            Assert.assertTrue(end is BitmapDrawable)
        }
    }

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))
        val factory = CrossfadeTransition.Factory()

        val imageView = ImageView(context)
        val imageViewTarget = ImageViewDisplayTarget(imageView)

        val resultDrawable =
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 200, RGB_565))

        val successResult = DisplayResult.Success(
            request = request,
            drawable = resultDrawable,
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            imageExifOrientation = 0,
            dataFrom = LOCAL,
            transformedList = null
        )
        Assert.assertNotNull(factory.create(imageViewTarget, successResult, true))

        val errorResult = DisplayResult.Error(
            request = request,
            drawable = resultDrawable,
            exception = UnknownException("", null)
        )
        Assert.assertNull(factory.create(imageViewTarget, errorResult, true))

        val fromMemoryCacheSuccessResult = DisplayResult.Success(
            request = request,
            drawable = resultDrawable,
            imageInfo = ImageInfo(100, 200, "image/jpeg"),
            imageExifOrientation = 0,
            dataFrom = MEMORY_CACHE,
            transformedList = null
        )
        Assert.assertNull(factory.create(imageViewTarget, fromMemoryCacheSuccessResult, true))

        // todo test alwaysUse
    }
}