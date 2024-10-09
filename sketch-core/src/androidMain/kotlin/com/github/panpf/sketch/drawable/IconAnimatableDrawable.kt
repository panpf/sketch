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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.drawable

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.drawable.internal.AnimatableCallbackHelper
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toLogString

/**
 * It consists of two parts: icon and bg. bg is scaled to fill bounds, the icon size is unchanged always centered.
 * It is suitable for use as a placeholder image for waterfall flow.
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.IconAnimatableDrawableTest
 */
class IconAnimatableDrawable constructor(
    icon: Drawable,
    background: Drawable? = null,
    iconSize: Size? = null,
    @ColorInt iconTint: Int? = null
) : IconDrawable(icon, background, iconSize, iconTint),
    Drawable.Callback,
    Animatable2Compat,
    SketchDrawable {

    internal var callbackHelper: AnimatableCallbackHelper? = null

    init {
        require(icon is Animatable) {
            "icon must be Animatable"
        }
        require(background == null || background !is Animatable) {
            "background can't be Animatable"
        }
        callbackHelper = AnimatableCallbackHelper(icon)
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

    override fun mutate(): IconAnimatableDrawable {
        val mutateIcon = icon.mutate()
        val mutateBackground = background?.mutate()
        return if (mutateIcon !== icon || mutateBackground !== background) {
            IconAnimatableDrawable(
                icon = mutateIcon,
                background = mutateBackground,
                iconSize = iconSize,
                iconTint = iconTint
            )
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as IconAnimatableDrawable
        if (icon != other.icon) return false
        if (background != other.background) return false
        if (iconSize != other.iconSize) return false
        return iconTint == other.iconTint
    }

    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + (background?.hashCode() ?: 0)
        result = 31 * result + (iconSize?.hashCode() ?: 0)
        result = 31 * result + (iconTint?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "IconAnimatableDrawable(" +
            "icon=${icon.toLogString()}, " +
            "background=${background?.toLogString()}, " +
            "iconSize=$iconSize, " +
            "iconTint=$iconTint" +
            ")"
}