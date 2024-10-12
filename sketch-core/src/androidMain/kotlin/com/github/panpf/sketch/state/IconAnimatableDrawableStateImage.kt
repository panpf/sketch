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
import com.github.panpf.sketch.drawable.ColorFetcherDrawableFetcher
import com.github.panpf.sketch.drawable.DrawableFetcher
import com.github.panpf.sketch.drawable.EquitableDrawable
import com.github.panpf.sketch.drawable.IconAnimatableDrawable
import com.github.panpf.sketch.drawable.RealDrawableFetcher
import com.github.panpf.sketch.drawable.ResDrawableFetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.ColorFetcher
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher
import com.github.panpf.sketch.util.Size

/* ********************************************* Drawable icon ********************************************* */

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = background?.let { RealDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColorFetcher(it) }
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = background?.let { RealDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = background?.let { ResDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColorFetcher(it) }
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = background?.let { ResDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = background?.let { ColorFetcherDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColorFetcher(it) }
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = background?.let { ColorFetcherDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = background?.let { ResDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = background?.let { ColorFetcherDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = background?.let { RealDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = null,
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColorFetcher(it) }
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = null,
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = background?.let { ResDrawableFetcher(it) },
    iconSize = null,
    iconTint = null
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = background?.let { ColorFetcherDrawableFetcher(it) },
    iconSize = null,
    iconTint = null
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = background?.let { RealDrawableFetcher(it) },
    iconSize = null,
    iconTint = null
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = null,
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithDrawableIcon
 */
fun IconAnimatableDrawableStateImage(
    icon: EquitableDrawable,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = RealDrawableFetcher(icon),
    background = null,
    iconSize = null,
    iconTint = null
)


/* ********************************************* res icon ********************************************* */

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = background?.let { RealDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColorFetcher(it) }
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = background?.let { RealDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = background?.let { ResDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColorFetcher(it) }
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = background?.let { ResDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = background?.let { ColorFetcherDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColorFetcher(it) }
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = background?.let { ColorFetcherDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = background?.let { ResDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColorFetcher? = null,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = background?.let { ColorFetcherDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = background?.let { RealDrawableFetcher(it) },
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    @ColorRes iconTint: Int? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = null,
    iconSize = iconSize,
    iconTint = iconTint?.let { ResColorFetcher(it) }
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    iconTint: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = null,
    iconSize = iconSize,
    iconTint = iconTint
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = background?.let { ResDrawableFetcher(it) },
    iconSize = null,
    iconTint = null
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColorFetcher? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = background?.let { ColorFetcherDrawableFetcher(it) },
    iconSize = null,
    iconTint = null
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = background?.let { RealDrawableFetcher(it) },
    iconSize = null,
    iconTint = null
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = null,
    iconSize = iconSize,
    iconTint = null
)

/**
 * Create a [IconAnimatableDrawableStateImage].
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest.testIconAnimatableDrawableStateImageWithResIcon
 */
fun IconAnimatableDrawableStateImage(
    @DrawableRes icon: Int,
): IconAnimatableDrawableStateImage = IconAnimatableDrawableStateImage(
    icon = ResDrawableFetcher(icon),
    background = null,
    iconSize = null,
    iconTint = null
)

/**
 * Combines the given icon and background into a drawable with no fixed size to use as a state drawable.
 *
 * Icons are centered and always the same size
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconAnimatableDrawableStateImageTest
 */
data class IconAnimatableDrawableStateImage constructor(
    val icon: DrawableFetcher,
    val background: DrawableFetcher? = null,
    val iconSize: Size? = null,
    val iconTint: ColorFetcher? = null,
) : StateImage {

    override val key: String =
        "IconAnimatableDrawable(${icon.key},${background?.key},$iconSize,${iconTint?.key})"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image {
        return IconAnimatableDrawable(
            icon = this.icon.getDrawable(request.context),
            background = this.background?.getDrawable(request.context),
            iconSize = iconSize,
            iconTint = iconTint?.getColor(request.context)
        ).asImage()
    }

    override fun toString(): String = "IconAnimatableDrawableStateImage(" +
            "icon=$icon, " +
            "background=$background, " +
            "iconSize=$iconSize, " +
            "iconTint=$iconTint" +
            ")"
}