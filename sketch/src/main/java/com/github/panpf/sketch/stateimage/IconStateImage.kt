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

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.drawable.internal.IconDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.SketchException

class IconStateImage private constructor(
    private val icon: DrawableFetcher,
    private val bg: Any?,
) : StateImage {

    constructor(icon: Drawable, bg: Drawable)
            : this(RealDrawable(icon), RealDrawable(bg))

    constructor(icon: Drawable, @DrawableRes bg: Int)
            : this(RealDrawable(icon), ResDrawable(bg))

    constructor(icon: Drawable, bg: ColorFetcher)
            : this(RealDrawable(icon), bg)

    constructor(icon: Drawable)
            : this(RealDrawable(icon), null)

    constructor(@DrawableRes icon: Int, bg: Drawable)
            : this(ResDrawable(icon), RealDrawable(bg))

    constructor(@DrawableRes icon: Int, @DrawableRes bg: Int)
            : this(ResDrawable(icon), ResDrawable(bg))

    constructor(@DrawableRes icon: Int, bg: ColorFetcher)
            : this(ResDrawable(icon), bg)

    constructor(@DrawableRes icon: Int)
            : this(ResDrawable(icon), null)

    override fun getDrawable(
        sketch: Sketch,
        request: ImageRequest,
        exception: SketchException?
    ): Drawable {
        val icon = icon.getDrawable(request.context)
        val bgDrawable = when (bg) {
            is DrawableFetcher -> bg.getDrawable(request.context)
            is ColorFetcher -> ColorDrawable(bg.getColor(request.context))
            else -> null
        }
        return IconDrawable(icon, bgDrawable)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IconStateImage) return false
        if (icon != other.icon) return false
        if (bg != other.bg) return false
        return true
    }

    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + (bg?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "IconStateImage(icon=$icon, bg=$bg)"
    }
}