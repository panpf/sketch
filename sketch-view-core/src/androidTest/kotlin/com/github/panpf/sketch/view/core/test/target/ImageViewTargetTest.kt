package com.github.panpf.sketch.view.core.test.target

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asDrawable
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.target.ImageViewTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.fakeSuccessImageResult
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ImageViewTargetTest {

    @Test
    fun testDrawable() {
        val (context, sketch) = getTestContextAndSketch()
        val imageView = ImageView(context)
        val target = ImageViewTarget(imageView)

        assertNull(imageView.drawable)
        assertNull(target.drawable)

        val drawable1 = Bitmap.createBitmap(100, 100, RGB_565).asImage().asDrawable()
        val drawable2 = Bitmap.createBitmap(100, 100, RGB_565).asImage().asDrawable()
        imageView.setImageDrawable(drawable1)
        assertSame(drawable1, imageView.drawable)
        assertSame(drawable1, target.drawable)

        val request = ImageRequest(context, "http://sample/com/sample/jpeg")
        target.onSuccess(sketch, request, fakeSuccessImageResult(context), drawable2.asImage())
        assertSame(drawable2, imageView.drawable)
        assertSame(drawable2, target.drawable)
    }

    @Test
    fun testScaleType() {
        val context = getTestContext()
        val imageView = ImageView(context)
        val target = ImageViewTarget(imageView)

        assertEquals(ScaleType.FIT_CENTER, imageView.scaleType)
        assertEquals(ScaleType.FIT_CENTER, target.scaleType)
        assertTrue(target.fitScale)

        imageView.scaleType = ScaleType.FIT_START
        assertEquals(ScaleType.FIT_START, target.scaleType)
        assertTrue(target.fitScale)

        imageView.scaleType = ScaleType.FIT_END
        assertEquals(ScaleType.FIT_END, target.scaleType)
        assertTrue(target.fitScale)

        imageView.scaleType = ScaleType.CENTER_INSIDE
        assertEquals(ScaleType.CENTER_INSIDE, target.scaleType)
        assertTrue(target.fitScale)

        imageView.scaleType = ScaleType.FIT_XY
        assertEquals(ScaleType.FIT_XY, target.scaleType)
        assertFalse(target.fitScale)

        imageView.scaleType = ScaleType.MATRIX
        assertEquals(ScaleType.MATRIX, target.scaleType)
        assertFalse(target.fitScale)

        imageView.scaleType = ScaleType.CENTER
        assertEquals(ScaleType.CENTER, target.scaleType)
        assertFalse(target.fitScale)

        imageView.scaleType = ScaleType.CENTER_CROP
        assertEquals(ScaleType.CENTER_CROP, target.scaleType)
        assertFalse(target.fitScale)
    }

    @Test
    fun testScaleDecider() {
        val context = getTestContext()
        val imageView = ImageView(context)
        val target = ImageViewTarget(imageView)

        assertEquals(ScaleType.FIT_CENTER, imageView.scaleType)
        assertEquals(FixedScaleDecider(Scale.CENTER_CROP), target.getScaleDecider())

        imageView.scaleType = ScaleType.FIT_START
        assertEquals(FixedScaleDecider(Scale.START_CROP), target.getScaleDecider())

        imageView.scaleType = ScaleType.FIT_END
        assertEquals(FixedScaleDecider(Scale.END_CROP), target.getScaleDecider())

        imageView.scaleType = ScaleType.CENTER_INSIDE
        assertEquals(FixedScaleDecider(Scale.CENTER_CROP), target.getScaleDecider())

        imageView.scaleType = ScaleType.CENTER
        assertEquals(FixedScaleDecider(Scale.CENTER_CROP), target.getScaleDecider())

        imageView.scaleType = ScaleType.CENTER_CROP
        assertEquals(FixedScaleDecider(Scale.CENTER_CROP), target.getScaleDecider())

        imageView.scaleType = ScaleType.FIT_XY
        assertEquals(FixedScaleDecider(Scale.FILL), target.getScaleDecider())

        imageView.scaleType = ScaleType.MATRIX
        assertEquals(FixedScaleDecider(Scale.FILL), target.getScaleDecider())
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val imageView1 = ImageView(context)
        val imageView2 = ImageView(context)
        val element1 = ImageViewTarget(imageView1)
        val element11 = ImageViewTarget(imageView1)
        val element2 = ImageViewTarget(imageView2)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val imageView = ImageView(context)
        val target = ImageViewTarget(imageView)
        assertEquals(
            expected = "ImageViewTarget($imageView)",
            actual = target.toString()
        )
    }
}