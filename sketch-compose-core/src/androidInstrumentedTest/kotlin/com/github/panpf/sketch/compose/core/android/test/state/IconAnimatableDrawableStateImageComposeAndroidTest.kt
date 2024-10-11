@file:OptIn(ExperimentalTestApi::class)
@file:Suppress("ComplexRedundantLet")

package com.github.panpf.sketch.compose.core.android.test.state

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.drawable.ColorFetcherDrawableFetcher
import com.github.panpf.sketch.drawable.RealDrawableFetcher
import com.github.panpf.sketch.drawable.ResDrawableFetcher
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.state.IconAnimatableDrawableStateImage
import com.github.panpf.sketch.state.rememberIconAnimatableDrawableStateImage
import com.github.panpf.sketch.test.utils.SizeColorDrawable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asAnimatableDrawable
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher
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
                                .let { RealDrawableFetcher(it) },
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
                                .let { RealDrawableFetcher(it) },
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
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
                                .let { RealDrawableFetcher(it) },
                            background = ResDrawableFetcher(android.R.color.darker_gray),
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
                    background = IntColorFetcher(Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealDrawableFetcher(it) },
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
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
                                .let { RealDrawableFetcher(it) },
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
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
                                .let { RealDrawableFetcher(it) },
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
                                .let { RealDrawableFetcher(it) },
                            background = null,
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
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
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealDrawableFetcher(it) },
                            background = null,
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
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
                                .let { RealDrawableFetcher(it) },
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
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
                                .let { RealDrawableFetcher(it) },
                            background = ResDrawableFetcher(android.R.color.darker_gray),
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
                    background = IntColorFetcher(Color.YELLOW),
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
                                .let { RealDrawableFetcher(it) },
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
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
                                .let { RealDrawableFetcher(it) },
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
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
                                .let { RealDrawableFetcher(it) },
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
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
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealDrawableFetcher(it) },
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
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
                                .let { RealDrawableFetcher(it) },
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
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
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealDrawableFetcher(it) },
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColorFetcher, iconTine
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = IntColorFetcher(Color.YELLOW),
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
                                .let { RealDrawableFetcher(it) },
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
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
                    background = IntColorFetcher(Color.YELLOW),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealDrawableFetcher(it) },
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
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
                                .let { RealDrawableFetcher(it) },
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
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
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealDrawableFetcher(it) },
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
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
                                .let { RealDrawableFetcher(it) },
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
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
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealDrawableFetcher(it) },
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
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
                                .let { RealDrawableFetcher(it) },
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
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
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealDrawableFetcher(it) },
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
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
                                .let { RealDrawableFetcher(it) },
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
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
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealDrawableFetcher(it) },
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColorFetcher
                rememberIconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(
                        TestColor.GRAY
                    ),
                    background = IntColorFetcher(Color.YELLOW),
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
                                .let { RealDrawableFetcher(it) },
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
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
                    background = IntColorFetcher(Color.YELLOW),
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealDrawableFetcher(it) },
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
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
                                .let { RealDrawableFetcher(it) },
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
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
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = SizeColorDrawable(
                                TestColor.GRAY,
                                SketchSize(100, 100)
                            ).asAnimatableDrawable().asEquitable(
                                TestColor.GRAY
                            )
                                .let { RealDrawableFetcher(it) },
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColorFetcher(Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = null,
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = null,
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColorFetcher(Color.YELLOW),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColorFetcher, iconTine
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColorFetcher(Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColorFetcher(Color.YELLOW),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColorFetcher
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColorFetcher(Color.YELLOW),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = IntColorFetcher(Color.YELLOW),
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
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
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconAnimatableDrawableStateImage(
                    icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconAnimatableDrawableStateImage(
                            icon = ResDrawableFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }
            }
        }
    }
}