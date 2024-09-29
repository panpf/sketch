package com.github.panpf.sketch.view.core.test.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.calculateBounds
import com.github.panpf.sketch.util.findLeafChildDrawable
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.util.toHexString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import okio.IOException
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ViewCoreUtilsTest {

    @Test
    fun testAnyAsOrNull() {
        assertNotNull(IOException().asOrNull<Exception>())
        assertNull((null as Exception?).asOrNull<Exception>())
        assertFailsWith(ClassCastException::class) {
            @Suppress("KotlinConstantConditions")
            Throwable() as Exception
        }
        assertNull(Throwable().asOrNull<Exception>())
    }

    @Test
    fun testAnyToHexString() {
        val any = Any()
        assertEquals(
            expected = any.hashCode().toString(16),
            actual = any.toHexString()
        )
    }

    @Test
    fun testRequiredMainThread() = runTest {
        assertFailsWith(IllegalStateException::class) {
            requiredMainThread()
        }
        withContext(Dispatchers.Main) {
            requiredMainThread()
        }
    }

    @Test
    fun testFitScale() {
        val context = getTestContext()

        assertTrue(ImageView(context).apply {
            scaleType = ScaleType.FIT_START
        }.scaleType.fitScale)

        assertTrue(ImageView(context).apply {
            scaleType = ScaleType.FIT_CENTER
        }.scaleType.fitScale)

        assertTrue(ImageView(context).apply {
            scaleType = ScaleType.FIT_END
        }.scaleType.fitScale)

        assertFalse(ImageView(context).apply {
            scaleType = ScaleType.FIT_XY
        }.scaleType.fitScale)

        assertFalse(ImageView(context).apply {
            scaleType = ScaleType.CENTER_CROP
        }.scaleType.fitScale)

        assertFalse(ImageView(context).apply {
            scaleType = ScaleType.CENTER
        }.scaleType.fitScale)

        assertTrue(ImageView(context).apply {
            scaleType = ScaleType.CENTER_INSIDE
        }.scaleType.fitScale)

        assertFalse(ImageView(context).apply {
            scaleType = ScaleType.MATRIX
        }.scaleType.fitScale)
    }

    @Test
    fun testFindLeafChildDrawable() {
        LayerDrawable(
            arrayOf(
                ColorDrawable(Color.BLUE),
                ColorDrawable(Color.RED),
                ColorDrawable(Color.GREEN)
            )
        ).findLeafChildDrawable().apply {
            assertEquals(Color.GREEN, this!!.asOrThrow<ColorDrawable>().color)
        }

        LayerDrawable(
            arrayOf(
                ColorDrawable(Color.RED),
                ColorDrawable(Color.GREEN),
                ColorDrawable(Color.BLUE),
            )
        ).findLeafChildDrawable().apply {
            assertEquals(Color.BLUE, this!!.asOrThrow<ColorDrawable>().color)
        }

        LayerDrawable(arrayOf()).findLeafChildDrawable().apply {
            assertEquals(null, this)
        }


        CrossfadeDrawable(
            ColorDrawable(Color.BLUE),
            ColorDrawable(Color.RED),
        ).findLeafChildDrawable().apply {
            assertEquals(Color.RED, this!!.asOrThrow<ColorDrawable>().color)
        }

        CrossfadeDrawable(
            ColorDrawable(Color.RED),
            ColorDrawable(Color.GREEN),
        ).findLeafChildDrawable().apply {
            assertEquals(Color.GREEN, this!!.asOrThrow<ColorDrawable>().color)
        }

        CrossfadeDrawable(null, null).findLeafChildDrawable().apply {
            assertEquals(null, this)
        }

        ColorDrawable(Color.GREEN).findLeafChildDrawable().apply {
            assertEquals(Color.GREEN, this!!.asOrThrow<ColorDrawable>().color)
        }

        ColorDrawable(Color.RED).findLeafChildDrawable().apply {
            assertEquals(Color.RED, this!!.asOrThrow<ColorDrawable>().color)
        }
    }

    @Test
    fun testCalculateBounds() {
        assertEquals(
            expected = Rect(0, 0, 100, 100),
            actual = calculateBounds(Size.Empty, Size(100, 100), Scale.CENTER_CROP)
        )
        assertEquals(
            expected = Rect(0, 0, 100, 100),
            actual = calculateBounds(Size(100, 100), Size.Empty, Scale.CENTER_CROP)
        )
        assertEquals(
            expected = Rect(0, 0, 0, 0),
            actual = calculateBounds(Size.Empty, Size.Empty, Scale.CENTER_CROP)
        )

        assertEquals(
            expected = Rect(0, 0, 250, 100),
            actual = calculateBounds(Size(50, 20), Size(100, 100), Scale.START_CROP)
        )
        assertEquals(
            expected = Rect(-75, 0, 175, 100),
            actual = calculateBounds(Size(50, 20), Size(100, 100), Scale.CENTER_CROP)
        )
        assertEquals(
            expected = Rect(-150, 0, 100, 100),
            actual = calculateBounds(Size(50, 20), Size(100, 100), Scale.END_CROP)
        )
        assertEquals(
            expected = Rect(0, 0, 100, 100),
            actual = calculateBounds(Size(50, 20), Size(100, 100), Scale.FILL)
        )

        assertEquals(
            expected = Rect(0, 0, 100, 250),
            actual = calculateBounds(Size(20, 50), Size(100, 100), Scale.START_CROP)
        )
        assertEquals(
            expected = Rect(0, -75, 100, 175),
            actual = calculateBounds(Size(20, 50), Size(100, 100), Scale.CENTER_CROP)
        )
        assertEquals(
            expected = Rect(0, -150, 100, 100),
            actual = calculateBounds(Size(20, 50), Size(100, 100), Scale.END_CROP)
        )
        assertEquals(
            expected = Rect(0, 0, 100, 100),
            actual = calculateBounds(Size(20, 50), Size(100, 100), Scale.FILL)
        )

        assertEquals(
            expected = Rect(0, 0, 125, 100),
            actual = calculateBounds(Size(150, 120), Size(100, 100), Scale.START_CROP)
        )
        assertEquals(
            expected = Rect(-12, 0, 113, 100),
            actual = calculateBounds(Size(150, 120), Size(100, 100), Scale.CENTER_CROP)
        )
        assertEquals(
            expected = Rect(-25, 0, 100, 100),
            actual = calculateBounds(Size(150, 120), Size(100, 100), Scale.END_CROP)
        )
        assertEquals(
            expected = Rect(0, 0, 100, 100),
            actual = calculateBounds(Size(150, 120), Size(100, 100), Scale.FILL)
        )

        assertEquals(
            expected = Rect(0, 0, 100, 125),
            actual = calculateBounds(Size(120, 150), Size(100, 100), Scale.START_CROP)
        )
        assertEquals(
            expected = Rect(0, -12, 100, 113),
            actual = calculateBounds(Size(120, 150), Size(100, 100), Scale.CENTER_CROP)
        )
        assertEquals(
            expected = Rect(0, -25, 100, 100),
            actual = calculateBounds(Size(120, 150), Size(100, 100), Scale.END_CROP)
        )
        assertEquals(
            expected = Rect(0, 0, 100, 100),
            actual = calculateBounds(Size(120, 150), Size(100, 100), Scale.FILL)
        )
    }
}