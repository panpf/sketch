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

package com.github.panpf.sketch.drawable.internal

import android.graphics.drawable.Animatable
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Handler
import android.os.Looper
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.util.requiredMainThread

/**
 * Provide unified Callback support for Animatable2, Animatable2Compat, Animatable
 */
abstract class AnimatableDrawableWrapper constructor(
    drawable: Drawable,
) : DrawableWrapperCompat(drawable), Animatable2Compat {

    private var callbacks: MutableList<Animatable2Compat.AnimationCallback>? = null
    private var callbackMap: HashMap<Animatable2Compat.AnimationCallback, Animatable2.AnimationCallback>? =
        null
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    init {
        require(drawable is Animatable) {
            "animatableDrawable must implement the Animatable"
        }
    }

    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        requiredMainThread()    // Consistent with AnimatedImageDrawable
        val drawable = drawable
        when {
            VERSION.SDK_INT >= VERSION_CODES.M && drawable is Animatable2 -> {
                val callbackMap = callbackMap
                    ?: HashMap<Animatable2Compat.AnimationCallback, Animatable2.AnimationCallback>().apply {
                        this@AnimatableDrawableWrapper.callbackMap = this
                    }
                if (callbackMap[callback] == null) {
                    val proxyCallback = object : Animatable2.AnimationCallback() {
                        override fun onAnimationStart(drawable: Drawable?) {
                            callback.onAnimationStart(drawable)
                        }

                        override fun onAnimationEnd(drawable: Drawable?) {
                            callback.onAnimationEnd(drawable)
                        }
                    }
                    callbackMap[callback] = proxyCallback
                    drawable.registerAnimationCallback(proxyCallback)
                }
            }

            drawable is Animatable2Compat -> {
                drawable.registerAnimationCallback(callback)
            }

            else -> {
                val callbacks = callbacks
                    ?: mutableListOf<Animatable2Compat.AnimationCallback>().apply {
                        this@AnimatableDrawableWrapper.callbacks = this
                    }
                if (!callbacks.contains(callback)) {
                    callbacks.add(callback)
                }
            }
        }
    }

    override fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean {
        val drawable = drawable
        return when {
            VERSION.SDK_INT >= VERSION_CODES.M && drawable is Animatable2 -> {
                callbackMap?.get(callback)
                    ?.let { drawable.unregisterAnimationCallback(it) } == true
            }

            drawable is Animatable2Compat -> {
                drawable.unregisterAnimationCallback(callback)
            }

            else -> {
                callbacks?.remove(callback) == true
            }
        }
    }

    override fun clearAnimationCallbacks() {
        val drawable = drawable
        when {
            VERSION.SDK_INT >= VERSION_CODES.M && drawable is Animatable2 -> {
                callbackMap?.clear()
                drawable.clearAnimationCallbacks()
            }

            drawable is Animatable2Compat -> {
                drawable.clearAnimationCallbacks()
            }

            else -> {
                callbacks?.clear()
            }
        }
    }

    override fun start() {
        val drawable = drawable as Animatable
        if (drawable.isRunning) {
            return
        }
        drawable.start()
        val callbacks = callbacks
        if (callbacks != null && !(VERSION.SDK_INT >= VERSION_CODES.M && drawable is Animatable2) && drawable !is Animatable2Compat) {
            handler.post {
                for (callback in callbacks) {
                    callback.onAnimationStart(this)
                }
            }
        }
    }

    override fun stop() {
        val drawable = drawable as Animatable
        if (!drawable.isRunning) {
            return
        }
        drawable.stop()
        val callbacks = callbacks
        if (callbacks != null && !(VERSION.SDK_INT >= VERSION_CODES.M && drawable is Animatable2) && drawable !is Animatable2Compat) {
            handler.post {
                for (callback in callbacks) {
                    callback.onAnimationEnd(this)
                }
            }
        }
    }

    override fun isRunning(): Boolean {
        val drawable = drawable
        if (drawable !is Animatable) {
            throw IllegalArgumentException("Drawable must implement the Animatable interface")
        }
        return drawable.isRunning
    }
}