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

package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback

class TestAnimatableDrawable3(drawable: Drawable) : DrawableWrapperCompat(drawable),
    Animatable2Compat {
    private var running = false
    private var callbacks: MutableList<AnimationCallback> = mutableListOf()
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    override fun start() {
        running = true
        handler.post {
            for (callback in callbacks) {
                callback.onAnimationStart(this)
            }
        }
    }

    override fun stop() {
        running = false
        handler.post {
            for (callback in callbacks) {
                callback.onAnimationEnd(this)
            }
        }
    }

    override fun isRunning(): Boolean {
        return running
    }

    override fun registerAnimationCallback(callback: AnimationCallback) {
        callbacks.add(callback)
    }

    override fun unregisterAnimationCallback(callback: AnimationCallback): Boolean {
        return callbacks.remove(callback)
    }

    override fun clearAnimationCallbacks() {
        callbacks.clear()
    }

    override fun mutate(): TestAnimatableDrawable3 {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            TestAnimatableDrawable3(drawable = mutateDrawable)
        } else {
            this
        }
    }
}