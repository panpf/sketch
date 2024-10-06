@file:OptIn(ExperimentalTestApi::class, ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.android.test.painter

import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.drawable.getEquitableDrawableCompat
import com.github.panpf.sketch.painter.IconPainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.rememberIconPainter
import com.github.panpf.sketch.state.asEquitablePainter
import com.github.panpf.sketch.test.utils.SizeColorDrawable
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asEquitable
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import com.github.panpf.sketch.util.SketchSize
import kotlin.test.Test
import kotlin.test.assertEquals

class IconPainterAndroidTest {

    @Test
    fun testRememberIconPainterWithPainterIcon() {
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
                // background
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
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconSize
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

                // iconTint
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
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = null,
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = null,
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }
            }
        }

        // Three parameters
        runComposeUiTest {
            setContent {
                // background, iconSize
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
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // background: Painter, iconTine
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
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color, iconTine
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
                    background = Color.Green,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Color.Green,
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res, iconTine
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColor, iconTine
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable, iconTine
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // iconSize, iconTint
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
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }
            }
        }

        // Four parameters
        runComposeUiTest {
            setContent {
                // background: Painter
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
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color
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
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColor
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testRememberIconPainterWithDrawableIcon() {
        // One parameters
        runComposeUiTest {
            setContent {
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
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
                // background
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconSize
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconTint
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = null,
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = null,
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = null,
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }
            }
        }

        // Three parameters
        runComposeUiTest {
            setContent {
                // background, iconSize
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // background: Painter, iconTine
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color, iconTine
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res, iconTine
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColor, iconTine
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable, iconTine
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // iconSize, iconTint
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }
            }
        }

        // Four parameters
        runComposeUiTest {
            setContent {
                // background: Painter
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColor
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testRememberIconPainterWithResIcon() {
        // One parameters
        runComposeUiTest {
            setContent {
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
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
                // background
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = IntColor(android.graphics.Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconSize
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconTint
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }
            }
        }

        // Three parameters
        runComposeUiTest {
            setContent {
                // background, iconSize
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // background: Painter, iconTine
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color, iconTine
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res, iconTine
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColor, iconTine
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable, iconTine
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // iconSize, iconTint
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }
            }
        }

        // Four parameters
        runComposeUiTest {
            setContent {
                // background: Painter
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColor
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainter(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }
            }
        }
    }
}