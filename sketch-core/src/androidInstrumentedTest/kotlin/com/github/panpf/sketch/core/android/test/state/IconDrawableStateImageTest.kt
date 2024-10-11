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
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import androidx.core.content.res.ResourcesCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.DrawableImage
import com.github.panpf.sketch.drawable.ColorFetcherDrawableFetcher
import com.github.panpf.sketch.drawable.IconDrawable
import com.github.panpf.sketch.drawable.RealColorDrawableFetcher
import com.github.panpf.sketch.drawable.RealDrawableFetcher
import com.github.panpf.sketch.drawable.ResDrawableFetcher
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.IconDrawableStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.SizeColorDrawable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asEquitable
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher
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
class IconDrawableStateImageTest {

    @Test
    fun testIconDrawableStateImageWithDrawableIcon() {
        // One parameters
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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

    @Test
    fun testIconDrawableStateImageWithResIcon() {
        // One parameters
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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
        IconDrawableStateImage(
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

    @Test
    fun testKey() {
        val iconDrawable = ResDrawableFetcher(android.R.drawable.ic_delete)
        IconDrawableStateImage(
            icon = iconDrawable,
            background = RealColorDrawableFetcher(Color.GRAY),
            iconSize = Size(44, 67),
            iconTint = IntColorFetcher(Color.BLUE)
        ).apply {
            assertEquals(
                expected = "IconDrawableStateImage(icon=${iconDrawable.key},background=RealColorDrawableFetcher(color=-7829368),iconSize=44x67,iconTint=IntColorFetcher(color=-16776961))",
                actual = key
            )
        }
    }

    @Test
    fun testGetImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val iconDrawable =
            ColorDrawable(Color.YELLOW).size(Size(100, 100)).asEquitable(Color.YELLOW)
        val backgroundDrawable = ColorDrawable(Color.GREEN).asEquitable()

        IconDrawableStateImage(
            icon = iconDrawable,
        ).getImage(sketch, request, null).asOrThrow<DrawableImage>()
            .drawable.asOrThrow<IconDrawable>().apply {
                assertEquals(iconDrawable, icon)
                assertNull(background)
                assertNull(iconSize)
                assertNull(iconTint)
            }

        IconDrawableStateImage(
            icon = iconDrawable,
            background = backgroundDrawable,
            iconSize = Size(40, 40),
            iconTint = IntColorFetcher(Color.YELLOW)
        ).getImage(sketch, request, null).asOrThrow<DrawableImage>()
            .drawable.asOrThrow<IconDrawable>().apply {
                assertEquals(iconDrawable, icon)
                assertEquals(backgroundDrawable, background)
                assertEquals(Size(40, 40), iconSize)
                assertEquals(Color.YELLOW, iconTint)
            }

        IconDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            background = android.R.drawable.ic_lock_lock,
            iconSize = Size(50, 50),
            iconTint = android.R.color.holo_purple
        ).getImage(sketch, request, null).asOrThrow<DrawableImage>()
            .drawable.asOrThrow<IconDrawable>().apply {
                assertTrue(icon is BitmapDrawable)
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
        val element1 = IconDrawableStateImage(
            icon = ResDrawableFetcher(android.R.drawable.ic_delete),
        )
        val element11 = element1.copy()
        val element2 = element1.copy(icon = ResDrawableFetcher(android.R.drawable.btn_star))
        val element3 =
            element1.copy(background = ResDrawableFetcher(android.R.drawable.ic_lock_lock))
        val element4 = element1.copy(iconSize = Size(100, 100))
        val element5 = element1.copy(iconTint = IntColorFetcher(Color.GREEN))

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
        val iconDrawable = ResDrawableFetcher(android.R.drawable.ic_delete)
        IconDrawableStateImage(
            icon = iconDrawable,
            background = RealColorDrawableFetcher(Color.GRAY),
            iconSize = Size(44, 67),
            iconTint = IntColorFetcher(Color.BLUE)
        ).apply {
            assertEquals(
                expected = "IconDrawableStateImage(icon=$iconDrawable, background=RealColorDrawableFetcher(color=-7829368), iconSize=44x67, iconTint=IntColorFetcher(color=-16776961))",
                actual = toString()
            )
        }
    }
}