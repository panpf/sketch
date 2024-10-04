@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.resources.common.test.state

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.equitablePainterResource
import com.github.panpf.sketch.state.IconPainterStateImage
import com.github.panpf.sketch.state.rememberIconPainterStateImage
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.asEquitable
import com.github.panpf.sketch.test.utils.compose.core.resources.Res
import com.github.panpf.sketch.test.utils.compose.core.resources.desert
import com.github.panpf.sketch.test.utils.compose.core.resources.moon
import kotlin.test.Test
import kotlin.test.assertEquals

class IconPainterStateImageComposeResourcesTest {

    @Test
    fun testRememberIconPainterStateImageWithPainterIcon() {
        // One parameters
        runComposeUiTest {
            setContent {
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
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
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Color.Green,
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Res.drawable.desert,
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = equitablePainterResource(Res.drawable.desert),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
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
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Color.Green,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Res.drawable.desert,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = equitablePainterResource(Res.drawable.desert),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Res.drawable.desert,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = equitablePainterResource(Res.drawable.desert),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
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
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Res.drawable.desert,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = equitablePainterResource(Res.drawable.desert),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testRememberIconPainterStateImageWithDrawableResourcesIcon() {
        // One parameters
        runComposeUiTest {
            setContent {
                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
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
                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    background = ColorPainter(Color.Green).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    background = Color.Green,
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    background = Res.drawable.desert,
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = equitablePainterResource(Res.drawable.desert),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
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
                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    background = Color.Green,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    background = Res.drawable.desert,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = equitablePainterResource(Res.drawable.desert),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    background = Res.drawable.desert,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = equitablePainterResource(Res.drawable.desert),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
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
                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconPainterStateImage(
                    icon = Res.drawable.moon,
                    background = Res.drawable.desert,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = equitablePainterResource(Res.drawable.desert),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
            }
        }
    }
}