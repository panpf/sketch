/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("ComplexRedundantLet")

package com.github.panpf.sketch.core.android.test.state

import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import androidx.core.content.res.ResourcesCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.DrawableImage
import com.github.panpf.sketch.drawable.ColorFetcherDrawable
import com.github.panpf.sketch.drawable.IconAnimatableDrawable
import com.github.panpf.sketch.drawable.RealColorDrawable
import com.github.panpf.sketch.drawable.RealEquitableDrawable
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.IconAnimatableDrawableStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.SizeColorDrawable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asAnimatableDrawable
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchSize
import com.github.panpf.sketch.util.asOrThrow
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class IconAnimatableDrawableStateImageTest {

    @Test
    fun testIconAnimatableDrawableStateImageWithDrawableIcon() {
        // One parameters
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
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
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = ColorDrawable(Color.GREEN).asEquitable(),
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
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
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = android.R.color.darker_gray,
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = ResDrawable(android.R.color.darker_gray),
                    iconSize = null,
                    iconTint = null
                ),
                actual = this
            )
        }
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = IntColor(Color.YELLOW),
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                    iconSize = null,
                    iconTint = null
                ),
                actual = this
            )
        }
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = ColorDrawable(Color.YELLOW).asEquitable(),
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
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
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            iconSize = Size(101, 202),
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = null,
                    iconSize = Size(101, 202),
                    iconTint = null
                ),
                actual = this
            )
        }

        // iconTint
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            iconTint = android.R.color.holo_purple
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = null,
                    iconSize = null,
                    iconTint = ResColor(android.R.color.holo_purple)
                ),
                actual = this
            )
        }
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            iconTint = IntColor(Color.CYAN)
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
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
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = ColorDrawable(Color.GREEN).asEquitable(),
            iconSize = Size(101, 202),
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
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
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = android.R.color.darker_gray,
            iconSize = Size(101, 202),
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = ResDrawable(android.R.color.darker_gray),
                    iconSize = Size(101, 202),
                    iconTint = null
                ),
                actual = this
            )
        }
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = IntColor(Color.YELLOW),
            iconSize = Size(101, 202),
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                    iconSize = Size(101, 202),
                    iconTint = null
                ),
                actual = this
            )
        }
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = ColorDrawable(Color.YELLOW).asEquitable(),
            iconSize = Size(101, 202),
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
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
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = ColorDrawable(Color.GREEN).asEquitable(),
            iconTint = android.R.color.holo_purple
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
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
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = ColorDrawable(Color.GREEN).asEquitable(),
            iconTint = IntColor(Color.CYAN)
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
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
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = android.R.color.darker_gray,
            iconTint = android.R.color.holo_purple
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = ResDrawable(android.R.color.darker_gray),
                    iconSize = null,
                    iconTint = ResColor(android.R.color.holo_purple)
                ),
                actual = this
            )
        }
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = android.R.color.darker_gray,
            iconTint = IntColor(Color.CYAN)
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = ResDrawable(android.R.color.darker_gray),
                    iconSize = null,
                    iconTint = IntColor(Color.CYAN)
                ),
                actual = this
            )
        }

        // background: IntColor, iconTine
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = IntColor(Color.YELLOW),
            iconTint = android.R.color.holo_purple
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                    iconSize = null,
                    iconTint = ResColor(android.R.color.holo_purple)
                ),
                actual = this
            )
        }
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = IntColor(Color.YELLOW),
            iconTint = IntColor(Color.CYAN)
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                    iconSize = null,
                    iconTint = IntColor(Color.CYAN)
                ),
                actual = this
            )
        }

        // background: Drawable, iconTine
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = ColorDrawable(Color.YELLOW).asEquitable(),
            iconTint = android.R.color.holo_purple
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = ColorDrawable(Color.YELLOW).asEquitable()
                        .let { RealEquitableDrawable(it) },
                    iconSize = null,
                    iconTint = ResColor(android.R.color.holo_purple)
                ),
                actual = this
            )
        }
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = ColorDrawable(Color.YELLOW).asEquitable(),
            iconTint = IntColor(Color.CYAN)
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
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
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            iconSize = Size(101, 202),
            iconTint = android.R.color.holo_purple
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = null,
                    iconSize = Size(101, 202),
                    iconTint = ResColor(android.R.color.holo_purple)
                ),
                actual = this
            )
        }
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            iconSize = Size(101, 202),
            iconTint = IntColor(Color.CYAN)
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
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
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = ColorDrawable(Color.GREEN).asEquitable(),
            iconSize = Size(101, 202),
            iconTint = android.R.color.holo_purple
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
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
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = ColorDrawable(Color.GREEN).asEquitable(),
            iconSize = Size(101, 202),
            iconTint = IntColor(Color.CYAN)
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
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
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = android.R.color.darker_gray,
            iconSize = Size(101, 202),
            iconTint = android.R.color.holo_purple
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = ResDrawable(android.R.color.darker_gray),
                    iconSize = Size(101, 202),
                    iconTint = ResColor(android.R.color.holo_purple)
                ),
                actual = this
            )
        }
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = android.R.color.darker_gray,
            iconSize = Size(101, 202),
            iconTint = IntColor(Color.CYAN)
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = ResDrawable(android.R.color.darker_gray),
                    iconSize = Size(101, 202),
                    iconTint = IntColor(Color.CYAN)
                ),
                actual = this
            )
        }

        // background: IntColor
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = IntColor(Color.YELLOW),
            iconSize = Size(101, 202),
            iconTint = android.R.color.holo_purple
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                    iconSize = Size(101, 202),
                    iconTint = ResColor(android.R.color.holo_purple)
                ),
                actual = this
            )
        }
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = IntColor(Color.YELLOW),
            iconSize = Size(101, 202),
            iconTint = IntColor(Color.CYAN)
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = ColorFetcherDrawable(IntColor(Color.YELLOW)),
                    iconSize = Size(101, 202),
                    iconTint = IntColor(Color.CYAN)
                ),
                actual = this
            )
        }

        // background: Drawable
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = ColorDrawable(Color.YELLOW).asEquitable(),
            iconSize = Size(101, 202),
            iconTint = android.R.color.holo_purple
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
                        .let { RealEquitableDrawable(it) },
                    background = ColorDrawable(Color.YELLOW).asEquitable()
                        .let { RealEquitableDrawable(it) },
                    iconSize = Size(101, 202),
                    iconTint = ResColor(android.R.color.holo_purple)
                ),
                actual = this
            )
        }
        IconAnimatableDrawableStateImage(
            icon = SizeColorDrawable(TestColor.GRAY, SketchSize(100, 100)).asAnimatableDrawable()
                .asEquitable(TestColor.GRAY),
            background = ColorDrawable(Color.YELLOW).asEquitable(),
            iconSize = Size(101, 202),
            iconTint = IntColor(Color.CYAN)
        ).apply {
            assertEquals(
                expected = IconAnimatableDrawableStateImage(
                    icon = SizeColorDrawable(
                        TestColor.GRAY,
                        SketchSize(100, 100)
                    ).asAnimatableDrawable().asEquitable(TestColor.GRAY)
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

    @Test
    fun testIconAnimatableDrawableStateImageWithResIcon() {
        // One parameters
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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
        IconAnimatableDrawableStateImage(
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

    @Test
    fun testKey() {
        val iconDrawable =
            ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
        IconAnimatableDrawableStateImage(
            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
            background = RealColorDrawable(Color.GRAY),
            iconSize = Size(44, 67),
            iconTint = IntColor(Color.BLUE)
        ).apply {
            assertEquals(
                expected = "IconAnimatableDrawableStateImage(icon=$iconDrawable,background=RealColorDrawable(-7829368),iconSize=44x67,iconTint=IntColor(-16776961))",
                actual = key
            )
        }
    }

    @Test
    fun testGetImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val iconDrawable =
            ColorDrawable(Color.YELLOW).size(Size(100, 100)).asAnimatableDrawable()
                .asEquitable(Color.YELLOW)
        val backgroundDrawable = ColorDrawable(Color.GREEN).asEquitable()

        IconAnimatableDrawableStateImage(
            icon = iconDrawable,
        ).getImage(sketch, request, null).asOrThrow<DrawableImage>()
            .drawable.asOrThrow<IconAnimatableDrawable>().apply {
                assertEquals(iconDrawable, icon)
                assertNull(background)
                assertNull(iconSize)
                assertNull(iconTint)
            }

        IconAnimatableDrawableStateImage(
            icon = iconDrawable,
            background = backgroundDrawable,
            iconSize = Size(40, 40),
            iconTint = IntColor(Color.YELLOW)
        ).getImage(sketch, request, null).asOrThrow<DrawableImage>()
            .drawable.asOrThrow<IconAnimatableDrawable>().apply {
                assertEquals(iconDrawable, icon)
                assertEquals(backgroundDrawable, background)
                assertEquals(Size(40, 40), iconSize)
                assertEquals(Color.YELLOW, iconTint)
            }

        IconAnimatableDrawableStateImage(
            icon = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated,
            background = android.R.drawable.ic_lock_lock,
            iconSize = Size(50, 50),
            iconTint = android.R.color.holo_purple
        ).getImage(sketch, request, null).asOrThrow<DrawableImage>()
            .drawable.asOrThrow<IconAnimatableDrawable>().apply {
                assertTrue(icon is Animatable)
                assertTrue(background is BitmapDrawable)
                assertEquals(Size(50, 50), iconSize)
                assertEquals(
                    ResourcesCompat.getColor(context.resources, android.R.color.holo_purple, null),
                    iconTint
                )
            }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = IconAnimatableDrawableStateImage(
            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
        )
        val element11 = element1.copy()
        val element2 = element1.copy(icon = ResDrawable(android.R.drawable.btn_star))
        val element3 = element1.copy(background = ResDrawable(android.R.drawable.ic_lock_lock))
        val element4 = element1.copy(iconSize = Size(100, 100))
        val element5 = element1.copy(iconTint = IntColor(Color.GREEN))

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
        val iconDrawable =
            ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
        IconAnimatableDrawableStateImage(
            icon = ResDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated),
            background = RealColorDrawable(Color.GRAY),
            iconSize = Size(44, 67),
            iconTint = IntColor(Color.BLUE)
        ).apply {
            assertEquals(
                expected = "IconAnimatableDrawableStateImage(icon=$iconDrawable, background=RealColorDrawable(-7829368), iconSize=44x67, iconTint=IntColor(-16776961))",
                actual = toString()
            )
        }
    }
}