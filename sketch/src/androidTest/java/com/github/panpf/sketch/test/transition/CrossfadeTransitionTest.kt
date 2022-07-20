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
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.UnknownException
import com.github.panpf.tools4j.reflect.ktx.getFieldValue
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
    fun testConstructor() {
        val context = getTestContext()
        val imageView = ImageView(context)
        val imageViewTarget = ImageViewDisplayTarget(imageView)
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))
        val resultDrawable =
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 200, RGB_565))
        val result = DisplayResult.Success(
            request = request,
            drawable = resultDrawable,
            imageInfo = ImageInfo(100, 200, "image/jpeg", 0),
            dataFrom = LOCAL,
            transformedList = null
        )
        CrossfadeTransition(imageViewTarget, result).apply {
            Assert.assertEquals(100, durationMillis)
            Assert.assertEquals(false, preferExactIntrinsicSize)
            Assert.assertEquals(true, fitScale)
        }
        CrossfadeTransition(
            imageViewTarget,
            result,
            durationMillis = 300,
            preferExactIntrinsicSize = true,
            fitScale = false
        ).apply {
            Assert.assertEquals(300, durationMillis)
            Assert.assertEquals(true, preferExactIntrinsicSize)
            Assert.assertEquals(false, fitScale)
        }
        assertThrow(IllegalArgumentException::class) {
            CrossfadeTransition(imageViewTarget, result, durationMillis = 0)
        }
    }

    @Test
    fun testTransition() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))

        val imageView = ImageView(context)
        val imageViewTarget = ImageViewDisplayTarget(imageView)

        Assert.assertNull(imageView.drawable)
        Assert.assertNull(imageViewTarget.drawable)
        Assert.assertEquals(false, imageViewTarget.getFieldValue<Boolean>("isStarted"))
        imageViewTarget.setFieldValue("isStarted", true)
        Assert.assertEquals(true, imageViewTarget.getFieldValue<Boolean>("isStarted"))

        // success
        runBlocking(Dispatchers.Main) {
            imageViewTarget.drawable = ColorDrawable(Color.GREEN)
        }
        Assert.assertEquals(Color.GREEN, (imageView.drawable as ColorDrawable).color)
        Assert.assertEquals(Color.GREEN, (imageViewTarget.drawable as ColorDrawable).color)
        val resultDrawable =
            BitmapDrawable(context.resources, Bitmap.createBitmap(100, 200, RGB_565))
        val success = DisplayResult.Success(
            request = request,
            drawable = resultDrawable,
            imageInfo = ImageInfo(100, 200, "image/jpeg", 0),
            dataFrom = LOCAL,
            transformedList = null
        )
        CrossfadeTransition(imageViewTarget, success).transition()
        (imageView.drawable as CrossfadeDrawable).apply {
            Assert.assertEquals(Color.GREEN, (start as ColorDrawable).color)
            Assert.assertTrue(end is BitmapDrawable)
            Assert.assertTrue(fitScale)
        }

        // error
        runBlocking(Dispatchers.Main) {
            imageViewTarget.drawable = ColorDrawable(Color.GREEN)
        }
        Assert.assertEquals(Color.GREEN, (imageView.drawable as ColorDrawable).color)
        Assert.assertEquals(Color.GREEN, (imageViewTarget.drawable as ColorDrawable).color)
        val error = DisplayResult.Error(
            request = request,
            drawable = resultDrawable,
            exception = UnknownException(""),
        )
        CrossfadeTransition(imageViewTarget, error).transition()
        (imageView.drawable as CrossfadeDrawable).apply {
            Assert.assertEquals(Color.GREEN, (start as ColorDrawable).color)
            Assert.assertTrue(end is BitmapDrawable)
        }

        // start end same
        runBlocking(Dispatchers.Main) {
            imageViewTarget.drawable = ColorDrawable(Color.GREEN)
        }
        Assert.assertTrue(imageViewTarget.drawable!! is ColorDrawable)
        CrossfadeTransition(
            imageViewTarget, DisplayResult.Success(
                request = request,
                drawable = imageViewTarget.drawable!!,
                imageInfo = ImageInfo(100, 200, "image/jpeg", 0),
                dataFrom = LOCAL,
                transformedList = null
            )
        ).transition()
        Assert.assertTrue(imageViewTarget.drawable!! is ColorDrawable)
    }

    @Test
    fun testFactoryConstructor() {
        CrossfadeTransition.Factory().apply {
            Assert.assertEquals(100, durationMillis)
            Assert.assertEquals(false, preferExactIntrinsicSize)
            Assert.assertEquals(false, alwaysUse)
        }

        assertThrow(IllegalArgumentException::class) {
            CrossfadeTransition.Factory(0)
        }

        CrossfadeTransition.Factory(
            durationMillis = 300,
            preferExactIntrinsicSize = true,
            alwaysUse = true
        ).apply {
            Assert.assertEquals(300, durationMillis)
            Assert.assertEquals(true, preferExactIntrinsicSize)
            Assert.assertEquals(true, alwaysUse)
        }
    }

    @Test
    fun testFactoryCreate() {
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
            imageInfo = ImageInfo(100, 200, "image/jpeg", 0),
            dataFrom = LOCAL,
            transformedList = null
        )
        Assert.assertNotNull(factory.create(imageViewTarget, successResult, true))

        val errorResult = DisplayResult.Error(
            request = request,
            drawable = resultDrawable,
            exception = UnknownException("", null)
        )
        Assert.assertNotNull(factory.create(imageViewTarget, errorResult, true))

        val fromMemoryCacheSuccessResult = DisplayResult.Success(
            request = request,
            drawable = resultDrawable,
            imageInfo = ImageInfo(100, 200, "image/jpeg", 0),
            dataFrom = MEMORY_CACHE,
            transformedList = null
        )
        Assert.assertNull(factory.create(imageViewTarget, fromMemoryCacheSuccessResult, true))

        val alwaysUseFactory = CrossfadeTransition.Factory(alwaysUse = true)
        Assert.assertNotNull(
            alwaysUseFactory.create(
                imageViewTarget,
                fromMemoryCacheSuccessResult,
                true
            )
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = CrossfadeTransition.Factory()
        val element11 = CrossfadeTransition.Factory()
        val element2 = CrossfadeTransition.Factory(durationMillis = 300)
        val element3 = CrossfadeTransition.Factory(preferExactIntrinsicSize = true)
        val element4 = CrossfadeTransition.Factory(alwaysUse = true)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element1, element4)
        Assert.assertNotSame(element11, element2)
        Assert.assertNotSame(element11, element3)
        Assert.assertNotSame(element11, element4)
        Assert.assertNotSame(element2, element3)
        Assert.assertNotSame(element2, element4)
        Assert.assertNotSame(element3, element4)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element1, element4)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element2, element4)
        Assert.assertNotEquals(element3, element4)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testFactoryToString() {
        val element1 = CrossfadeTransition.Factory()
        val element2 = CrossfadeTransition.Factory(preferExactIntrinsicSize = true)
        val element3 = CrossfadeTransition.Factory(alwaysUse = true)

        Assert.assertEquals(
            "CrossfadeTransition.Factory(durationMillis=100, preferExactIntrinsicSize=false, alwaysUse=false)",
            element1.toString()
        )
        Assert.assertEquals(
            "CrossfadeTransition.Factory(durationMillis=100, preferExactIntrinsicSize=true, alwaysUse=false)",
            element2.toString()
        )
        Assert.assertEquals(
            "CrossfadeTransition.Factory(durationMillis=100, preferExactIntrinsicSize=false, alwaysUse=true)",
            element3.toString()
        )
    }
}