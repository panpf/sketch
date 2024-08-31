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

package com.github.panpf.sketch.drawable

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.drawable.internal.AnimatableDrawableWrapper
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.calculateBounds
import com.github.panpf.sketch.util.toLogString

/**
 * Using [size] as the intrinsic size of [drawable], [drawable] will be scaled according to the scale of [size].
 * ResizeDrawable is suitable for changing the start and end pictures to the same size when using CrossfadeDrawable to display pictures in transition, so as to avoid the start or end pictures being scaled when the transition animation starts
 */
open class ResizeAnimatableDrawable(
    drawable: Drawable,
    val size: Size,
    val scale: Scale
) : AnimatableDrawableWrapper(drawable), SketchDrawable {

    override fun getIntrinsicWidth(): Int {
        return size.width
    }

    override fun getIntrinsicHeight(): Int {
        return size.height
    }

    override fun mutate(): ResizeAnimatableDrawable {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            ResizeAnimatableDrawable(mutateDrawable, size, scale)
        } else {
            this
        }
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        drawable?.apply {
            this@apply.bounds = calculateBounds(
                srcSize = Size(
                    width = this@apply.intrinsicWidth,
                    height = this@apply.intrinsicHeight
                ),
                dstSize = Size(
                    width = this@ResizeAnimatableDrawable.bounds.width(),
                    height = this@ResizeAnimatableDrawable.bounds.height()
                ),
                scale = scale
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ResizeAnimatableDrawable
        if (size != other.size) return false
        if (drawable != other.drawable) return false
        return scale == other.scale
    }

    override fun hashCode(): Int {
        var result = size.hashCode()
        result = 31 * result + scale.hashCode()
        result = 31 * result + drawable.hashCode()
        return result
    }

    override fun toString(): String {
        return "ResizeAnimatableDrawable(drawable=${drawable?.toLogString()}, size=$size, scale=$scale)"
    }
}