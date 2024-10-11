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
import com.github.panpf.sketch.state.IconPainterStateImage
import com.github.panpf.sketch.state.asEquitablePainter
import com.github.panpf.sketch.state.rememberIconPainterStateImage
import com.github.panpf.sketch.test.utils.SizeColorDrawable
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asEquitable
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher
import com.github.panpf.sketch.util.SketchSize
import kotlin.test.Test
import kotlin.test.assertEquals

class IconPainterStateImageAndroidTest {

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
                // background
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
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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

                // iconTint
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
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = null,
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color, iconTine
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
                    background = Color.Green,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Color.Green,
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res, iconTine
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColorFetcher, iconTine
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable, iconTine
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color
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
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColorFetcher
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
    fun testRememberIconPainterStateImageWithDrawableIcon() {
        // One parameters
        runComposeUiTest {
            setContent {
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = null,
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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

                // background: IntColorFetcher, iconTine
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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

                // background: IntColorFetcher
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asEquitable().asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
    fun testRememberIconPainterStateImageWithResIcon() {
        // One parameters
        runComposeUiTest {
            setContent {
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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

                // background: IntColorFetcher, iconTine
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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

                // background: IntColorFetcher
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(android.R.drawable.ic_delete)
                                .asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconPainterStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconPainterStateImage(
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