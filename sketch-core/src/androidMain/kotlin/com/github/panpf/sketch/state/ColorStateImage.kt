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
package com.github.panpf.sketch.state

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.asSketchImage

/**
 * Use color as the state [Drawable]
 */
class ColorStateImage constructor(private val color: ColorFetcher) : StateImage {

    constructor(@ColorInt color: Int) : this(IntColor(color))

    override fun getImage(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Image {
        return ColorDrawable(color.getColor(request.context)).asSketchImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ColorStateImage) return false
        if (color != other.color) return false
        return true
    }

    override fun hashCode(): Int {
        return color.hashCode()
    }

    override fun toString(): String {
        return "ColorStateImage($color)"
    }
}