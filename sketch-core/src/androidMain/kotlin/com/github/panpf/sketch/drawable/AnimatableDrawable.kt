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
package com.github.panpf.sketch.drawable

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.drawable.internal.AnimatableDrawableWrapper
import com.github.panpf.sketch.drawable.internal.SketchDrawable
import com.github.panpf.sketch.drawable.internal.toLogString

/**
 * Provide unified Callback support for Animatable2, Animatable2Compat, Animatable
 */
class AnimatableDrawable constructor(
    drawable: Drawable,
) : AnimatableDrawableWrapper(drawable), SketchDrawable {

    override fun mutate(): AnimatableDrawable {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            AnimatableDrawable(mutateDrawable)
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AnimatableDrawable
        if (drawable != other.drawable) return false
        return true
    }

    override fun hashCode(): Int = drawable.hashCode()

    override fun toString(): String {
        return "AnimatableDrawable(drawable=${drawable?.toLogString()})"
    }
}