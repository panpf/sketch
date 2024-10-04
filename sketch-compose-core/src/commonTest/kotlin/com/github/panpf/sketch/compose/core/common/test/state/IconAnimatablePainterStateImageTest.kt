@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.state

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.EquitablePainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.state.IconAnimatablePainterStateImage
import com.github.panpf.sketch.state.rememberIconAnimatablePainterStateImage
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.asAnimatablePainter
import kotlin.test.Test
import kotlin.test.assertEquals

class IconAnimatablePainterStateImageTest {

    @Test
    fun testRememberIconAnimatablePainterStateImage() {
        runComposeUiTest {
            setContent {
                /*
                 * icon: EquitablePainter, background: EquitablePainter
                 */
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                    background = null as EquitablePainter?
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitable(Color.Gray),
                            background = null,
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                    background = ColorPainter(Color.Gray).asEquitable()
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitable(Color.Gray),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                    background = ColorPainter(Color.Gray).asEquitable(),
                    iconSize = Size(101f, 202f)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitable(Color.Gray),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                    background = ColorPainter(Color.Gray).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                    background = null as Color?
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitable(Color.Gray),
                            background = null,
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                    background = Color.Gray
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitable(Color.Gray),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                    background = Color.Gray,
                    iconSize = Size(101f, 202f)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitable(Color.Gray),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                    background = Color.Gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitable(Color.Gray),
                            background = null,
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                    iconSize = Size(101f, 202f)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitable(Color.Gray),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
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