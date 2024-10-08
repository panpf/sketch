package com.github.panpf.sketch.compose.core.common.test.target

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.ComposeRequestDelegate
import com.github.panpf.sketch.resize.ComposeResizeOnDrawHelper
import com.github.panpf.sketch.target.TestComposeTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestCrossfadeTransition
import com.github.panpf.sketch.transition.ComposeCrossfadeTransition
import com.github.panpf.sketch.transition.CrossfadeTransition
import kotlinx.coroutines.Job
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ComposeTargetTest {

    @Test
    fun testCurrentImage() {
        val composeTarget = TestComposeTarget()
        assertEquals(null, composeTarget.currentImage)

        composeTarget.painter = ColorPainter(Color.Blue)
        assertEquals(
            expected = ColorPainter(Color.Blue).asImage(),
            actual = composeTarget.currentImage
        )

        composeTarget.painter = null
        assertEquals(null, composeTarget.currentImage)
    }

    @Test
    fun testNewRequestDelegate() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)

        val composeTarget = TestComposeTarget()
        val requestDelegate1 = composeTarget.newRequestDelegate(sketch, request, Job())
        val requestDelegate2 = composeTarget.newRequestDelegate(sketch, request, Job())

        assertNotEquals(requestDelegate1, requestDelegate2)
        assertNotSame(requestDelegate1, requestDelegate2)
        assertTrue(
            actual = requestDelegate1 is ComposeRequestDelegate,
            message = "requestDelegate1=$requestDelegate1"
        )
    }

    @Test
    fun testGetResizeOnDrawHelper() {
        val composeTarget = TestComposeTarget()
        val resizeOnDrawHelper1 = composeTarget.getResizeOnDrawHelper()
        val resizeOnDrawHelper2 = composeTarget.getResizeOnDrawHelper()

        assertEquals(resizeOnDrawHelper1, resizeOnDrawHelper2)
        assertSame(resizeOnDrawHelper1, resizeOnDrawHelper2)
        assertTrue(
            actual = resizeOnDrawHelper1 is ComposeResizeOnDrawHelper,
            message = "resizeOnDrawHelper1=$resizeOnDrawHelper1"
        )
    }

    @Test
    fun testConvertTransition() {
        val composeTarget = TestComposeTarget()

        assertEquals(
            expected = ComposeCrossfadeTransition.Factory(
                durationMillis = 2000,
                fadeStart = false,
                preferExactIntrinsicSize = false,
                alwaysUse = true
            ),
            actual = composeTarget.convertTransition(
                CrossfadeTransition.Factory(
                    durationMillis = 2000,
                    fadeStart = false,
                    preferExactIntrinsicSize = false,
                    alwaysUse = true
                )
            )
        )

        assertEquals(
            expected = null,
            actual = composeTarget.convertTransition(
                TestCrossfadeTransition.Factory(
                    durationMillis = 2000,
                    fadeStart = false,
                    preferExactIntrinsicSize = false,
                    alwaysUse = true
                )
            )
        )
    }
}