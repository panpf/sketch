@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.android.test.state

import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.drawable.getEquitableDrawableCompat
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.state.IconAnimatablePainterStateImage
import com.github.panpf.sketch.state.asEquitablePainter
import com.github.panpf.sketch.state.rememberIconAnimatablePainterStateImage
import com.github.panpf.sketch.test.utils.SizeColorDrawable
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asAnimatableDrawable
import com.github.panpf.sketch.test.utils.asAnimatablePainter
import com.github.panpf.sketch.test.utils.asEquitableWithThis
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import com.github.panpf.sketch.util.SketchSize
import kotlin.test.Test
import kotlin.test.assertEquals

class IconAnimatablePainterStateImageAndroidTest {

    @Test
    fun testRememberIconAnimatablePainterStateImageWithPainterIcon() {
        // One parameters
        runComposeUiTest {
            setContent {
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconSize
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconTint
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = null,
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = null,
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // background: Painter, iconTine
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color, iconTine
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res, iconTine
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColor, iconTine
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable, iconTine
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // iconSize, iconTint
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColor
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
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
    fun testRememberIconAnimatablePainterStateImageWithDrawableIcon() {
        // One parameters
        runComposeUiTest {
            setContent {
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconSize
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconTint
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = null,
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = null,
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // background: Painter, iconTine
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color, iconTine
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res, iconTine
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColor, iconTine
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable, iconTine
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // iconSize, iconTint
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColor
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColor(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
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
                rememberIconAnimatablePainterStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
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
    fun testRememberIconAnimatablePainterStateImageWithResIcon() {
        // One parameters
        runComposeUiTest {
            setContent {
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = Color.Green,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColor(android.graphics.Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconTint
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color, iconTine
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = Color.Green,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = Color.Green,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = Color.Green,
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res, iconTine
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable, iconTine
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColor(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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
                rememberIconAnimatablePainterStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColor(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
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