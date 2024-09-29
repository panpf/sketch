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
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
import com.github.panpf.sketch.drawable.internal.AnimatableCallbackHelper
import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.key
import com.github.panpf.sketch.util.toLogString

/**
 * Animatable version of EquitableDrawable
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.EquitableAnimatableDrawableTest
 */
open class EquitableAnimatableDrawable internal constructor(
    drawable: Drawable,
    equalityKey: Any,
) : EquitableDrawable(drawable, equalityKey), Animatable2Compat, Key {

    override val key: String = "EquitableAnimatableDrawable('${key(equalityKey)}')"

    internal var callbackHelper: AnimatableCallbackHelper? = null

    init {
        callbackHelper = AnimatableCallbackHelper(drawable)
    }

    override fun setDrawable(drawable: Drawable?) {
        callbackHelper?.setDrawable(drawable)
        super.setDrawable(drawable)
    }

    override fun registerAnimationCallback(callback: AnimationCallback) {
        callbackHelper?.registerAnimationCallback(callback)
    }

    override fun unregisterAnimationCallback(callback: AnimationCallback): Boolean {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EquitableAnimatableDrawable
        if (equalityKey != other.equalityKey) return false
        return true
    }

    override fun hashCode(): Int {
        return equalityKey.hashCode()
    }

    override fun toString(): String {
        return "EquitableAnimatableDrawable(drawable=${drawable.toLogString()}, equalityKey=$equalityKey)"
    }
}