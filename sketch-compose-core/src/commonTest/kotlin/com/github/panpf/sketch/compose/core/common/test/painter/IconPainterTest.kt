@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.EquitablePainter
import com.github.panpf.sketch.painter.IconPainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.rememberIconPainter
import com.github.panpf.sketch.test.utils.SizeColorPainter
import kotlin.test.Test
import kotlin.test.assertEquals

class IconPainterTest {

    @Test
    fun testRememberIconPainter() {
        runComposeUiTest {
            setContent {
                /*
                 * icon: EquitablePainter, background: EquitablePainter
                 */
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asEquitable(Color.Gray),
                    background = null as EquitablePainter?
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asEquitable(Color.Gray),
                            background = null,
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asEquitable(Color.Gray),
                    background = ColorPainter(Color.Gray).asEquitable()
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asEquitable(Color.Gray),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asEquitable(Color.Gray),
                    background = ColorPainter(Color.Gray).asEquitable(),
                    iconSize = Size(101f, 202f)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asEquitable(Color.Gray),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asEquitable(Color.Gray),
                    background = ColorPainter(Color.Gray).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asEquitable(Color.Gray),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                /*
                 * icon: EquitablePainter, background: Color
                 */
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asEquitable(Color.Gray),
                    background = null as Color?
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asEquitable(Color.Gray),
                            background = null,
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asEquitable(Color.Gray),
                    background = Color.Gray
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asEquitable(Color.Gray),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asEquitable(Color.Gray),
                    background = Color.Gray,
                    iconSize = Size(101f, 202f)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asEquitable(Color.Gray),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asEquitable(Color.Gray),
                    background = Color.Gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asEquitable(Color.Gray),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                /*
                 * icon: EquitablePainter
                 */
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asEquitable(Color.Gray),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asEquitable(Color.Gray),
                            background = null,
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asEquitable(Color.Gray),
                    iconSize = Size(101f, 202f)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asEquitable(Color.Gray),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asEquitable(Color.Gray),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asEquitable(Color.Gray),
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