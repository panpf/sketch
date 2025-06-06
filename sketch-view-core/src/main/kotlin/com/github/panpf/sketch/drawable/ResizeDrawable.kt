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

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.calculateBoundsWithScaleType
import com.github.panpf.sketch.util.toLogString
import com.github.panpf.sketch.util.toScale
import com.github.panpf.sketch.util.toScaleType

/**
 * Using [size] as the intrinsic size of [drawable], [drawable] will be scaled according to the scale of [size].
 *
 * @see com.github.panpf.sketch.view.core.test.drawable.ResizeDrawableTest.testResize
 */
fun Drawable.resize(size: Size, scaleType: ScaleType = ScaleType.CENTER_CROP): ResizeDrawable {
    return if (this is Animatable) {
        ResizeAnimatableDrawable(this, size, scaleType)
    } else {
        ResizeDrawable(this, size, scaleType)
    }
}

/**
 * Using [size] as the intrinsic size of [drawable], [drawable] will be scaled according to the scale of [size].
 *
 * @see com.github.panpf.sketch.view.core.test.drawable.ResizeDrawableTest.testResize
 */
@Deprecated("Use resize(size, scaleType) instead")
fun Drawable.resize(size: Size, scale: Scale): ResizeDrawable {
    return if (this is Animatable) {
        ResizeAnimatableDrawable(this, size, scale)
    } else {
        ResizeDrawable(this, size, scale)
    }
}

/**
 * Using [size] as the intrinsic size of [drawable], [drawable] will be scaled according to the scale of [size].
 * ResizeDrawable is suitable for changing the start and end pictures to the same size when using CrossfadeDrawable to display pictures in transition, so as to avoid the start or end pictures being scaled when the transition animation starts
 *
 * @see com.github.panpf.sketch.view.core.test.drawable.ResizeDrawableTest
 */
open class ResizeDrawable constructor(
    drawable: Drawable,
    val size: Size,
    val scaleType: ScaleType = ScaleType.CENTER_CROP
) : DrawableWrapperCompat(drawable), SketchDrawable {

    @Deprecated("Use scaleType instead.")
    val scale: Scale = scaleType.toScale()

    @Deprecated(message = "Use ResizeDrawable(painter, size, scaleType) instead")
    constructor(
        drawable: Drawable,
        size: Size,
        scale: Scale
    ) : this(
        drawable = drawable,
        size = size,
        scaleType = scale.toScaleType()
    )

    override fun getIntrinsicWidth(): Int {
        return size.width
    }

    override fun getIntrinsicHeight(): Int {
        return size.height
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        val drawable = drawable ?: return
        val srcSize = Size(
            width = drawable.intrinsicWidth,
            height = drawable.intrinsicHeight
        )
        if (srcSize.isEmpty) {
            drawable.setBounds(left, top, right, bottom)
            return
        }

        val drawableBounds = calculateBoundsWithScaleType(
            srcSize = srcSize,
            dstSize = Size(width = right - left, height = bottom - top),
            scaleType = scaleType
        )
        drawable.setBounds(
            /* left = */ drawableBounds.left,
            /* top = */ drawableBounds.top,
            /* right = */ drawableBounds.right,
            /* bottom = */ drawableBounds.bottom
        )
    }

    override fun mutate(): ResizeDrawable {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            ResizeDrawable(mutateDrawable, size, scaleType)
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ResizeDrawable
        if (size != other.size) return false
        if (drawable != other.drawable) return false
        return scaleType == other.scaleType
    }

    override fun hashCode(): Int {
        var result = size.hashCode()
        result = 31 * result + scaleType.hashCode()
        result = 31 * result + drawable.hashCode()
        return result
    }

    override fun toString(): String {
        return "ResizeDrawable(drawable=${drawable?.toLogString()}, size=$size, scaleType=$scaleType)"
    }
}