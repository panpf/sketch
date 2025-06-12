package com.github.panpf.sketch.view.core.test.util

import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.DrawableWrapper
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.widget.ImageView.ScaleType
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.utils.SizeColorDrawable
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.calculateBounds
import com.github.panpf.sketch.util.calculateBoundsWithScaleAndAlignment
import com.github.panpf.sketch.util.calculateBoundsWithScaleType
import com.github.panpf.sketch.util.findDeepestDrawable
import com.github.panpf.sketch.util.findLeafDrawable
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.sketch.util.requiredMainThread
import com.github.panpf.sketch.util.toHexString
import com.github.panpf.sketch.util.toScale
import com.github.panpf.sketch.util.toScaleType
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
import kotlin.test.assertSame
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
        assertTrue(ScaleType.FIT_START.fitScale)
        assertTrue(ScaleType.FIT_CENTER.fitScale)
        assertTrue(ScaleType.FIT_END.fitScale)
        assertFalse(ScaleType.FIT_XY.fitScale)
        assertFalse(ScaleType.CENTER_CROP.fitScale)
        assertFalse(ScaleType.CENTER.fitScale)
        assertTrue(ScaleType.CENTER_INSIDE.fitScale)
        assertFalse(ScaleType.MATRIX.fitScale)
    }

    @Test
    fun testFindLeafDrawable() {
        val painter1 = ColorDrawable(Color.WHITE)
        val painter2 = SizeColorDrawable(Color.BLUE, Size(100, 100))

        assertSame(expected = painter1, actual = painter1.findLeafDrawable())
        assertSame(expected = painter2, actual = painter2.findLeafDrawable())

        CrossfadeDrawable(start = painter1, end = painter2).apply {
            assertSame(expected = painter2, actual = this.findLeafDrawable())
        }
        CrossfadeDrawable(start = painter2, end = painter1).apply {
            assertSame(expected = painter1, actual = this.findLeafDrawable())
        }

        LayerDrawable(arrayOf(painter1, painter2)).apply {
            assertSame(expected = painter2, actual = this.findLeafDrawable())
        }
        LayerDrawable(arrayOf(painter2, painter1)).apply {
            assertSame(expected = painter1, actual = this.findLeafDrawable())
        }
    }

    @Test
    fun testFindDeepestDrawable() {
        val painter1 = ColorDrawable(Color.WHITE)
        val painter2 = SizeColorDrawable(Color.BLUE, Size(100, 100))

        assertSame(expected = painter1, actual = painter1.findDeepestDrawable())
        assertSame(expected = painter2, actual = painter2.findDeepestDrawable())

        CrossfadeDrawable(start = painter1, end = painter2).apply {
            assertSame(expected = painter2, actual = this.findDeepestDrawable())
        }
        CrossfadeDrawable(start = painter2, end = painter1).apply {
            assertSame(expected = painter1, actual = this.findDeepestDrawable())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            object : DrawableWrapper(painter1) {}.apply {
                assertSame(expected = painter1, actual = this.findDeepestDrawable())
            }
            object : DrawableWrapper(painter2) {}.apply {
                assertSame(expected = painter2, actual = this.findDeepestDrawable())
            }
        }

        DrawableWrapperCompat(painter1).apply {
            assertSame(expected = painter1, actual = this.findDeepestDrawable())
        }
        DrawableWrapperCompat(painter2).apply {
            assertSame(expected = painter2, actual = this.findDeepestDrawable())
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

    @Test
    fun testCalculateBoundsWithScaleType() {
        assertEquals(
            expected = Rect(left = 0, top = 0, right = 0, bottom = 0),
            actual = calculateBoundsWithScaleType(
                srcSize = Size(width = 101, height = 202),
                dstSize = Size.Empty,
                scaleType = ScaleType.FIT_START
            )
        )
        assertEquals(
            expected = Rect(left = 0, top = 0, right = 303, bottom = 404),
            actual = calculateBoundsWithScaleType(
                srcSize = Size.Empty,
                dstSize = Size(width = 303, height = 404),
                scaleType = ScaleType.FIT_START
            )
        )
        assertEquals(
            expected = Rect(left = 0, top = 0, right = 0, bottom = 0),
            actual = calculateBoundsWithScaleType(
                srcSize = Size.Empty,
                dstSize = Size.Empty,
                scaleType = ScaleType.FIT_START
            )
        )

        // small scaled srcSize
        listOf(
            ScaleType.MATRIX to Rect(0, 0, 51, 23),
            ScaleType.FIT_XY to Rect(0, 0, 100, 100),
            ScaleType.FIT_START to Rect(0, 0, 100, 46),
            ScaleType.FIT_CENTER to Rect(0, 27, 100, 73),
            ScaleType.FIT_END to Rect(0, 54, 100, 100),
            ScaleType.CENTER to Rect(24, 38, 76, 62),
            ScaleType.CENTER_CROP to Rect(-61, 0, 161, 100),
            ScaleType.CENTER_INSIDE to Rect(24, 38, 76, 62),
        ).forEach { (scaleType, exceptedBound) ->
            assertEquals(
                expected = exceptedBound,
                actual = calculateBoundsWithScaleType(
                    srcSize = Size(51, 23),
                    dstSize = Size(100, 100),
                    scaleType = scaleType
                ),
                message = "scaleType=$scaleType"
            )
        }

        // large scaled srcSize
        listOf(
            ScaleType.MATRIX to Rect(0, 0, 151, 123),
            ScaleType.FIT_XY to Rect(0, 0, 100, 100),
            ScaleType.FIT_START to Rect(0, 0, 100, 82),
            ScaleType.FIT_CENTER to Rect(0, 9, 100, 91),
            ScaleType.FIT_END to Rect(0, 18, 100, 100),
            ScaleType.CENTER to Rect(-26, -12, 126, 112),
            ScaleType.CENTER_CROP to Rect(-12, 0, 112, 100),
            ScaleType.CENTER_INSIDE to Rect(0, 9, 100, 91),
        ).forEach { (scaleType, exceptedBound) ->
            assertEquals(
                expected = exceptedBound,
                actual = calculateBoundsWithScaleType(
                    srcSize = Size(151, 123),
                    dstSize = Size(100, 100),
                    scaleType = scaleType
                ),
                message = "scaleType=$scaleType"
            )
        }
    }

    @Test
    fun testCalculateBoundsWithScaleAndAlignment() {
        // Empty size
        assertEquals(
            expected = Rect(0, 0, 0, 0),
            actual = calculateBoundsWithScaleAndAlignment(
                srcSize = Size(width = 101, height = 202),
                dstSize = Size.Empty,
                scaleFactor = PointF(/* x = */ 1f, /* y = */ 1f),
                alignment = 0
            )
        )
        assertEquals(
            expected = Rect(0, 0, 303, 404),
            actual = calculateBoundsWithScaleAndAlignment(
                srcSize = Size.Empty,
                dstSize = Size(width = 303, height = 404),
                scaleFactor = PointF(/* x = */ 1f, /* y = */ 1f),
                alignment = 0
            )
        )
        assertEquals(
            expected = Rect(0, 0, 0, 0),
            actual = calculateBoundsWithScaleAndAlignment(
                srcSize = Size.Empty,
                dstSize = Size.Empty,
                scaleFactor = PointF(/* x = */ 1f, /* y = */ 1f),
                alignment = 0
            )
        )

        // small scaled srcSize
        listOf(
            -1 to Rect(0, 0, 67, 49),
            0 to Rect(16, 25, 84, 75),
            1 to Rect(33, 51, 100, 100),
        ).forEach { (alignment, expectedBounds) ->
            assertEquals(
                expected = expectedBounds,
                actual = calculateBoundsWithScaleAndAlignment(
                    srcSize = Size(width = 51, height = 23),
                    dstSize = Size(width = 100, height = 100),
                    scaleFactor = PointF(/* x = */ 1.3f, /* y = */ 2.1f),
                    alignment = alignment
                ),
                message = "alignment=$alignment"
            )
        }

        // large scaled srcSize
        listOf(
            -1 to Rect(0, 0, 169, 141),
            0 to Rect(-35, -21, 135, 121),
            1 to Rect(-69, -41, 100, 100),
        ).forEach { (alignment, expectedBounds) ->
            assertEquals(
                expected = expectedBounds,
                actual = calculateBoundsWithScaleAndAlignment(
                    srcSize = Size(width = 51, height = 23),
                    dstSize = Size(width = 100, height = 100),
                    scaleFactor = PointF(/* x = */ 3.3f, /* y = */ 6.1f),
                    alignment = alignment
                ),
                message = "alignment=$alignment"
            )
        }
    }

    @Test
    fun testToScale() {
        assertEquals(expected = Scale.START_CROP, actual = ScaleType.FIT_START.toScale())
        assertEquals(expected = Scale.CENTER_CROP, actual = ScaleType.FIT_CENTER.toScale())
        assertEquals(expected = Scale.END_CROP, actual = ScaleType.FIT_END.toScale())
        assertEquals(expected = Scale.FILL, actual = ScaleType.FIT_XY.toScale())
        assertEquals(expected = Scale.CENTER_CROP, actual = ScaleType.CENTER_CROP.toScale())
        assertEquals(expected = Scale.CENTER_CROP, actual = ScaleType.CENTER.toScale())
        assertEquals(expected = Scale.CENTER_CROP, actual = ScaleType.CENTER_INSIDE.toScale())
        assertEquals(expected = Scale.FILL, actual = ScaleType.MATRIX.toScale())
    }

    @Test
    fun testToScaleType() {
        assertEquals(expected = ScaleType.FIT_START, actual = Scale.START_CROP.toScaleType())
        assertEquals(expected = ScaleType.CENTER_CROP, actual = Scale.CENTER_CROP.toScaleType())
        assertEquals(expected = ScaleType.FIT_END, actual = Scale.END_CROP.toScaleType())
        assertEquals(expected = ScaleType.FIT_XY, actual = Scale.FILL.toScaleType())
    }
}