@file:OptIn(ExperimentalTestApi::class)
@file:Suppress("ComplexRedundantLet")

package com.github.panpf.sketch.compose.core.android.test.state

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.drawable.ColorFetcherDrawable
import com.github.panpf.sketch.drawable.RealEquitableDrawable
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.state.IconAnimatableDrawableStateImage
import com.github.panpf.sketch.state.rememberIconAnimatableDrawableStateImage
import com.github.panpf.sketch.test.utils.SizeColorDrawable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asAnimatableDrawable
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchSize
import kotlin.test.Test
import kotlin.test.assertEquals

class IconAnimatableDrawableStateImageComposeAndroidTest {

    @Test
    fun testRememberIconAnimatableDrawableStateImageWithDrawableIcon() {
        runComposeUiTest {
            setContent {
                // One parameters
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = null,
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // Two parameters
                // background
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ResDrawable(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = IntColor(Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconSize
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconTint
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = null,
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = null,
                            iconSize = null,
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // Three parameters
                // background, iconSize
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ResDrawable(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = IntColor(Color.YELLOW),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // background: Painter, iconTine
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = null,
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res, iconTine
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ResDrawable(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = android.R.color.darker_gray,
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ResDrawable(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColor, iconTine
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = IntColor(Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = IntColor(Color.YELLOW),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                            iconSize = null,
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable, iconTine
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = null,
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // iconSize, iconTint
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    iconSize = Size(101, 202),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // Four parameters
                // background: Painter
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = Size(101, 202),
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = Size(101, 202),
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ResDrawable(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101, 202),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ResDrawable(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColor
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = IntColor(Color.YELLOW),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = IntColor(Color.YELLOW),
                    iconSize = Size(101, 202),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = Size(101, 202),
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealEquitableDrawable(it) },
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = Size(101, 202),
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testRememberIconAnimatableDrawableStateImageWithResIcon() {
        runComposeUiTest {
            setContent {
                // One parameters
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = null,
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // Two parameters
                // background
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ResDrawable(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColor(Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconSize
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconTint
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = null,
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = null,
                            iconSize = null,
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // Three parameters
                // background, iconSize
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ResDrawable(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColor(Color.YELLOW),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // background: Painter, iconTine
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = null,
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res, iconTine
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ResDrawable(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ResDrawable(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColor, iconTine
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColor(Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColor(Color.YELLOW),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                            iconSize = null,
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable, iconTine
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = null,
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = null,
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // iconSize, iconTint
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    iconSize = Size(101, 202),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // Four parameters
                // background: Painter
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = Size(101, 202),
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = Size(101, 202),
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: android res
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ResDrawable(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101, 202),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ResDrawable(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColor
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColor(Color.YELLOW),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColor(Color.YELLOW),
                    iconSize = Size(101, 202),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = Size(101, 202),
                            iconTint = ResColor(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = IntColor(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealEquitableDrawable(it) },
                            iconSize = Size(101, 202),
                            iconTint = IntColor(Color.CYAN)
                        ),
                        actual = this
                    )
                }
            }
        }
    }
}