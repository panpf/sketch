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
import com.github.panpf.sketch.state.IconDrawableStateImage
import com.github.panpf.sketch.state.rememberIconDrawableStateImage
import com.github.panpf.sketch.test.utils.SizeColorDrawable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asEquitable
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchSize
import kotlin.test.Test
import kotlin.test.assertEquals

class IconDrawableStateImageComposeAndroidTest {

    @Test
    fun testRememberIconDrawableStateImageWithDrawableIcon() {
        runComposeUiTest {
            setContent {
                // One parameters
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
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
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
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
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColorFetcher(Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
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
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconTint
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = null,
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
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
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
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
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColorFetcher(Color.YELLOW),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
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
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
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
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
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
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColorFetcher, iconTine
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColorFetcher(Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColorFetcher(Color.YELLOW),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable, iconTine
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
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
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
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
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
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
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
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
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = android.R.color.darker_gray,
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColorFetcher
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColorFetcher(Color.YELLOW),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = IntColorFetcher(Color.YELLOW),
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asEquitable(),
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100))
                                .asEquitable()
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
    fun testRememberIconDrawableStateImageWithResIcon() {
        runComposeUiTest {
            setContent {
                // One parameters
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = null,
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // Two parameters
                // background
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = IntColorFetcher(Color.YELLOW),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = null,
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconSize
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // iconTint
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = null,
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = null,
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // Three parameters
                // background, iconSize
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = IntColorFetcher(Color.YELLOW),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconSize = Size(101, 202),
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = Size(101, 202),
                            iconTint = null
                        ),
                        actual = this
                    )
                }

                // background: Painter, iconTine
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
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
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColorFetcher, iconTine
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = IntColorFetcher(Color.YELLOW),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = IntColorFetcher(Color.YELLOW),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable, iconTine
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = null,
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = null,
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // iconSize, iconTint
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = null,
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // Four parameters
                // background: Painter
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorDrawable(Color.GREEN)
                                .asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(Color.GREEN).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
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
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = android.R.color.darker_gray,
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ResDrawableFetcher(android.R.color.darker_gray),
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: IntColorFetcher
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = IntColorFetcher(Color.YELLOW),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = IntColorFetcher(Color.YELLOW),
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorFetcherDrawableFetcher(IntColorFetcher(Color.YELLOW)),
                            iconSize = Size(101, 202),
                            iconTint = IntColorFetcher(Color.CYAN)
                        ),
                        actual = this
                    )
                }

                // background: Drawable
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = android.R.color.holo_purple
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
                            background = ColorDrawable(Color.YELLOW).asEquitable()
                                .let { RealDrawableFetcher(it) },
                            iconSize = Size(101, 202),
                            iconTint = ResColorFetcher(android.R.color.holo_purple)
                        ),
                        actual = this
                    )
                }
                rememberIconDrawableStateImage(
                    icon = android.R.drawable.ic_delete,
                    background = ColorDrawable(Color.YELLOW).asEquitable(),
                    iconSize = Size(101, 202),
                    iconTint = IntColorFetcher(Color.CYAN)
                ).apply {
                    assertEquals(
                        expected = IconDrawableStateImage(
                            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
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