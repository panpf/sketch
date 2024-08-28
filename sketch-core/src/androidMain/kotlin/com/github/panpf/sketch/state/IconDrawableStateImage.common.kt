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
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.drawable.ColorFetcherDrawable
import com.github.panpf.sketch.drawable.DrawableEqualizer
import com.github.panpf.sketch.drawable.DrawableFetcher
import com.github.panpf.sketch.drawable.IconDrawable
import com.github.panpf.sketch.drawable.RealEqualityDrawable
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.ColorFetcher
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import com.github.panpf.sketch.util.Size


/* ********************************************* drawable icon ********************************************* */

fun IconDrawableStateImage(
    icon: DrawableEqualizer,
    background: DrawableEqualizer? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEqualityDrawable(icon),
    background = background?.let { RealEqualityDrawable(it) },
    iconSize = iconSize,
    iconTint = ResColor(iconTint),
)

fun IconDrawableStateImage(
    icon: DrawableEqualizer,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEqualityDrawable(icon),
    background = background?.let { ResDrawable(it) },
    iconSize = iconSize,
    iconTint = ResColor(iconTint),
)

fun IconDrawableStateImage(
    icon: DrawableEqualizer,
    background: IntColor? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEqualityDrawable(icon),
    background = background?.let { ColorFetcherDrawable(it) },
    iconSize = iconSize,
    iconTint = ResColor(iconTint),
)


fun IconDrawableStateImage(
    icon: DrawableEqualizer,
    background: DrawableEqualizer? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEqualityDrawable(icon),
    background = background?.let { RealEqualityDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint,
)

fun IconDrawableStateImage(
    icon: DrawableEqualizer,
    @DrawableRes background: Int? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEqualityDrawable(icon),
    background = background?.let { ResDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint,
)

fun IconDrawableStateImage(
    icon: DrawableEqualizer,
    background: IntColor? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEqualityDrawable(icon),
    background = background?.let { ColorFetcherDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint,
)


fun IconDrawableStateImage(
    icon: DrawableEqualizer,
    iconSize: Size? = null,
    @ColorRes iconTint: Int,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEqualityDrawable(icon),
    background = null,
    iconSize = iconSize,
    iconTint = ResColor(iconTint),
)

fun IconDrawableStateImage(
    icon: DrawableEqualizer,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = RealEqualityDrawable(icon),
    background = null,
    iconSize = iconSize,
    iconTint = iconTint,
)


/* ********************************************* res icon ********************************************* */

fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    background: DrawableEqualizer? = null,
    iconSize: Size? = null,
    @ColorRes iconTint: Int,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { RealEqualityDrawable(it) },
    iconSize = iconSize,
    iconTint = ResColor(iconTint),
)

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


fun IconDrawableStateImage(
    @DrawableRes icon: Int,
    background: DrawableEqualizer? = null,
    iconSize: Size? = null,
    iconTint: IntColor? = null,
): IconDrawableStateImage = IconDrawableStateImage(
    icon = ResDrawable(icon),
    background = background?.let { RealEqualityDrawable(it) },
    iconSize = iconSize,
    iconTint = iconTint,
)

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
 */
class IconDrawableStateImage(
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
        }?.asSketchImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IconDrawableStateImage) return false
        if (icon != other.icon) return false
        if (iconSize != other.iconSize) return false
        if (background != other.background) return false
        return true
    }

    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + (iconSize?.hashCode() ?: 0)
        result = 31 * result + (background?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "IconDrawableStateImage(icon=$icon, background=$background, iconSize=$iconSize)"
    }
}