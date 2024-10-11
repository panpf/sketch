@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.state

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.PainterImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.painter.IconPainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.IconPainterStateImage
import com.github.panpf.sketch.state.rememberIconPainterStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.asEquitable
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class IconPainterStateImageTest {

    @Test
    fun testRememberIconPainterStateImage() {
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
            }
        }
    }

    @Test
    fun testKey() {
        val iconPainter = SizeColorPainter(Color.Red).asEquitable()
        IconPainterStateImage(
            icon = iconPainter,
            background = ColorPainter(Color.Gray).asEquitable(),
            iconSize = Size(44f, 67f),
            iconTint = Color.Blue
        ).apply {
            assertEquals(
                expected = "IconPainterStateImage(icon=EquitablePainter('SizeColorPainter(color=18446462598732840960, size=Size.Unspecified)'),background=EquitablePainter('18413117194201202688'),iconSize=Size(44.0, 67.0),iconTint=18374687574888284160)",
                actual = key
            )
        }
    }

    @Test
    fun testGetImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val iconPainter =
            ColorPainter(Color.Yellow).size(Size(100f, 100f)).asEquitable(Color.Yellow)
        val backgroundPainter = ColorPainter(Color.Green).asEquitable()

        IconPainterStateImage(
            icon = iconPainter,
        ).getImage(sketch, request, null).asOrThrow<PainterImage>()
            .painter.asOrThrow<IconPainter>().apply {
                assertEquals(iconPainter, icon)
                assertNull(background)
                assertNull(iconSize)
                assertNull(iconTint)
            }

        IconPainterStateImage(
            icon = iconPainter,
            background = backgroundPainter,
            iconSize = Size(40f, 40f),
            iconTint = Color.Yellow
        ).getImage(sketch, request, null).asOrThrow<PainterImage>()
            .painter.asOrThrow<IconPainter>().apply {
                assertEquals(iconPainter, icon)
                assertEquals(backgroundPainter, background)
                assertEquals(Size(40f, 40f), iconSize)
                assertEquals(Color.Yellow, iconTint)
            }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = IconPainterStateImage(icon = SizeColorPainter(Color.Red).asEquitable())
        val element11 = element1.copy()
        val element2 = element1.copy(icon = SizeColorPainter(Color.Green).asEquitable())
        val element3 = element1.copy(background = SizeColorPainter(Color.Gray).asEquitable())
        val element4 = element1.copy(iconSize = Size(100f, 100f))
        val element5 = element1.copy(iconTint = Color.Green)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element2, element5)
        assertNotEquals(element3, element4)
        assertNotEquals(element3, element5)
        assertNotEquals(element4, element5)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element5.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element5.hashCode())
        assertNotEquals(element4.hashCode(), element5.hashCode())
    }

    @Test
    fun testToString() {
        IconPainterStateImage(
            icon = SizeColorPainter(Color.Red).asEquitable(),
            background = ColorPainter(Color.Gray).asEquitable(),
            iconSize = Size(44f, 67f),
            iconTint = Color.Blue
        ).apply {
            assertEquals(
                expected = "IconPainterStateImage(icon=EquitablePainter(painter=SizeColorPainter(color=18446462598732840960, size=Size.Unspecified), equalityKey=SizeColorPainter(color=18446462598732840960, size=Size.Unspecified)), background=EquitablePainter(painter=ColorPainter(color=-7829368), equalityKey=18413117194201202688), iconSize=Size(44.0, 67.0), iconTint=18374687574888284160)",
                actual = toString()
            )
        }
    }
}