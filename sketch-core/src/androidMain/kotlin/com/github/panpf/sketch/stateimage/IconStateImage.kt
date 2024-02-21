/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.stateimage

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.drawable.IconDrawable
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.util.DrawableFetcher
import com.github.panpf.sketch.util.RealColorDrawable
import com.github.panpf.sketch.util.RealDrawable
import com.github.panpf.sketch.util.ResColorDrawable
import com.github.panpf.sketch.util.ResDrawable
import com.github.panpf.sketch.util.Size

/**
 * Create an IconStateImage. Set the size and background of the icon through trailing functions.
 */
fun IconStateImage(
    icon: DrawableFetcher,
    block: (IconStateImageBuilderScope.() -> Unit)? = null
): IconStateImage {
    var iconSize: Size? = null
    var background: DrawableFetcher? = null
    if (block != null) {
        val scope = IconStateImageBuilderScope().apply(block)
        iconSize = scope.iconSize
        background = scope.background
    }
    return IconStateImage(icon, iconSize, background)
}

/**
 * Create an IconStateImage. Set the size and background of the icon through trailing functions.
 */
fun IconStateImage(
    icon: Drawable,
    block: (IconStateImageBuilderScope.() -> Unit)? = null
): IconStateImage = IconStateImage(RealDrawable(icon), block)


/**
 * Create an IconStateImage. Set the size and background of the icon through trailing functions.
 */
fun IconStateImage(
    @DrawableRes icon: Int,
    block: (IconStateImageBuilderScope.() -> Unit)? = null
): IconStateImage = IconStateImage(ResDrawable(icon), block)

/**
 * Combines the given icon and background into a drawable with no fixed size to use as a state drawable.
 *
 * Icons are centered and always the same size
 */
class IconStateImage internal constructor(
    private val icon: DrawableFetcher,
    private val iconSize: Size?,
    private val background: DrawableFetcher?,
) : StateImage {

    @Deprecated("Please use IconStateImage to create the function")
    constructor(icon: Drawable, bg: Drawable)
            : this(RealDrawable(icon), null, RealDrawable(bg))

    @Deprecated("Please use IconStateImage to create the function")
    constructor(icon: Drawable, @DrawableRes bg: Int)
            : this(RealDrawable(icon), null, ResDrawable(bg))

    @Deprecated("Please use IconStateImage to create the function")
    constructor(icon: Drawable, bg: ColorFetcher)
            : this(RealDrawable(icon), null, bg.toDrawableFetcher())

    @Deprecated("Please use IconStateImage to create the function")
    constructor(@DrawableRes icon: Int, bg: Drawable)
            : this(ResDrawable(icon), null, RealDrawable(bg))

    @Deprecated("Please use IconStateImage to create the function")
    constructor(@DrawableRes icon: Int, @DrawableRes bg: Int)
            : this(ResDrawable(icon), null, ResDrawable(bg))

    @Deprecated("Please use IconStateImage to create the function")
    constructor(@DrawableRes icon: Int, bg: ColorFetcher)
            : this(ResDrawable(icon), null, bg.toDrawableFetcher())

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
            IconDrawable(icon, background, iconSize)
        } catch (e: Throwable) {
            sketch.logger.w("IconStateImage", "getDrawable error. ${e.message}")
            e.printStackTrace()
            null
        }?.asSketchImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IconStateImage) return false
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
        return "IconStateImage(icon=$icon, background=$background, iconSize=$iconSize)"
    }
}

class IconStateImageBuilderScope {

    internal var iconSize: Size? = null
    internal var background: DrawableFetcher? = null

    fun iconSize(size: Size?) {
        this.iconSize = size
    }

    fun iconSize(width: Int, height: Int) {
        this.iconSize = Size(width = width, height = height)
    }

    fun iconSize(size: Int) {
        this.iconSize = Size(width = size, height = size)
    }

    fun background(drawable: Drawable) {
        this.background = RealDrawable(drawable)
    }

    fun resBackground(@DrawableRes resId: Int) {
        this.background = ResDrawable(resId)
    }

    fun colorBackground(@ColorInt color: Int) {
        this.background = RealColorDrawable(color)
    }

    fun resColorBackground(@ColorRes resId: Int) {
        this.background = ResColorDrawable(resId)
    }
}