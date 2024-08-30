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

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.ColorFetcher
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor

fun IntColorDrawableStateImage(@ColorInt color: Int): ColorDrawableStateImage =
    ColorDrawableStateImage(IntColor(color))

fun ResColorDrawableStateImage(@ColorRes resId: Int): ColorDrawableStateImage =
    ColorDrawableStateImage(ResColor(resId))

fun ColorDrawableStateImage(color: IntColor): ColorDrawableStateImage =
    ColorDrawableStateImage(color as ColorFetcher)

fun ColorDrawableStateImage(color: ResColor): ColorDrawableStateImage =
    ColorDrawableStateImage(color as ColorFetcher)

/**
 * Use color as the state [Drawable]
 */
class ColorDrawableStateImage(val color: ColorFetcher) : StateImage {

    override val key: String = "ColorDrawableStateImage(${color.key})"

    override fun getImage(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Image {
        return ColorDrawable(color.getColor(request.context)).asSketchImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ColorDrawableStateImage
        if (color != other.color) return false
        return true
    }

    override fun hashCode(): Int {
        return color.hashCode()
    }

    override fun toString(): String {
        return "ColorDrawableStateImage($color)"
    }
}