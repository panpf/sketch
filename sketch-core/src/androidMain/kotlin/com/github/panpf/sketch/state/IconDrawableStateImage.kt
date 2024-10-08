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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.state

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.drawable.ColorFetcherDrawable
import com.github.panpf.sketch.drawable.DrawableFetcher
import com.github.panpf.sketch.drawable.EquitableDrawable
import com.github.panpf.sketch.drawable.IconDrawable
import com.github.panpf.sketch.drawable.RealEquitableDrawable
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.ColorFetcher
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import com.github.panpf.sketch.util.Size

/* ********************************************* Drawable icon ********************************************* */

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { RealEquitableDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColor(it) }
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { RealEquitableDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { ResDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColor(it) }
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { ResDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { ColorFetcherDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColor(it) }
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { ColorFetcherDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { ResDrawable(it) },
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { ColorFetcherDrawable(it) },
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { RealEquitableDrawable(it) },
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = null,
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColor(it) }
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = null,
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { ResDrawable(it) },
    iconSize = null,
    iconTint = null
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { ColorFetcherDrawable(it) },
    iconSize = null,
    iconTint = null
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { RealEquitableDrawable(it) },
    iconSize = null,
    iconTint = null
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = null,
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithDrawableIcon
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = null,
    iconSize = null,
    iconTint = null
)


/* ********************************************* res icon ********************************************* */

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { RealEquitableDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColor(it) }
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { RealEquitableDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { ResDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColor(it) }
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { ResDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { ColorFetcherDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColor(it) }
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { ColorFetcherDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { ResDrawable(it) },
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { ColorFetcherDrawable(it) },
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { RealEquitableDrawable(it) },
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = null,
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColor(it) }
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = null,
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { ResDrawable(it) },
    iconSize = null,
    iconTint = null
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { ColorFetcherDrawable(it) },
    iconSize = null,
    iconTint = null
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { RealEquitableDrawable(it) },
    iconSize = null,
    iconTint = null
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = null,
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.testIconDrawableStateImageWithResIcon
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = null,
    iconSize = null,
    iconTint = null
)

/**
 * Combines the given icon and background into a drawable with no fixed size to use as a state drawable.
 *
 * Icons are centered and always the same size
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest
 */
data class IconDrawableStateImage constructor(
    val icon: DrawableFetcher,
    val background: DrawableFetcher? = null,
    val iconSize: Size? = null,
    val iconTint: ColorFetcher? = null,
) : StateImage {

    override val key: String =
        "IconDrawableStateImage(icon=${icon.key},background=${background?.key},iconSize=$iconSize,iconTint=${iconTint?.key})"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        return IconDrawable(
            icon = this.icon.getDrawable(request.context),
            background = this.background?.getDrawable(request.context),
            iconSize = iconSize,
            iconTint = iconTint?.getColor(request.context)
        ).asImage()
    }

    override fun toString(): String {
        return "IconDrawableStateImage(icon=$icon, background=$background, iconSize=$iconSize, iconTint=$iconTint)"
    }
}