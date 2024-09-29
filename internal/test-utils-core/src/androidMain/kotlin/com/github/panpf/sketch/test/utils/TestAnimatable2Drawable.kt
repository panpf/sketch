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

import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.util.toLogString

@RequiresApi(23)
class TestAnimatable2Drawable(
    drawable: Drawable? = null
) : DrawableWrapperCompat(drawable), Animatable2, SketchDrawable {

    private var running = false
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    var callbacks: MutableList<Animatable2.AnimationCallback>? = null

    override fun start() {
        running = true
        handler.post {
            val callbacks = callbacks
            if (callbacks != null) {
                for (callback in callbacks) {
                    callback.onAnimationStart(this)
                }
            }
        }
    }

    override fun stop() {
        running = false
        handler.post {
            val callbacks = callbacks
            if (callbacks != null) {
                for (callback in callbacks) {
                    callback.onAnimationEnd(this)
                }
            }
        }
    }

    override fun isRunning(): Boolean {
        return running
    }

    override fun registerAnimationCallback(callback: Animatable2.AnimationCallback) {
        val callbacks = callbacks ?: mutableListOf<Animatable2.AnimationCallback>().apply {
            this@TestAnimatable2Drawable.callbacks = this
        }
        callbacks.add(callback)
    }

    override fun unregisterAnimationCallback(callback: Animatable2.AnimationCallback): Boolean {
        return callbacks?.remove(callback) == true
    }

    override fun clearAnimationCallbacks() {
        callbacks?.clear()
    }

    override fun mutate(): TestAnimatable2Drawable {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            TestAnimatable2Drawable(drawable = mutateDrawable)
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestAnimatable2Drawable
        if (drawable != other.drawable) return false
        return true
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {
        return "TestAnimatable2Drawable(drawable=${drawable?.toLogString()})"
    }
}