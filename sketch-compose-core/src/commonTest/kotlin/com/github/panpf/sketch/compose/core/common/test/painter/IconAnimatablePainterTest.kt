@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.IconAnimatablePainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.rememberIconAnimatablePainter
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.asAnimatablePainter
import com.github.panpf.sketch.test.utils.asEquitableWithThis
import kotlin.test.Test
import kotlin.test.assertEquals

class IconAnimatablePainterTest {

    @Test
    fun testRememberIconAnimatablePainter() {
        // One parameters
        runComposeUiTest {
            setContent {
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitableWithThis(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitableWithThis(),
                            background = null,
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
            }
        }

        // Two parameters
        runComposeUiTest {
            setContent {
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitableWithThis(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitableWithThis(),
                            background = null,
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
            }
        }

        // Three parameters
        runComposeUiTest {
            setContent {
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitableWithThis(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
            }
        }

        // Four parameters
        runComposeUiTest {
            setContent {
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
            }
        }
    }

    // TODO test
}