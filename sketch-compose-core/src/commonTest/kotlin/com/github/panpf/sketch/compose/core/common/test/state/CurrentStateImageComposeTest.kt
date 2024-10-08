@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.state

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.state.CurrentStateImage
import com.github.panpf.sketch.state.PainterStateImage
import kotlin.test.Test
import kotlin.test.assertEquals

class CurrentStateImageComposeTest {

    @Test
    fun testCurrentStateImage() {
        runComposeUiTest {
            setContent {
                CurrentStateImage(ColorPainter(Color.Blue).asEquitable()).apply {
                    assertEquals(
                        expected = CurrentStateImage(PainterStateImage(ColorPainter(Color.Blue).asEquitable())),
                        actual = this
                    )
                }

                CurrentStateImage(Color.Blue).apply {
                    assertEquals(
                        expected = CurrentStateImage(PainterStateImage(ColorPainter(Color.Blue).asEquitable())),
                        actual = this
                    )
                }
            }
        }
    }
}