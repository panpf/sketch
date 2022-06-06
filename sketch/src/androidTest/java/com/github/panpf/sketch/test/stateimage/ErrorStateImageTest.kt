package com.github.panpf.sketch.test.stateimage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.UriInvalidException
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.ColorStateImage
import com.github.panpf.sketch.stateimage.DrawableStateImage
import com.github.panpf.sketch.stateimage.ErrorStateImage
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorStateImageTest {

    @Test
    fun testGetDrawable() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val request = DisplayRequest(context, "")
        val colorDrawable = ColorDrawable(Color.BLUE)
        val colorDrawable2 = ColorDrawable(Color.RED)

        ErrorStateImage(DrawableStateImage(colorDrawable)).apply {
            Assert.assertEquals(colorDrawable, getDrawable(sketch, request, null))
            Assert.assertEquals(
                colorDrawable,
                getDrawable(sketch, request, UriInvalidException(request, ""))
            )
        }

        ErrorStateImage(DrawableStateImage(colorDrawable)) {
            uriEmptyError(colorDrawable2)
        }.apply {
            Assert.assertEquals(colorDrawable, getDrawable(sketch, request, null))
            Assert.assertEquals(
                colorDrawable2,
                getDrawable(sketch, request, UriInvalidException(request, ""))
            )
        }
    }

    @Test
    fun testEquals() {
        val stateImage1 = ErrorStateImage(ColorStateImage(Color.RED))
        val stateImage11 = ErrorStateImage(ColorStateImage(Color.RED))

        val stateImage2 = ErrorStateImage(ColorStateImage(Color.GREEN))
        val stateImage21 = ErrorStateImage(ColorStateImage(Color.GREEN))

        val stateImage3 = ErrorStateImage(ColorStateImage(Color.BLUE)) {
            uriEmptyError(ColorStateImage(Color.YELLOW))
        }
        val stateImage31 = ErrorStateImage(ColorStateImage(Color.BLUE)) {
            uriEmptyError(ColorStateImage(Color.YELLOW))
        }

        Assert.assertNotSame(stateImage1, stateImage11)
        Assert.assertNotSame(stateImage2, stateImage21)
        Assert.assertNotSame(stateImage3, stateImage31)

        Assert.assertEquals(stateImage1, stateImage11)
        Assert.assertEquals(stateImage2, stateImage21)
        Assert.assertEquals(stateImage3, stateImage31)

        Assert.assertNotEquals(stateImage1, stateImage2)
        Assert.assertNotEquals(stateImage1, stateImage3)
        Assert.assertNotEquals(stateImage2, stateImage3)
    }

    @Test
    fun testHashCode() {
        val stateImage1 = ErrorStateImage(ColorStateImage(Color.RED))
        val stateImage11 = ErrorStateImage(ColorStateImage(Color.RED))

        val stateImage2 = ErrorStateImage(ColorStateImage(Color.GREEN))
        val stateImage21 = ErrorStateImage(ColorStateImage(Color.GREEN))

        val stateImage3 = ErrorStateImage(ColorStateImage(Color.BLUE)) {
            uriEmptyError(ColorStateImage(Color.YELLOW))
        }
        val stateImage31 = ErrorStateImage(ColorStateImage(Color.BLUE)) {
            uriEmptyError(ColorStateImage(Color.YELLOW))
        }

        Assert.assertEquals(stateImage1.hashCode(), stateImage11.hashCode())
        Assert.assertEquals(stateImage2.hashCode(), stateImage21.hashCode())
        Assert.assertEquals(stateImage3.hashCode(), stateImage31.hashCode())

        Assert.assertNotEquals(stateImage1.hashCode(), stateImage2.hashCode())
        Assert.assertNotEquals(stateImage1.hashCode(), stateImage3.hashCode())
        Assert.assertNotEquals(stateImage2.hashCode(), stateImage3.hashCode())
    }

    @Test
    fun testToString() {
        ErrorStateImage(ColorStateImage(Color.RED)).apply {
            Assert.assertEquals(
                "ErrorStateImage([DefaultMatcher(ColorStateImage(color=IntColor(${Color.RED})))])",
                toString()
            )
        }

        ErrorStateImage(ColorStateImage(Color.GREEN)) {
            uriEmptyError(ColorStateImage(Color.YELLOW))
        }.apply {
            Assert.assertEquals(
                "ErrorStateImage([UriEmptyMatcher(ColorStateImage(color=IntColor(${Color.YELLOW}))), DefaultMatcher(ColorStateImage(color=IntColor(${Color.GREEN})))])",
                toString()
            )
        }
    }
}