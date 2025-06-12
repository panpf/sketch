package com.github.panpf.sketch.compose.core.common.test.resize

import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.compose.core.common.test.target.TestGenericComposeTarget
import com.github.panpf.sketch.painter.ResizePainter
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.ComposeResizeOnDrawHelper
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.SketchSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class ComposeResizeOnDrawHelperTest {

    @Test
    fun test() {
        assertSame(
            expected = ComposeResizeOnDrawHelper,
            actual = ComposeResizeOnDrawHelper
        )
    }

    @Test
    fun testKey() {
        assertEquals(
            expected = "ComposeResizeOnDrawHelper",
            actual = ComposeResizeOnDrawHelper.key
        )
    }

    @Test
    fun testResize() {
        val context = getTestContext()
        val helper = ComposeResizeOnDrawHelper

        helper.resize(
            request = ImageRequest(context, "http://sample.com/sample.jpeg") {
            },
            size = SketchSize(200, 200),
            image = SizeColorPainter(Color.Red, Size(300f, 500f)).asImage()
        ).apply {
            assertEquals(
                expected = SizeColorPainter(Color.Red, Size(300f, 500f)).asImage(),
                actual = this
            )
        }

        helper.resize(
            request = ImageRequest(context, "http://sample.com/sample.jpeg") {
                target(TestGenericComposeTarget())
            },
            size = SketchSize(200, 200),
            image = SizeColorPainter(Color.Red, Size(300f, 500f)).asImage()
        ).apply {
            assertEquals(
                expected = ResizePainter(
                    painter = SizeColorPainter(Color.Red, Size(300f, 500f)),
                    size = Size(200f, 200f),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center,
                ).asImage(),
                actual = this
            )
        }

        helper.resize(
            request = ImageRequest(context, "http://sample.com/sample.jpeg") {
                target(
                    TestGenericComposeTarget(
                        contentScale = ContentScale.None,
                        alignment = Alignment.BottomEnd
                    )
                )
            },
            size = SketchSize(200, 200),
            image = SizeColorPainter(Color.Red, Size(300f, 1500f)).asImage()
        ).apply {
            assertEquals(
                expected = ResizePainter(
                    painter = SizeColorPainter(Color.Red, Size(300f, 1500f)),
                    size = Size(200f, 200f),
                    contentScale = ContentScale.None,
                    alignment = Alignment.BottomEnd,
                ).asImage(),
                actual = this
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ComposeResizeOnDrawHelper
        val element11 = ComposeResizeOnDrawHelper

        assertSame(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ComposeResizeOnDrawHelper",
            actual = ComposeResizeOnDrawHelper.toString()
        )
    }
}