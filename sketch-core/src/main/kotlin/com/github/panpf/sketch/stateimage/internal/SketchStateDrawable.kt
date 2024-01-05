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
package com.github.panpf.sketch.stateimage.internal

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import com.github.panpf.sketch.drawable.internal.AnimatableDrawableWrapper

/**
 * Identify the Sketch state Drawable
 */
interface SketchStateDrawable

// TODO interface StateImage

internal fun Drawable.toSketchStateDrawable(): Drawable {
    return if (this is Animatable) {
        SketchStateAnimatableDrawable(this)
    } else {
        SketchStateNormalDrawable(this)
    }
}

open class SketchStateNormalDrawable constructor(drawable: Drawable) :
    DrawableWrapperCompat(drawable), SketchStateDrawable {

    override fun mutate(): SketchStateNormalDrawable {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            SketchStateNormalDrawable(mutateDrawable)
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SketchStateNormalDrawable
        if (drawable != other.drawable) return false
        return true
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {
        return "SketchStateNormalDrawable($drawable)"
    }
}

open class SketchStateAnimatableDrawable constructor(animatableDrawable: Drawable) :
    AnimatableDrawableWrapper(animatableDrawable), SketchStateDrawable {

    override fun mutate(): SketchStateAnimatableDrawable {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            SketchStateAnimatableDrawable(mutateDrawable)
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SketchStateAnimatableDrawable
        if (drawable != other.drawable) return false
        return true
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {
        return "SketchStateAnimatableDrawable($drawable)"
    }
}