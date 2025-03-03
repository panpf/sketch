@file:OptIn(ExperimentalTestApi::class)

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
import com.github.panpf.sketch.painter.IconAnimatablePainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.rememberIconAnimatablePainter
import com.github.panpf.sketch.state.asEquitablePainter
import com.github.panpf.sketch.test.utils.SizeColorDrawable
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asAnimatableDrawable
import com.github.panpf.sketch.test.utils.asAnimatablePainter
import com.github.panpf.sketch.test.utils.asEquitableWithThis
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher
import com.github.panpf.sketch.util.SketchSize
import kotlin.test.Test
import kotlin.test.assertEquals

class IconAnimatablePainterAndroidTest {

    @Test
    fun testRememberIconAnimatablePainterWithPainterIcon() {
        // One parameters
        runComposeUiTest {
            setContent {
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = null,
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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

                // background: IntColorFetcher, iconTine
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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

                // background: IntColorFetcher
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorPainter(
                                Color.Gray,
                                Size(100f, 100f)
                            ).asAnimatablePainter().asEquitableWithThis(),
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
                rememberIconAnimatablePainter(
                    icon = SizeColorPainter(Color.Gray, Size(100f, 100f)).asAnimatablePainter()
                        .asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
    fun testRememberIconAnimatablePainterWithDrawableIcon() {
        // One parameters
        runComposeUiTest {
            setContent {
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = null,
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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

                // background: IntColorFetcher, iconTine
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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

                // background: IntColorFetcher
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                                .getColor(LocalContext.current)
                                .let { Color(it) }
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitableWithThis().asEquitablePainter(),
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
                rememberIconAnimatablePainter(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitableWithThis(),
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
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
    fun testRememberIconAnimatablePainterWithResIcon() {
        // One parameters
        runComposeUiTest {
            setContent {
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = Color.Green,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconTint
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color, iconTine
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = Color.Green,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = Color.Green,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = Color.Green,
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res, iconTine
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = null,
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable, iconTine
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = null,
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = null,
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorPainter(Color.Green).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Color
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = Color.Green,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color.Green).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = LocalContext.current.getEquitableDrawableCompat(android.R.color.darker_gray)
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = IntColorFetcher(android.graphics.Color.YELLOW),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorPainter(Color(android.graphics.Color.YELLOW)).asEquitable(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color(android.graphics.Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = Color.Blue
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
                                .asEquitablePainter(),
                            background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable()
                                .asEquitablePainter(),
                            iconSize = Size(101f, 202f),
                            iconTint = Color.Blue
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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
                rememberIconAnimatablePainter(
                    icon = com.github.panpf.sketch.test.R.drawable.ic_animated,
                    background = ColorDrawable(android.graphics.Color.YELLOW).asEquitable(),
                    iconSize = Size(101f, 202f),
                    iconTint = IntColorFetcher(android.graphics.Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatablePainter(
                            icon = LocalContext.current
                                .getEquitableDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_animated)
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