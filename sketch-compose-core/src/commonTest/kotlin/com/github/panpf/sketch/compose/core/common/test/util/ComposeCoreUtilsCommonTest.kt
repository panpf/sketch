package com.github.panpf.sketch.compose.core.common.test.util

import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.painter.CrossfadePainter
import com.github.panpf.sketch.painter.PainterWrapper
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.TestContentScale
import com.github.panpf.sketch.test.utils.createBitmap
import com.github.panpf.sketch.test.utils.toComposeBitmap
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.findDeepestPainter
import com.github.panpf.sketch.util.findLeafPainter
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.sketch.util.fromScale
import com.github.panpf.sketch.util.isEmpty
import com.github.panpf.sketch.util.name
import com.github.panpf.sketch.util.simpleName
import com.github.panpf.sketch.util.toHexString
import com.github.panpf.sketch.util.toIntSize
import com.github.panpf.sketch.util.toIntSizeOrNull
import com.github.panpf.sketch.util.toLogString
import com.github.panpf.sketch.util.toRequestSize
import com.github.panpf.sketch.util.toScale
import com.github.panpf.sketch.util.toSize
import com.github.panpf.sketch.util.toSketchSize
import okio.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class ComposeCoreUtilsCommonTest {

    @Test
    fun testContentScaleToScale() {
        assertEquals(expected = Scale.FILL, actual = ContentScale.FillBounds.toScale())
        assertEquals(expected = Scale.FILL, actual = ContentScale.FillWidth.toScale())
        assertEquals(expected = Scale.FILL, actual = ContentScale.FillHeight.toScale())
        assertEquals(expected = Scale.CENTER_CROP, actual = ContentScale.Fit.toScale())
        assertEquals(expected = Scale.CENTER_CROP, actual = ContentScale.Crop.toScale())
        assertEquals(expected = Scale.CENTER_CROP, actual = ContentScale.Inside.toScale())
        assertEquals(expected = Scale.CENTER_CROP, actual = ContentScale.None.toScale())
        assertEquals(expected = Scale.CENTER_CROP, actual = TestContentScale.toScale())
    }

    @Test
    fun testToScale() {
        assertEquals(Scale.FILL, toScale(ContentScale.FillBounds, Alignment.TopStart))
        assertEquals(Scale.FILL, toScale(ContentScale.FillBounds, Alignment.TopCenter))
        assertEquals(Scale.FILL, toScale(ContentScale.FillBounds, Alignment.TopEnd))
        assertEquals(Scale.FILL, toScale(ContentScale.FillBounds, Alignment.CenterStart))
        assertEquals(Scale.FILL, toScale(ContentScale.FillBounds, Alignment.Center))
        assertEquals(Scale.FILL, toScale(ContentScale.FillBounds, Alignment.CenterEnd))
        assertEquals(Scale.FILL, toScale(ContentScale.FillBounds, Alignment.BottomStart))
        assertEquals(Scale.FILL, toScale(ContentScale.FillBounds, Alignment.BottomCenter))
        assertEquals(Scale.FILL, toScale(ContentScale.FillBounds, Alignment.BottomEnd))
        assertEquals(Scale.FILL, toScale(ContentScale.FillHeight, Alignment.TopStart))
        assertEquals(Scale.FILL, toScale(ContentScale.FillHeight, Alignment.TopCenter))
        assertEquals(Scale.FILL, toScale(ContentScale.FillHeight, Alignment.TopEnd))
        assertEquals(Scale.FILL, toScale(ContentScale.FillHeight, Alignment.CenterStart))
        assertEquals(Scale.FILL, toScale(ContentScale.FillHeight, Alignment.Center))
        assertEquals(Scale.FILL, toScale(ContentScale.FillHeight, Alignment.CenterEnd))
        assertEquals(Scale.FILL, toScale(ContentScale.FillHeight, Alignment.BottomStart))
        assertEquals(Scale.FILL, toScale(ContentScale.FillHeight, Alignment.BottomCenter))
        assertEquals(Scale.FILL, toScale(ContentScale.FillHeight, Alignment.BottomEnd))
        assertEquals(Scale.FILL, toScale(ContentScale.FillWidth, Alignment.TopStart))
        assertEquals(Scale.FILL, toScale(ContentScale.FillWidth, Alignment.TopCenter))
        assertEquals(Scale.FILL, toScale(ContentScale.FillWidth, Alignment.TopEnd))
        assertEquals(Scale.FILL, toScale(ContentScale.FillWidth, Alignment.CenterStart))
        assertEquals(Scale.FILL, toScale(ContentScale.FillWidth, Alignment.Center))
        assertEquals(Scale.FILL, toScale(ContentScale.FillWidth, Alignment.CenterEnd))
        assertEquals(Scale.FILL, toScale(ContentScale.FillWidth, Alignment.BottomStart))
        assertEquals(Scale.FILL, toScale(ContentScale.FillWidth, Alignment.BottomCenter))
        assertEquals(Scale.FILL, toScale(ContentScale.FillWidth, Alignment.BottomEnd))
        assertEquals(Scale.START_CROP, toScale(ContentScale.Fit, Alignment.TopStart))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Fit, Alignment.TopCenter))
        assertEquals(Scale.END_CROP, toScale(ContentScale.Fit, Alignment.TopEnd))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Fit, Alignment.CenterStart))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Fit, Alignment.Center))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Fit, Alignment.CenterEnd))
        assertEquals(Scale.START_CROP, toScale(ContentScale.Fit, Alignment.BottomStart))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Fit, Alignment.BottomCenter))
        assertEquals(Scale.END_CROP, toScale(ContentScale.Fit, Alignment.BottomEnd))
        assertEquals(Scale.START_CROP, toScale(ContentScale.Crop, Alignment.TopStart))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Crop, Alignment.TopCenter))
        assertEquals(Scale.END_CROP, toScale(ContentScale.Crop, Alignment.TopEnd))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Crop, Alignment.CenterStart))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Crop, Alignment.Center))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Crop, Alignment.CenterEnd))
        assertEquals(Scale.START_CROP, toScale(ContentScale.Crop, Alignment.BottomStart))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Crop, Alignment.BottomCenter))
        assertEquals(Scale.END_CROP, toScale(ContentScale.Crop, Alignment.BottomEnd))
        assertEquals(Scale.START_CROP, toScale(ContentScale.Inside, Alignment.TopStart))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Inside, Alignment.TopCenter))
        assertEquals(Scale.END_CROP, toScale(ContentScale.Inside, Alignment.TopEnd))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Inside, Alignment.CenterStart))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Inside, Alignment.Center))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Inside, Alignment.CenterEnd))
        assertEquals(Scale.START_CROP, toScale(ContentScale.Inside, Alignment.BottomStart))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.Inside, Alignment.BottomCenter))
        assertEquals(Scale.END_CROP, toScale(ContentScale.Inside, Alignment.BottomEnd))
        assertEquals(Scale.START_CROP, toScale(ContentScale.None, Alignment.TopStart))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.None, Alignment.TopCenter))
        assertEquals(Scale.END_CROP, toScale(ContentScale.None, Alignment.TopEnd))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.None, Alignment.CenterStart))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.None, Alignment.Center))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.None, Alignment.CenterEnd))
        assertEquals(Scale.START_CROP, toScale(ContentScale.None, Alignment.BottomStart))
        assertEquals(Scale.CENTER_CROP, toScale(ContentScale.None, Alignment.BottomCenter))
        assertEquals(Scale.END_CROP, toScale(ContentScale.None, Alignment.BottomEnd))
    }

    @Test
    fun testFromScale() {
        assertEquals(ContentScale.FillBounds to Alignment.Center, fromScale(Scale.FILL))
        assertEquals(ContentScale.Crop to Alignment.TopStart, fromScale(Scale.START_CROP))
        assertEquals(ContentScale.Crop to Alignment.Center, fromScale(Scale.CENTER_CROP))
        assertEquals(ContentScale.Crop to Alignment.TopEnd, fromScale(Scale.END_CROP))
    }

    @Test
    fun testContentScaleName() {
        assertEquals(expected = "FillWidth", actual = ContentScale.FillWidth.name)
        assertEquals(expected = "FillHeight", actual = ContentScale.FillHeight.name)
        assertEquals(expected = "FillBounds", actual = ContentScale.FillBounds.name)
        assertEquals(expected = "Fit", actual = ContentScale.Fit.name)
        assertEquals(expected = "Crop", actual = ContentScale.Crop.name)
        assertEquals(expected = "Inside", actual = ContentScale.Inside.name)
        assertEquals(expected = "None", actual = ContentScale.None.name)
        assertEquals(expected = TestContentScale::class.simpleName, actual = TestContentScale.name)
        val anonymousContentScale = object : ContentScale {
            override fun computeScaleFactor(srcSize: Size, dstSize: Size): ScaleFactor {
                return ScaleFactor(1f, 1f)
            }
        }
        assertEquals(
            expected = anonymousContentScale.toString(),
            actual = anonymousContentScale.name
        )
    }

    @Test
    fun testContentScaleFitScale() {
        assertEquals(expected = false, actual = ContentScale.FillWidth.fitScale)
        assertEquals(expected = false, actual = ContentScale.FillHeight.fitScale)
        assertEquals(expected = false, actual = ContentScale.FillBounds.fitScale)
        assertEquals(expected = true, actual = ContentScale.Fit.fitScale)
        assertEquals(expected = false, actual = ContentScale.Crop.fitScale)
        assertEquals(expected = true, actual = ContentScale.Inside.fitScale)
        assertEquals(expected = false, actual = ContentScale.None.fitScale)
        assertEquals(expected = false, actual = TestContentScale.fitScale)
    }

    @Test
    fun testSizeToIntSizeOrNull() {
        assertEquals(expected = null, actual = Size.Unspecified.toIntSizeOrNull())
        assertEquals(expected = null, actual = Size.Zero.toIntSizeOrNull())
        assertEquals(expected = null, actual = Size(-1f, -1f).toIntSizeOrNull())
        assertEquals(expected = null, actual = Size(0.4f, 0.4f).toIntSizeOrNull())
        assertEquals(expected = null, actual = Size(0.5f, 0.4f).toIntSizeOrNull())
        assertEquals(expected = null, actual = Size(0.4f, 0.5f).toIntSizeOrNull())
        assertEquals(
            expected = null,
            actual = Size(Float.NEGATIVE_INFINITY, 0.5f).toIntSizeOrNull()
        )
        assertEquals(
            expected = null,
            actual = Size(0.5f, Float.NEGATIVE_INFINITY).toIntSizeOrNull()
        )
        assertEquals(
            expected = null,
            actual = Size(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY).toIntSizeOrNull()
        )
        assertEquals(expected = IntSize(1, 1), actual = Size(0.5f, 0.5f).toIntSizeOrNull())
        assertEquals(expected = IntSize(1, 1), actual = Size(1f, 1f).toIntSizeOrNull())
        assertEquals(expected = IntSize(1, 1), actual = Size(1.4f, 1.4f).toIntSizeOrNull())
        assertEquals(expected = IntSize(2, 2), actual = Size(1.5f, 1.5f).toIntSizeOrNull())
    }

    @Test
    fun testToLogString() {
        assertEquals(expected = "Unspecified", actual = Size.Unspecified.toLogString())
        assertEquals(expected = "0.0x0.0", actual = Size.Zero.toLogString())
        assertEquals(expected = "-1.0x-1.0", actual = Size(-1f, -1f).toLogString())
        assertEquals(expected = "1.5x1.5", actual = Size(1.5f, 1.5f).toLogString())
    }

    @Test
    fun testIntSizeIsEmpty() {
        assertEquals(expected = true, actual = IntSize(-1, 0).isEmpty())
        assertEquals(expected = true, actual = IntSize(0, -1).isEmpty())
        assertEquals(expected = true, actual = IntSize(-1, -1).isEmpty())
        assertEquals(expected = true, actual = IntSize(0, 0).isEmpty())
        assertEquals(expected = true, actual = IntSize(1, 0).isEmpty())
        assertEquals(expected = true, actual = IntSize(0, 1).isEmpty())
        assertEquals(expected = false, actual = IntSize(1, 1).isEmpty())
    }

    @Test
    fun testIntSizeToLogString() {
        assertEquals(expected = "0x0", actual = IntSize.Zero.toLogString())
        assertEquals(expected = "-101x-202", actual = IntSize(-101, -202).toLogString())
        assertEquals(expected = "202x101", actual = IntSize(202, 101).toLogString())
    }

    @Test
    fun testIntSizeToSketchSize() {
        assertEquals(
            expected = com.github.panpf.sketch.util.SketchSize(1, 1),
            actual = IntSize(1, 1).toSketchSize()
        )
        assertEquals(
            expected = com.github.panpf.sketch.util.SketchSize(2, 2),
            actual = IntSize(2, 2).toSketchSize()
        )
    }

    @Test
    fun testSketchSizeToIntSize() {
        assertEquals(
            expected = IntSize(1, 1),
            actual = com.github.panpf.sketch.util.SketchSize(1, 1).toIntSize()
        )
        assertEquals(
            expected = IntSize(2, 2),
            actual = com.github.panpf.sketch.util.SketchSize(2, 2).toIntSize()
        )
    }

    @Test
    fun testSketchSizeToSize() {
        assertEquals(
            expected = Size(1f, 1f),
            actual = com.github.panpf.sketch.util.SketchSize(1, 1).toSize()
        )
        assertEquals(
            expected = Size(2f, 2f),
            actual = com.github.panpf.sketch.util.SketchSize(2, 2).toSize()
        )
    }

    @Test
    fun testConstraintsToIntSizeOrNull() {
        assertEquals(
            expected = null,
            actual = Constraints(maxWidth = 0, maxHeight = 0).toIntSizeOrNull()
        )
        assertEquals(
            expected = null,
            actual = Constraints(maxWidth = 1, maxHeight = 0).toIntSizeOrNull()
        )
        assertEquals(
            expected = null,
            actual = Constraints(maxWidth = 0, maxHeight = 1).toIntSizeOrNull()
        )
        assertEquals(
            expected = null,
            actual = Constraints(maxWidth = 1, maxHeight = Constraints.Infinity).toIntSizeOrNull()
        )
        assertEquals(
            expected = null,
            actual = Constraints(maxWidth = Constraints.Infinity, maxHeight = 1).toIntSizeOrNull()
        )
        assertEquals(
            expected = IntSize(1, 1),
            actual = Constraints(maxWidth = 1, maxHeight = 1).toIntSizeOrNull()
        )
        assertEquals(
            expected = IntSize(2, 2),
            actual = Constraints(maxWidth = 2, maxHeight = 2).toIntSizeOrNull()
        )
    }

    @Test
    fun testConstraintsToRequestSize() {
        assertEquals(
            expected = IntSize(0, 0),
            actual = Constraints(maxWidth = 0, maxHeight = 0).toRequestSize()
        )
        assertEquals(
            expected = IntSize(1, 0),
            actual = Constraints(maxWidth = 1, maxHeight = 0).toRequestSize()
        )
        assertEquals(
            expected = IntSize(0, 1),
            actual = Constraints(maxWidth = 0, maxHeight = 1).toRequestSize()
        )
        assertEquals(
            expected = IntSize(1, 0),
            actual = Constraints(maxWidth = 1, maxHeight = Constraints.Infinity).toRequestSize()
        )
        assertEquals(
            expected = IntSize(0, 1),
            actual = Constraints(maxWidth = Constraints.Infinity, maxHeight = 1).toRequestSize()
        )
        assertEquals(
            expected = IntSize(1, 1),
            actual = Constraints(maxWidth = 1, maxHeight = 1).toRequestSize()
        )
        assertEquals(
            expected = IntSize(2, 2),
            actual = Constraints(maxWidth = 2, maxHeight = 2).toRequestSize()
        )
    }

    @Test
    fun testPainterFindLeafPainter() {
        val painter1 = ColorPainter(Color.White)
        val painter2 = SizeColorPainter(Color.Blue, Size(100f, 100f))

        assertSame(expected = painter1, actual = painter1.findLeafPainter())
        assertSame(expected = painter2, actual = painter2.findLeafPainter())

        CrossfadePainter(start = painter1, end = painter2).apply {
            assertSame(expected = painter2, actual = this.findLeafPainter())
        }
        CrossfadePainter(start = painter2, end = painter1).apply {
            assertSame(expected = painter1, actual = this.findLeafPainter())
        }
    }

    @Test
    fun testSimpleName() {
        assertEquals(expected = "Srgb", actual = ColorSpaces.Srgb.simpleName)
        assertEquals(expected = "LinearSrgb", actual = ColorSpaces.LinearSrgb.simpleName)
        assertEquals(expected = "ExtendedSrgb", actual = ColorSpaces.ExtendedSrgb.simpleName)
        assertEquals(
            expected = "LinearExtendedSrgb",
            actual = ColorSpaces.LinearExtendedSrgb.simpleName
        )
        assertEquals(expected = "Bt709", actual = ColorSpaces.Bt709.simpleName)
        assertEquals(expected = "Bt2020", actual = ColorSpaces.Bt2020.simpleName)
        assertEquals(expected = "DciP3", actual = ColorSpaces.DciP3.simpleName)
        assertEquals(expected = "DisplayP3", actual = ColorSpaces.DisplayP3.simpleName)
        assertEquals(expected = "Ntsc1953", actual = ColorSpaces.Ntsc1953.simpleName)
        assertEquals(expected = "SmpteC", actual = ColorSpaces.SmpteC.simpleName)
        assertEquals(expected = "AdobeRgb", actual = ColorSpaces.AdobeRgb.simpleName)
        assertEquals(expected = "ProPhotoRgb", actual = ColorSpaces.ProPhotoRgb.simpleName)
        assertEquals(expected = "Aces", actual = ColorSpaces.Aces.simpleName)
        assertEquals(expected = "Acescg", actual = ColorSpaces.Acescg.simpleName)
        assertEquals(expected = "CieXyz", actual = ColorSpaces.CieXyz.simpleName)
        assertEquals(expected = "CieLab", actual = ColorSpaces.CieLab.simpleName)
        assertEquals(expected = "Oklab", actual = ColorSpaces.Oklab.simpleName)
    }

    @Test
    fun testPainterFindDeepestPainter() {
        val painter1 = ColorPainter(Color.White)
        val painter2 = SizeColorPainter(Color.Blue, Size(100f, 100f))

        assertSame(expected = painter1, actual = painter1.findDeepestPainter())
        assertSame(expected = painter2, actual = painter2.findDeepestPainter())

        CrossfadePainter(start = painter1, end = painter2).apply {
            assertSame(expected = painter2, actual = this.findDeepestPainter())
        }
        CrossfadePainter(start = painter2, end = painter1).apply {
            assertSame(expected = painter1, actual = this.findDeepestPainter())
        }

        PainterWrapper(painter1).apply {
            assertSame(expected = painter1, actual = this.findDeepestPainter())
        }
        PainterWrapper(painter2).apply {
            assertSame(expected = painter2, actual = this.findDeepestPainter())
        }
    }

    @Test
    fun testAnyAsOrNull() {
        assertNotNull(IOException().asOrNull<Exception>())
        assertNull((null as Exception?).asOrNull<Exception>())
        assertFailsWith(ClassCastException::class) {
            Throwable() as Exception
        }
        assertNull(Throwable().asOrNull<Exception>())
    }

    @Test
    fun testToHexString() {
        val any = Any()
        assertEquals(
            expected = any.hashCode().toString(16),
            actual = any.toHexString()
        )
    }

    @Test
    fun testImageBitmapToLogString() {
        val composeBitmap = createBitmap(101, 202).toComposeBitmap()
        assertEquals(
            expected = "ImageBitmap@${composeBitmap.toHexString()}(101x202,Argb8888,Srgb)",
            actual = composeBitmap.toLogString()
        )
    }
}