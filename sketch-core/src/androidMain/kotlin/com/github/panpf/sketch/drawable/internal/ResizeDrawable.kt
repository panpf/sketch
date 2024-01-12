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
package com.github.panpf.sketch.drawable.internal

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.asDrawable
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.calculateBounds

fun Image.resizeApplyToDrawable(
    request: ImageRequest,
    resizeSize: Size?,
): Image {
    return if (request.resizeApplyToDrawable && resizeSize != null) {
        val scale = request.resizeScaleDecider.get(
            imageWidth = width,
            imageHeight = height,
            resizeWidth = resizeSize.width,
            resizeHeight = resizeSize.height
        )
        val drawable = this.asDrawable()
        if (drawable is Animatable) {
            ResizeAnimatableDrawable(drawable, resizeSize, scale)
        } else {
            ResizeDrawable(drawable, resizeSize, scale)
        }.asSketchImage()
    } else {
        this
    }
}

/**
 * Using [size] as the intrinsic size of [drawable], [drawable] will be scaled according to the scale of [size].
 * ResizeDrawable is suitable for changing the start and end pictures to the same size when using CrossfadeDrawable to display pictures in transition, so as to avoid the start or end pictures being scaled when the transition animation starts
 */
open class ResizeDrawable constructor(
    drawable: Drawable,
    val size: Size,
    val scale: Scale
) : DrawableWrapperCompat(drawable) {

    override fun getIntrinsicWidth(): Int {
        return size.width
    }

    override fun getIntrinsicHeight(): Int {
        return size.height
    }

    override fun mutate(): ResizeDrawable {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            ResizeDrawable(mutateDrawable, size, scale)
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
                    width = this@ResizeDrawable.bounds.width(),
                    height = this@ResizeDrawable.bounds.height()
                ),
                scale = scale
            )
        }
    }

    override fun toString(): String {
        return "ResizeDrawable(wrapped=$drawable, size=$size, scale=$scale)"
    }
}

/**
 * Using [size] as the intrinsic size of [drawable], [drawable] will be scaled according to the scale of [size].
 * ResizeDrawable is suitable for changing the start and end pictures to the same size when using CrossfadeDrawable to display pictures in transition, so as to avoid the start or end pictures being scaled when the transition animation starts
 */
open class ResizeAnimatableDrawable(
    drawable: Drawable,
    val size: Size,
    val scale: Scale
) : AnimatableDrawableWrapper(drawable) {

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

    override fun toString(): String {
        return "ResizeAnimatableDrawable(wrapped=$drawable, size=$size, scale=$scale)"
    }
}