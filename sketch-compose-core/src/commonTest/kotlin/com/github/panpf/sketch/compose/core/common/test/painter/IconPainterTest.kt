@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.IconPainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.rememberIconPainter
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.asEquitable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class IconPainterTest {

    @Test
    fun testRememberIconPainter() {
        // One parameters
        runComposeUiTest {
            setContent {
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
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
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Color.Green,
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
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
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Color.Green,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
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
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }

                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
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
    fun testConstructor() {
        IconPainter(
            icon = SizeColorPainter(Color.Green, Size(100f, 100f)).asEquitable(),
        ).apply {
            assertEquals(
                expected = SizeColorPainter(Color.Green, Size(100f, 100f)).asEquitable(),
                actual = icon
            )
            assertNull(background)
            assertNull(iconSize)
            assertNull(iconTint)
        }

        IconPainter(
            icon = SizeColorPainter(Color.Green, Size(100f, 100f)).asEquitable(),
            background = ColorPainter(Color.Blue).asEquitable(),
            iconSize = Size(69f, 44f),
            iconTint = Color.Red,
        ).apply {
            assertEquals(
                expected = SizeColorPainter(Color.Green, Size(100f, 100f)).asEquitable(),
                actual = icon
            )
            assertEquals(ColorPainter(Color.Blue).asEquitable(), background)
            assertEquals(Size(69f, 44f), iconSize)
            assertEquals(Color.Red, iconTint)
        }

        assertFailsWith(IllegalArgumentException::class) {
            IconPainter(icon = ColorPainter(Color.Green).asEquitable())
        }
        assertFailsWith(IllegalArgumentException::class) {
            IconPainter(icon = ColorPainter(Color.Green).asEquitable(), iconSize = Size(-1f, -1f))
        }
        IconPainter(
            icon = ColorPainter(Color.Green).asEquitable(), iconSize = Size(100f, 100f)
        )

        assertFailsWith(IllegalArgumentException::class) {
            IconPainter(icon = SizeColorPainter(Color.Green, Size(-1f, -1f)).asEquitable())
        }
        assertFailsWith(IllegalArgumentException::class) {
            IconPainter(
                icon = SizeColorPainter(Color.Green, Size(-1f, -1f)).asEquitable(),
                iconSize = Size(-1f, -1f)
            )
        }
        IconPainter(
            icon = SizeColorPainter(Color.Green, Size(-1f, -1f)).asEquitable(),
            iconSize = Size(100f, 100f)
        )
        IconPainter(
            icon = SizeColorPainter(Color.Green, Size(100f, 100f)).asEquitable(),
        )
    }

    @Test
    fun testDraw() {
        // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
    }

    @Test
    fun testIntrinsicSize() {
        IconPainter(
            icon = SizeColorPainter(Color.Red, Size(101f, 202f)).asEquitable(),
            background = null
        ).apply {
            assertEquals(expected = Size.Unspecified, actual = intrinsicSize)
        }

        IconPainter(
            icon = SizeColorPainter(Color.Red, Size(101f, 202f)).asEquitable(),
            background = SizeColorPainter(Color.Gray, Size(1000f, 500f)).asEquitable()
        ).apply {
            assertEquals(expected = Size.Unspecified, actual = intrinsicSize)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = IconPainter(
            icon = SizeColorPainter(Color.Red, Size(100f, 100f)).asEquitable(),
        )
        val element11 = IconPainter(
            icon = SizeColorPainter(Color.Red, Size(100f, 100f)).asEquitable(),
        )
        val element2 = IconPainter(
            icon = SizeColorPainter(Color.Green, Size(100f, 100f)).asEquitable(),
        )
        val element3 = IconPainter(
            icon = SizeColorPainter(Color.Red, Size(100f, 100f)).asEquitable(),
            background = ColorPainter(Color.Gray).asEquitable(),
        )
        val element4 = IconPainter(
            icon = SizeColorPainter(Color.Red, Size(100f, 100f)).asEquitable(),
            iconSize = Size(69f, 44f),
        )
        val element5 = IconPainter(
            icon = SizeColorPainter(Color.Red, Size(100f, 100f)).asEquitable(),
            iconTint = Color.Blue,
        )

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
    fun testKey() {
        assertEquals(
            expected = "IconPainter(SizeColorPainter(color=-65536, size=100.0x100.0):SizeColorPainter(color=-65536, size=100.0x100.0),ColorPainter(-7829368),69.0x44.0,-16776961)",
            actual = IconPainter(
                icon = SizeColorPainter(
                    Color.Red,
                    Size(100f, 100f)
                ).asEquitable(),
                background = ColorPainter(Color.Gray).asEquitable(),
                iconSize = Size(69f, 44f),
                iconTint = Color.Blue
            ).key
        )
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "IconPainter(icon=EquitablePainter(painter=SizeColorPainter(color=-65536, size=100.0x100.0), equalityKey=SizeColorPainter(color=-65536, size=100.0x100.0)), background=EquitablePainter(painter=ColorPainter(color=-7829368), equalityKey=18413117194201202688), iconSize=69.0x44.0, iconTint=-16776961)",
            actual = IconPainter(
                icon = SizeColorPainter(
                    Color.Red,
                    Size(100f, 100f)
                ).asEquitable(),
                background = ColorPainter(Color.Gray).asEquitable(),
                iconSize = Size(69f, 44f),
                iconTint = Color.Blue
            ).toString()
        )
    }
}