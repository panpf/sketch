@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.state

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.state.ColorPainterStateImage
import com.github.panpf.sketch.state.rememberColorPainterStateImage
import com.github.panpf.sketch.state.rememberColorPainterStateImageWithInt
import com.github.panpf.sketch.state.rememberColorPainterStateImageWithLong
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorPainterStateImageTest {

    @Test
    fun testRememberColorPainterStateImage() {
        runComposeUiTest {
            setContent {
                rememberColorPainterStateImage(Color.Blue).apply {
                    assertEquals(
                        expected = ColorPainterStateImage(Color.Blue),
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testRememberColorPainterStateImageWithLong() {
        runComposeUiTest {
            setContent {
                rememberColorPainterStateImageWithLong(0xFFFFFFFF).apply {
                    assertEquals(
                        expected = ColorPainterStateImage(Color(0xFFFFFFFF)),
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testRememberColorPainterStateImageWithInt() {
        runComposeUiTest {
            setContent {
                rememberColorPainterStateImageWithInt(Color.Blue.toArgb()).apply {
                    assertEquals(
                        expected = ColorPainterStateImage(Color(Color.Blue.toArgb())),
                        actual = this
                    )
                }
            }
        }
    }

    // TODO test
}