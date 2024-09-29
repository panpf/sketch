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

package com.github.panpf.sketch.drawable.internal

import android.graphics.drawable.Animatable
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Handler
import android.os.Looper
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.util.requiredMainThread

/**
 * Provide unified Callback support for Animatable2, Animatable2Compat, Animatable
 *
 * @see com.github.panpf.sketch.core.android.test.drawable.internal.AnimatableCallbackHelperTest
 */
class AnimatableCallbackHelper constructor(drawable: Drawable) {

    internal var callbacks: MutableList<Animatable2Compat.AnimationCallback>? = null
    internal var callbackProxyMap: HashMap<Animatable2Compat.AnimationCallback, Animatable2.AnimationCallback>? =
        null
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    private var drawable: Drawable? = drawable

    val isRunning: Boolean
        get() {
            val drawable = drawable ?: return false
            val animatable = drawable as Animatable
            return animatable.isRunning
        }

    init {
        checkDrawable(drawable)
    }

    fun setDrawable(drawable: Drawable?) {
        checkDrawable(drawable)
        this.drawable = drawable
    }

    private fun checkDrawable(drawable: Drawable?) {
        require(drawable is Animatable) {
            "drawable must implement the Animatable"
        }
    }

    fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        requiredMainThread()    // Consistent with AnimatedImageDrawable
        val drawable = drawable ?: return
        when {
            VERSION.SDK_INT >= VERSION_CODES.M && drawable is Animatable2 -> {
                val callbackProxyMap = callbackProxyMap
                    ?: HashMap<Animatable2Compat.AnimationCallback, Animatable2.AnimationCallback>().apply {
                        this@AnimatableCallbackHelper.callbackProxyMap = this
                    }
                if (callbackProxyMap[callback] == null) {
                    val proxyCallback = object : Animatable2.AnimationCallback() {
                        override fun onAnimationStart(drawable: Drawable?) {
                            callback.onAnimationStart(drawable)
                        }

                        override fun onAnimationEnd(drawable: Drawable?) {
                            callback.onAnimationEnd(drawable)
                        }
                    }
                    callbackProxyMap[callback] = proxyCallback
                    drawable.registerAnimationCallback(proxyCallback)
                }
            }

            drawable is Animatable2Compat -> {
                drawable.registerAnimationCallback(callback)
            }

            else -> {
                val callbacks = callbacks
                    ?: mutableListOf<Animatable2Compat.AnimationCallback>().apply {
                        this@AnimatableCallbackHelper.callbacks = this
                    }
                if (!callbacks.contains(callback)) {
                    callbacks.add(callback)
                }
            }
        }
    }

    fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean {
        val drawable = drawable ?: return false
        return when {
            VERSION.SDK_INT >= VERSION_CODES.M && drawable is Animatable2 -> {
                val proxyCallback = callbackProxyMap?.remove(callback)
                proxyCallback?.let { drawable.unregisterAnimationCallback(it) } == true
            }

            drawable is Animatable2Compat -> {
                drawable.unregisterAnimationCallback(callback)
            }

            else -> {
                callbacks?.remove(callback) == true
            }
        }
    }

    fun clearAnimationCallbacks() {
        val drawable = drawable ?: return
        when {
            VERSION.SDK_INT >= VERSION_CODES.M && drawable is Animatable2 -> {
                callbackProxyMap?.clear()
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

    fun start() {
        val drawable = drawable ?: return
        val animatable = drawable as Animatable
        if (animatable.isRunning) {
            return
        }
        animatable.start()
        val callbacks = callbacks
        if (callbacks != null && !(VERSION.SDK_INT >= VERSION_CODES.M && animatable is Animatable2) && animatable !is Animatable2Compat) {
            handler.post {
                for (callback in callbacks) {
                    callback.onAnimationStart(drawable)
                }
            }
        }
    }

    fun stop() {
        val drawable = drawable ?: return
        val animatable = drawable as Animatable
        if (!animatable.isRunning) {
            return
        }
        animatable.stop()
        val callbacks = callbacks
        if (callbacks != null && !(VERSION.SDK_INT >= VERSION_CODES.M && animatable is Animatable2) && animatable !is Animatable2Compat) {
            handler.post {
                for (callback in callbacks) {
                    callback.onAnimationEnd(drawable)
                }
            }
        }
    }
}