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
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.util.Size
import kotlin.math.max
import kotlin.math.roundToInt

fun Drawable.tryToResizeDrawable(
    request: ImageRequest,
    resizeSize: Size?,
): Drawable {
    return if (request.resizeApplyToDrawable && resizeSize != null) {
        val scale = request.resizeScaleDecider.get(
            imageWidth = intrinsicWidth,
            imageHeight = intrinsicHeight,
            resizeWidth = resizeSize.width,
            resizeHeight = resizeSize.height
        )
        if (this is Animatable) {
            ResizeAnimatableDrawable(this, resizeSize, scale)
        } else {
            ResizeDrawable(this, resizeSize, scale)
        }
    } else {
        this
    }
}

/**
 * Using [resizeSize] as the intrinsic size of [drawable], [drawable] will be scaled according to the scale of [resizeSize].
 * ResizeDrawable is suitable for changing the start and end pictures to the same size when using CrossfadeDrawable to display pictures in transition, so as to avoid the start or end pictures being scaled when the transition animation starts
 */
open class ResizeDrawable constructor(
    drawable: Drawable,
    val resizeSize: Size,
    val resizeScale: Scale
) : DrawableWrapperCompat(drawable) {

    override fun getIntrinsicWidth(): Int {
        return resizeSize.width
    }

    override fun getIntrinsicHeight(): Int {
        return resizeSize.height
    }

    override fun mutate(): ResizeDrawable {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            ResizeDrawable(mutateDrawable, resizeSize, resizeScale)
        } else {
            this
        }
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        applyResize(drawable, resizeSize, resizeScale)
    }

    override fun toString(): String {
        return "ResizeDrawable(wrapped=$drawable, resizeSize=$resizeSize, resizeScale=$resizeScale)"
    }
}

/**
 * Using [resizeSize] as the intrinsic size of [drawable], [drawable] will be scaled according to the scale of [resizeSize].
 * ResizeDrawable is suitable for changing the start and end pictures to the same size when using CrossfadeDrawable to display pictures in transition, so as to avoid the start or end pictures being scaled when the transition animation starts
 */
open class ResizeAnimatableDrawable(
    drawable: Drawable,
    val resizeSize: Size,
    val resizeScale: Scale
) : AnimatableDrawableWrapper(drawable) {

    override fun getIntrinsicWidth(): Int {
        return resizeSize.width
    }

    override fun getIntrinsicHeight(): Int {
        return resizeSize.height
    }

    override fun mutate(): ResizeAnimatableDrawable {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            ResizeAnimatableDrawable(mutateDrawable, resizeSize, resizeScale)
        } else {
            this
        }
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        applyResize(drawable, resizeSize, resizeScale)
    }

    override fun toString(): String {
        return "ResizeAnimatableDrawable(wrapped=$drawable, resizeSize=$resizeSize, resizeScale=$resizeScale)"
    }
}

private fun applyResize(wrappedDrawable: Drawable?, resizeSize: Size, resizeScale: Scale) {
    val resizeWidth = resizeSize.width
    val resizeHeight = resizeSize.height
    val wrappedWidth = wrappedDrawable?.intrinsicWidth ?: 0
    val wrappedHeight = wrappedDrawable?.intrinsicHeight ?: 0
    val wrappedLeft: Int
    val wrappedTop: Int
    val wrappedRight: Int
    val wrappedBottom: Int
    if (wrappedWidth <= 0 || wrappedHeight <= 0) {
        wrappedLeft = 0
        wrappedTop = 0
        wrappedRight = wrappedWidth.takeIf { it > 0 } ?: resizeWidth
        wrappedBottom = wrappedHeight.takeIf { it > 0 } ?: resizeHeight
    } else {
        val widthRatio = resizeWidth.toFloat() / wrappedWidth
        val heightRatio = resizeHeight.toFloat() / wrappedHeight
        val drawableScale = max(widthRatio, heightRatio)
        val newWrappedWidth = (wrappedWidth * drawableScale).roundToInt()
        val newWrappedHeight = (wrappedHeight * drawableScale).roundToInt()
        when (resizeScale) {
            Scale.START_CROP -> {
                wrappedLeft = 0
                wrappedTop = 0
                @Suppress("KotlinConstantConditions")
                wrappedRight = newWrappedWidth + wrappedLeft
                @Suppress("KotlinConstantConditions")
                wrappedBottom = newWrappedHeight + wrappedTop
            }

            Scale.CENTER_CROP -> {
                wrappedLeft = -(newWrappedWidth - resizeWidth) / 2
                wrappedTop = -(newWrappedHeight - resizeHeight) / 2
                wrappedRight = newWrappedWidth + wrappedLeft
                wrappedBottom = newWrappedHeight + wrappedTop
            }

            Scale.END_CROP -> {
                wrappedLeft = -(newWrappedWidth - resizeWidth)
                wrappedTop = -(newWrappedHeight - resizeHeight)
                wrappedRight = newWrappedWidth + wrappedLeft
                wrappedBottom = newWrappedHeight + wrappedTop
            }

            Scale.FILL -> {
                wrappedLeft = 0
                wrappedTop = 0
                wrappedRight = resizeWidth
                wrappedBottom = resizeHeight
            }
        }
    }
    wrappedDrawable?.setBounds(wrappedLeft, wrappedTop, wrappedRight, wrappedBottom)
}