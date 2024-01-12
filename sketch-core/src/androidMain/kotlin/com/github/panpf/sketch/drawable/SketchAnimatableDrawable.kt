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
import com.github.panpf.sketch.util.Size

/**
 * Provide unified Callback support for Animatable2, Animatable2Compat, Animatable
 */
class SketchAnimatableDrawable constructor(
    private val animatableDrawable: Drawable,
) : AnimatableDrawableWrapper(animatableDrawable) {

    override fun mutate(): SketchAnimatableDrawable {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            SketchAnimatableDrawable(mutateDrawable)
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SketchAnimatableDrawable
        if (animatableDrawable != other.animatableDrawable) return false
        return true
    }

    override fun hashCode(): Int = animatableDrawable.hashCode()

    override fun toString(): String {
        return "SketchAnimatableDrawable($animatableDrawable)"
    }
}