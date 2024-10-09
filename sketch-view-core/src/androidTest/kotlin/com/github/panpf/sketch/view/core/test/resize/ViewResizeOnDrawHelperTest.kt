package com.github.panpf.sketch.view.core.test.resize

import android.graphics.Color
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.drawable.ResizeDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ViewResizeOnDrawHelper
import com.github.panpf.sketch.test.utils.SizeColorDrawable
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class ViewResizeOnDrawHelperTest {

    @Test
    fun test() {
        assertSame(
            expected = ViewResizeOnDrawHelper,
            actual = ViewResizeOnDrawHelper
        )
    }

    @Test
    fun testKey() {
        assertEquals(
            expected = "ViewResizeOnDrawHelper",
            actual = ViewResizeOnDrawHelper.key
        )
    }

    @Test
    fun testResize() {
        val context = getTestContext()
        val helper = ViewResizeOnDrawHelper

        helper.resize(
            request = ImageRequest(context, "http://sample.com/sample.jpeg") {
                scale(LongImageScaleDecider())
            },
            size = Size(200, 200),
            image = SizeColorDrawable(Color.RED, Size(300, 500)).asImage()
        ).apply {
            assertEquals(
                expected = ResizeDrawable(
                    drawable = SizeColorDrawable(Color.RED, Size(300, 500)),
                    size = Size(200, 200),
                    scale = Scale.CENTER_CROP
                ).asImage(),
                actual = this
            )
        }

        helper.resize(
            request = ImageRequest(context, "http://sample.com/sample.jpeg") {
                scale(LongImageScaleDecider())
            },
            size = Size(200, 200),
            image = SizeColorDrawable(Color.RED, Size(300, 1500)).asImage()
        ).apply {
            assertEquals(
                expected = ResizeDrawable(
                    drawable = SizeColorDrawable(Color.RED, Size(300, 1500)),
                    size = Size(200, 200),
                    scale = Scale.START_CROP
                ).asImage(),
                actual = this
            )
        }

        helper.resize(
            request = ImageRequest(context, "http://sample.com/sample.jpeg") {
                scale(Scale.END_CROP)
            },
            size = Size(200, 200),
            image = SizeColorDrawable(Color.RED, Size(300, 500)).asImage()
        ).apply {
            assertEquals(
                expected = ResizeDrawable(
                    drawable = SizeColorDrawable(Color.RED, Size(300, 500)),
                    size = Size(200, 200),
                    scale = Scale.END_CROP
                ).asImage(),
                actual = this
            )
        }

        helper.resize(
            request = ImageRequest(context, "http://sample.com/sample.jpeg") {
                scale(Scale.END_CROP)
            },
            size = Size(200, 200),
            image = SizeColorDrawable(Color.RED, Size(300, 1500)).asImage()
        ).apply {
            assertEquals(
                expected = ResizeDrawable(
                    drawable = SizeColorDrawable(Color.RED, Size(300, 1500)),
                    size = Size(200, 200),
                    scale = Scale.END_CROP
                ).asImage(),
                actual = this
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ViewResizeOnDrawHelper
        val element11 = ViewResizeOnDrawHelper

        assertSame(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ViewResizeOnDrawHelper",
            actual = ViewResizeOnDrawHelper.toString()
        )
    }
}