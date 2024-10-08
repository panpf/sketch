@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.state

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.state.PainterStateImage
import com.github.panpf.sketch.state.rememberPainterStateImage
import kotlin.test.Test
import kotlin.test.assertEquals

class PainterStateImageTest {

    @Test
    fun testRememberPainterStateImage() {
        runComposeUiTest {
            setContent {
                rememberPainterStateImage(ColorPainter(Color.Blue).asEquitable()).apply {
                    assertEquals(
                        expected = PainterStateImage(ColorPainter(Color.Blue).asEquitable()),
                        actual = this
                    )
                }
            }
        }
    }

    // TODO test
}