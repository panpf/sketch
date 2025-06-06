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
import android.widget.ImageView.ScaleType
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.drawable.internal.AnimatableCallbackHelper
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toLogString
import com.github.panpf.sketch.util.toScaleType

/**
 * Using [size] as the intrinsic size of [drawable], [drawable] will be scaled according to the scale of [size].
 * ResizeDrawable is suitable for changing the start and end pictures to the same size when using CrossfadeDrawable to display pictures in transition, so as to avoid the start or end pictures being scaled when the transition animation starts
 *
 * @see com.github.panpf.sketch.view.core.test.drawable.ResizeAnimatableDrawableTest
 */
open class ResizeAnimatableDrawable constructor(
    drawable: Drawable,
    size: Size,
    scaleType: ScaleType = ScaleType.CENTER_CROP
) : ResizeDrawable(drawable, size, scaleType), Animatable2Compat, SketchDrawable {

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

    internal var callbackHelper: AnimatableCallbackHelper? = null

    init {
        callbackHelper = AnimatableCallbackHelper(drawable)
    }

    override fun setDrawable(drawable: Drawable?) {
        callbackHelper?.setDrawable(drawable)
        super.setDrawable(drawable)
    }

    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        callbackHelper?.registerAnimationCallback(callback)
    }

    override fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean {
        return callbackHelper?.unregisterAnimationCallback(callback) == true
    }

    override fun clearAnimationCallbacks() {
        callbackHelper?.clearAnimationCallbacks()
    }

    override fun start() {
        callbackHelper?.start()
    }

    override fun stop() {
        callbackHelper?.stop()
    }

    override fun isRunning(): Boolean {
        return callbackHelper?.isRunning == true
    }

    override fun mutate(): ResizeAnimatableDrawable {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            ResizeAnimatableDrawable(mutateDrawable, size, scaleType)
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ResizeAnimatableDrawable
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
        return "ResizeAnimatableDrawable(" +
                "drawable=${drawable?.toLogString()}, " +
                "size=$size, " +
                "scaleType=$scaleType" +
                ")"
    }
}