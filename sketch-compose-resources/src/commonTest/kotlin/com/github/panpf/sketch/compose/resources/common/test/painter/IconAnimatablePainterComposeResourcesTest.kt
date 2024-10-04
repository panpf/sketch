@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.resources.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.IconAnimatablePainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.equitablePainterResource
import com.github.panpf.sketch.painter.rememberIconAnimatablePainter
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.asAnimatablePainter
import com.github.panpf.sketch.test.utils.compose.core.resources.Res
import com.github.panpf.sketch.test.utils.compose.core.resources.moon
import org.jetbrains.compose.resources.DrawableResource
import kotlin.test.Test
import kotlin.test.assertEquals

class IconAnimatablePainterComposeResourcesTest {

    @Test
    fun testRememberIconAnimatablePainter() {
        runComposeUiTest {
            setContent {
                /*
                 * icon: EquitablePainter, background: DrawableResource
                 */
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                    background = null as DrawableResource?
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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

                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                    background = Res.drawable.moon
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitable(Color.Gray),
                            background = equitablePainterResource(Res.drawable.moon),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                    background = Res.drawable.moon,
                    iconSize = Size(101f, 202f)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitable(Color.Gray),
                            background = equitablePainterResource(Res.drawable.moon),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                        .asAnimatablePainter()
                        .asEquitable(Color.Gray),
                    background = Res.drawable.moon,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f))
                                .asAnimatablePainter()
                                .asEquitable(Color.Gray),
                            background = equitablePainterResource(Res.drawable.moon),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

//                /*
//                 * icon: DrawableResource, background: EquitablePainter
//                 */
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                    background = null as EquitablePainter?
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            background = null,
//                            iconSize = null,
//                            iconTint = null
//                        ),
//                        actual = this
//                    )
//                }
//
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                    background = ColorPainter(Color.Gray).asEquitable()
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            background = ColorPainter(Color.Gray).asEquitable(),
//                            iconSize = null,
//                            iconTint = null
//                        ),
//                        actual = this
//                    )
//                }
//
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                    background = ColorPainter(Color.Gray).asEquitable(),
//                    iconSize = Size(101f, 202f)
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            background = ColorPainter(Color.Gray).asEquitable(),
//                            iconSize = Size(101f, 202f),
//                            iconTint = null
//                        ),
//                        actual = this
//                    )
//                }
//
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                    background = ColorPainter(Color.Gray).asEquitable(),
//                    iconSize = Size(101f, 202f),
//                    iconTint = Color.Blue
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            background = ColorPainter(Color.Gray).asEquitable(),
//                            iconSize = Size(101f, 202f),
//                            iconTint = Color.Blue
//                        ),
//                        actual = this
//                    )
//                }
//
//                /*
//                 * icon: DrawableResource, background: Color
//                 */
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                    background = null as Color?
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            background = null,
//                            iconSize = null,
//                            iconTint = null
//                        ),
//                        actual = this
//                    )
//                }
//
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                    background = Color.Gray
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            background = ColorPainter(Color.Gray).asEquitable(),
//                            iconSize = null,
//                            iconTint = null
//                        ),
//                        actual = this
//                    )
//                }
//
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                    background = Color.Gray,
//                    iconSize = Size(101f, 202f)
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            background = ColorPainter(Color.Gray).asEquitable(),
//                            iconSize = Size(101f, 202f),
//                            iconTint = null
//                        ),
//                        actual = this
//                    )
//                }
//
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                    background = Color.Gray,
//                    iconSize = Size(101f, 202f),
//                    iconTint = Color.Blue
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            background = ColorPainter(Color.Gray).asEquitable(),
//                            iconSize = Size(101f, 202f),
//                            iconTint = Color.Blue
//                        ),
//                        actual = this
//                    )
//                }
//
//                /*
//                 * icon: DrawableResource, background: DrawableResource
//                 */
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                    background = null as DrawableResource?
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            background = null,
//                            iconSize = null,
//                            iconTint = null
//                        ),
//                        actual = this
//                    )
//                }
//
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                    background = Res.drawable.desert
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            background = equitablePainterResource(Res.drawable.desert),
//                            iconSize = null,
//                            iconTint = null
//                        ),
//                        actual = this
//                    )
//                }
//
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                    background = Res.drawable.desert,
//                    iconSize = Size(101f, 202f)
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            background = equitablePainterResource(Res.drawable.desert),
//                            iconSize = Size(101f, 202f),
//                            iconTint = null
//                        ),
//                        actual = this
//                    )
//                }
//
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                    background = Res.drawable.desert,
//                    iconSize = Size(101f, 202f),
//                    iconTint = Color.Blue
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            background = equitablePainterResource(Res.drawable.desert),
//                            iconSize = Size(101f, 202f),
//                            iconTint = Color.Blue
//                        ),
//                        actual = this
//                    )
//                }
//
//                /*
//                 * icon: DrawableResource
//                 */
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            background = null,
//                            iconSize = null,
//                            iconTint = null
//                        ),
//                        actual = this
//                    )
//                }
//
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                    iconSize = Size(101f, 202f)
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            iconSize = Size(101f, 202f),
//                            iconTint = null
//                        ),
//                        actual = this
//                    )
//                }
//
//                rememberIconAnimatablePainter(
//                    icon = Res.drawable.moon,
//                    iconTint = Color.Blue
//                ).apply {
//                    assertEquals(
//                        expected = IconAnimatablePainter(
//                            icon = equitablePainterResource(Res.drawable.moon),
//                            iconTint = Color.Blue
//                        ),
//                        actual = this
//                    )
//                }
            }
        }
    }
}