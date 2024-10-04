@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.resources.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.EquitablePainter
import com.github.panpf.sketch.painter.IconPainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.equitablePainterResource
import com.github.panpf.sketch.painter.rememberIconPainter
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.compose.core.resources.Res
import com.github.panpf.sketch.test.utils.compose.core.resources.desert
import com.github.panpf.sketch.test.utils.compose.core.resources.moon
import org.jetbrains.compose.resources.DrawableResource
import kotlin.test.Test
import kotlin.test.assertEquals

class IconPainterComposeResourcesTest {

    @Test
    fun testRememberIconPainter() {
        runComposeUiTest {
            setContent {
                /*
                 * icon: EquitablePainter, background: DrawableResource
                 */
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asEquitable(Color.Gray),
                    background = null as DrawableResource?
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
                    background = Res.drawable.moon
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asEquitable(Color.Gray),
                            background = equitablePainterResource(Res.drawable.moon),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asEquitable(Color.Gray),
                    background = Res.drawable.moon,
                    iconSize = Size(101f, 202f)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asEquitable(Color.Gray),
                            background = equitablePainterResource(Res.drawable.moon),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asEquitable(Color.Gray),
                    background = Res.drawable.moon,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asEquitable(Color.Gray),
                            background = equitablePainterResource(Res.drawable.moon),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                /*
                 * icon: DrawableResource, background: EquitablePainter
                 */
                rememberIconPainter(
                    icon = Res.drawable.moon,
                    background = null as EquitablePainter?
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = null,
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = Res.drawable.moon,
                    background = ColorPainter(Color.Gray).asEquitable()
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = Res.drawable.moon,
                    background = ColorPainter(Color.Gray).asEquitable(),
                    iconSize = Size(101f, 202f)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = Res.drawable.moon,
                    background = ColorPainter(Color.Gray).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                /*
                 * icon: DrawableResource, background: Color
                 */
                rememberIconPainter(
                    icon = Res.drawable.moon,
                    background = null as Color?
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = null,
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = Res.drawable.moon,
                    background = Color.Gray
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = Res.drawable.moon,
                    background = Color.Gray,
                    iconSize = Size(101f, 202f)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = Res.drawable.moon,
                    background = Color.Gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = ColorPainter(Color.Gray).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                /*
                 * icon: DrawableResource, background: DrawableResource
                 */
                rememberIconPainter(
                    icon = Res.drawable.moon,
                    background = null as DrawableResource?
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = null,
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = Res.drawable.moon,
                    background = Res.drawable.desert
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = equitablePainterResource(Res.drawable.desert),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = Res.drawable.moon,
                    background = Res.drawable.desert,
                    iconSize = Size(101f, 202f)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = equitablePainterResource(Res.drawable.desert),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = Res.drawable.moon,
                    background = Res.drawable.desert,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = equitablePainterResource(Res.drawable.desert),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                /*
                 * icon: DrawableResource
                 */
                rememberIconPainter(
                    icon = Res.drawable.moon,
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            background = null,
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = Res.drawable.moon,
                    iconSize = Size(101f, 202f)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = Res.drawable.moon,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = equitablePainterResource(Res.drawable.moon),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
            }
        }
    }
}