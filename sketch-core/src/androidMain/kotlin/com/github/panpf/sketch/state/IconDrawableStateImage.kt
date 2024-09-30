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

package com.github.panpf.sketch.state

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.ImageInvalidException
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


/* ********************************************* drawable icon ********************************************* */

/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { RealEquitableDrawable(it) },
    iconSize = iconSize,
    iconTint = ResColor(iconTint),
)

/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { ResDrawable(it) },
    iconSize = iconSize,
    iconTint = ResColor(iconTint),
)

/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = background?.let { ColorFetcherDrawable(it) },
    iconSize = iconSize,
    iconTint = ResColor(iconTint),
)


/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
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
    iconTint = iconTint,
)

/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
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
    iconTint = iconTint,
)

/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
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
    iconTint = iconTint,
)


/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    @ColorRes iconTint: Int,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = null,
    iconSize = iconSize,
    iconTint = ResColor(iconTint),
)

/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
 */
fun IconDrawableStateImage(
    icon: EquitableDrawable,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEquitableDrawable(icon),
    background = null,
    iconSize = iconSize,
    iconTint = iconTint,
)


/* ********************************************* res icon ********************************************* */

/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    background: EquitableDrawable? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { RealEquitableDrawable(it) },
    iconSize = iconSize,
    iconTint = ResColor(iconTint),
)

/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { ResDrawable(it) },
    iconSize = iconSize,
    iconTint = ResColor(iconTint),
)

/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { ColorFetcherDrawable(it) },
    iconSize = iconSize,
    iconTint = ResColor(iconTint),
)


/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
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
    iconTint = iconTint,
)

/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
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
    iconTint = iconTint,
)

/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
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
    iconTint = iconTint,
)


/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    @ColorRes iconTint: Int,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = null,
    iconSize = iconSize,
    iconTint = ResColor(iconTint),
)

/**
 * Creates an [IconDrawableStateImage] that combines the given icon and background into a drawable of no fixed size that can be used as a state diagram.
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest.createIconDrawableStateImage
 */
fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = null,
    iconSize = iconSize,
    iconTint = iconTint,
)


/**
 * Combines the given icon and background into a drawable with no fixed size to use as a state drawable.
 *
 * Icons are centered and always the same size
 *
 * @see com.github.panpf.sketch.core.android.test.state.IconDrawableStateImageTest
 */
data class IconDrawableStateImage(
    val icon: DrawableFetcher,
    val background: DrawableFetcher?,
    val iconSize: Size?,
    val iconTint: ColorFetcher?
) : StateImage {

    override val key: String =
        "IconDrawableStateImage(icon=${icon.key},background=${background?.key},iconSize=$iconSize)"

    override fun getImage(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Image? {
        return try {
            val icon = icon.getDrawable(request.context).apply {
                if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
                    if (icon is ResDrawable) {
                        val resources = icon.resources ?: sketch.context.resources
                        val resId = icon.resId
                        val resourceName = resources.getResourceName(resId)
                        throw ImageInvalidException(
                            "Invalid drawable resource, intrinsicWidth or intrinsicHeight is less than or equal to 0. resId=$resId, resName=$resourceName"
                        )
                    } else {
                        throw ImageInvalidException(
                            "Invalid drawable resource, intrinsicWidth or intrinsicHeight is less than or equal to 0."
                        )
                    }
                }
            }
            val background = background?.getDrawable(request.context)
            val iconTintColor = iconTint?.getColor(request.context)
            IconDrawable(
                icon = icon,
                background = background,
                iconSize = iconSize,
                iconTint = iconTintColor
            )
        } catch (e: Throwable) {
            sketch.logger.w("IconStateDrawableImage. getDrawable error. ${e.message}")
            e.printStackTrace()
            null
        }?.asImage()
    }

    override fun toString(): String {
        return "IconDrawableStateImage(icon=$icon, background=$background, iconSize=$iconSize)"
    }
}