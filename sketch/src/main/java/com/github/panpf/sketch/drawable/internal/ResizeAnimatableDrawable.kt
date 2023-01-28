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

import android.graphics.drawable.Drawable
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.requiredMainThread

/**
 * Using [resizeSize] as the intrinsic size of [drawable], [drawable] will be scaled according to the scale of [resizeSize].
 * ResizeDrawable is suitable for changing the start and end pictures to the same size when using CrossfadeDrawable to display pictures in transition, so as to avoid the start or end pictures being scaled when the transition animation starts
 */
open class ResizeAnimatableDrawable(
    drawable: SketchAnimatableDrawable,
    resizeSize: Size,
    resizeScale: Scale
) : ResizeDrawable(drawable, resizeSize, resizeScale), Animatable2Compat {

    override fun start() {
        drawable?.start()
    }

    override fun stop() {
        drawable?.stop()
    }

    override fun isRunning(): Boolean {
        return drawable?.isRunning ?: false
    }

    override fun registerAnimationCallback(callback: AnimationCallback) {
        requiredMainThread()    // Consistent with AnimatedImageDrawable
        drawable?.registerAnimationCallback(callback)
    }

    override fun unregisterAnimationCallback(callback: AnimationCallback): Boolean {
        return drawable?.unregisterAnimationCallback(callback) ?: false
    }

    override fun clearAnimationCallbacks() {
        drawable?.clearAnimationCallbacks()
    }

    override fun getDrawable(): SketchAnimatableDrawable? {
        return super.getDrawable() as SketchAnimatableDrawable?
    }

    override fun setDrawable(drawable: Drawable?) {
        super.setDrawable(drawable as SketchAnimatableDrawable?)
    }

    override fun mutate(): ResizeAnimatableDrawable {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            ResizeAnimatableDrawable(mutateDrawable, resizeSize, resizeScale)
        } else {
            this
        }
    }

    override fun toString(): String {
        return "ResizeAnimatableDrawable(wrapped=$drawable, resizeSize=$resizeSize, resizeScale=$resizeScale)"
    }
}