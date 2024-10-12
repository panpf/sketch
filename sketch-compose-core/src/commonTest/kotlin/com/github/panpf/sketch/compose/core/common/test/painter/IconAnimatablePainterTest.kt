@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.AnimatablePainter
import com.github.panpf.sketch.painter.IconAnimatablePainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.rememberIconAnimatablePainter
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.TestAnimatablePainter
import com.github.panpf.sketch.test.utils.asAnimatablePainter
import com.github.panpf.sketch.test.utils.asEquitable
import com.github.panpf.sketch.test.utils.asEquitableWithThis
import com.github.panpf.sketch.test.utils.block
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

    @Test
    fun testConstructor() {
        IconAnimatablePainter(
            icon = SizeColorPainter(Color.Green, Size(100f, 100f))
                .asAnimatablePainter()
                .asEquitableWithThis()
        ).apply {
            assertTrue(icon is AnimatablePainter)
            assertNull(background)
            assertNull(iconSize)
            assertNull(iconTint)
        }

        IconAnimatablePainter(
            icon = SizeColorPainter(Color.Green, Size(100f, 100f))
                .asAnimatablePainter()
                .asEquitableWithThis(),
            background = ColorPainter(Color.Green).asEquitable(),
            iconSize = Size(69f, 44f),
            iconTint = Color.Red,
        ).apply {
            assertTrue(icon is AnimatablePainter)
            assertEquals(ColorPainter(Color.Green).asEquitable(), background)
            assertEquals(Size(69f, 44f), iconSize)
            assertEquals(Color.Red, iconTint)
        }

        assertFailsWith(IllegalArgumentException::class) {
            IconAnimatablePainter(
                icon = SizeColorPainter(Color.Green, Size(100f, 100f)).asEquitableWithThis()
            )
        }
        assertFailsWith(IllegalArgumentException::class) {
            IconAnimatablePainter(
                icon = SizeColorPainter(Color.Green, Size(100f, 100f)).asAnimatablePainter()
                    .asEquitableWithThis(),
                background = TestAnimatablePainter(ColorPainter(Color.Green)).asEquitableWithThis()
            )
        }
    }

    @Test
    fun testIntrinsicSize() {
        IconAnimatablePainter(
            icon = SizeColorPainter(Color.Red, Size(101f, 202f))
                .asAnimatablePainter()
                .asEquitableWithThis(),
            background = null
        ).apply {
            assertEquals(expected = Size.Unspecified, actual = intrinsicSize)
        }

        IconAnimatablePainter(
            icon = SizeColorPainter(Color.Red, Size(101f, 202f))
                .asAnimatablePainter()
                .asEquitableWithThis(),
            background = SizeColorPainter(Color.Gray, Size(1000f, 500f)).asEquitable()
        ).apply {
            assertEquals(expected = Size.Unspecified, actual = intrinsicSize)
        }
    }

    @Test
    fun testStartStop() = runTest {
        val animatablePainter =
            SizeColorPainter(Color.Yellow, Size(100f, 100f)).asAnimatablePainter()
        val wrapper = IconAnimatablePainter(animatablePainter.asEquitableWithThis())

        assertFalse(actual = animatablePainter.isRunning())
        assertFalse(actual = wrapper.isRunning())

        wrapper.start()
        block(100)
        assertTrue(actual = animatablePainter.isRunning())
        assertTrue(actual = wrapper.isRunning())

        wrapper.start()
        block(100)
        assertTrue(actual = animatablePainter.isRunning())
        assertTrue(actual = wrapper.isRunning())

        wrapper.stop()
        block(100)
        assertFalse(actual = animatablePainter.isRunning())
        assertFalse(actual = wrapper.isRunning())
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = IconAnimatablePainter(
            icon = TestAnimatablePainter(
                SizeColorPainter(Color.Red, Size(100f, 100f))
            ).asEquitableWithThis(),
        )
        val element11 = IconAnimatablePainter(
            icon = TestAnimatablePainter(
                SizeColorPainter(Color.Red, Size(100f, 100f))
            ).asEquitableWithThis(),
        )
        val element2 = IconAnimatablePainter(
            icon = TestAnimatablePainter(
                SizeColorPainter(Color.Green, Size(100f, 100f))
            ).asEquitableWithThis(),
        )
        val element3 = IconAnimatablePainter(
            icon = TestAnimatablePainter(
                SizeColorPainter(Color.Red, Size(100f, 100f))
            ).asEquitableWithThis(),
            background = ColorPainter(Color.Gray).asEquitable(),
        )
        val element4 = IconAnimatablePainter(
            icon = TestAnimatablePainter(
                SizeColorPainter(Color.Red, Size(100f, 100f))
            ).asEquitableWithThis(),
            iconSize = Size(69f, 44f),
        )
        val element5 = IconAnimatablePainter(
            icon = TestAnimatablePainter(
                SizeColorPainter(Color.Red, Size(100f, 100f))
            ).asEquitableWithThis(),
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
            expected = "IconAnimatablePainter(TestAnimatablePainter(painter=SizeColorPainter(color=-65536, size=100.0x100.0)):TestAnimatablePainter(painter=SizeColorPainter(color=-65536, size=100.0x100.0)),ColorPainter(-7829368),69.0x44.0,-16776961)",
            actual = IconAnimatablePainter(
                icon = TestAnimatablePainter(
                    SizeColorPainter(Color.Red, Size(100f, 100f))
                ).asEquitableWithThis(),
                background = ColorPainter(Color.Gray).asEquitable(),
                iconSize = Size(69f, 44f),
                iconTint = Color.Blue
            ).key
        )
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "IconAnimatablePainter(icon=EquitableAnimatablePainter(painter=TestAnimatablePainter(painter=SizeColorPainter(color=-65536, size=100.0x100.0)), equalityKey=TestAnimatablePainter(painter=SizeColorPainter(color=-65536, size=100.0x100.0))), background=EquitablePainter(painter=ColorPainter(color=-7829368), equalityKey=18413117194201202688), iconSize=69.0x44.0, iconTint=-16776961)",
            actual = IconAnimatablePainter(
                icon = TestAnimatablePainter(
                    SizeColorPainter(Color.Red, Size(100f, 100f))
                ).asEquitableWithThis(),
                background = ColorPainter(Color.Gray).asEquitable(),
                iconSize = Size(69f, 44f),
                iconTint = Color.Blue
            ).toString()
        )
    }
}