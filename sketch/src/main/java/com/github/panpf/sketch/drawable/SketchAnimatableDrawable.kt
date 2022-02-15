package com.github.panpf.sketch.drawable

/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

import android.annotation.SuppressLint
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.drawable.Animatable
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.appcompat.graphics.drawable.DrawableWrapper
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.util.BitmapInfo
import com.github.panpf.sketch.util.computeByteCount

@SuppressLint("RestrictedApi")
class SketchAnimatableDrawable<T> constructor(
    override val requestKey: String,
    override val requestUri: String,
    override val imageInfo: ImageInfo,
    override val imageExifOrientation: Int,
    override val dataFrom: DataFrom,
    private val animatableDrawable: T,
    private val animatableDrawableName: String,
) : DrawableWrapper(animatableDrawable),
    SketchDrawable, Animatable2Compat where T : Drawable, T : Animatable {

    private var callbacks: MutableList<Animatable2Compat.AnimationCallback>? = null
    private var callbackMap: HashMap<Animatable2Compat.AnimationCallback, Animatable2.AnimationCallback>? =
        null

    override val transformedList: List<Transformed>? get() = null
    override val bitmapInfo: BitmapInfo by lazy {
        BitmapInfo(
            animatableDrawable.intrinsicWidth,
            animatableDrawable.intrinsicHeight,
            computeByteCount(
                animatableDrawable.intrinsicWidth,
                animatableDrawable.intrinsicHeight,
                ARGB_8888
            ),
            ARGB_8888
        )
    }

    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        when {
            VERSION.SDK_INT >= VERSION_CODES.M && animatableDrawable is Animatable2 -> {
                val callbackMap = callbackMap
                    ?: HashMap<Animatable2Compat.AnimationCallback, Animatable2.AnimationCallback>().apply {
                        this@SketchAnimatableDrawable.callbackMap = this
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
                    animatableDrawable.registerAnimationCallback(proxyCallback)
                }
            }
            animatableDrawable is Animatable2Compat -> {
                animatableDrawable.registerAnimationCallback(callback)
            }
            else -> {
                val callbacks = callbacks
                    ?: mutableListOf<Animatable2Compat.AnimationCallback>().apply {
                        this@SketchAnimatableDrawable.callbacks = this
                    }
                if (!callbacks.contains(callback)) {
                    callbacks.add(callback)
                }
            }
        }
    }

    override fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean =
        when {
            VERSION.SDK_INT >= VERSION_CODES.M && animatableDrawable is Animatable2 -> {
                callbackMap?.get(callback)?.let {
                    animatableDrawable.unregisterAnimationCallback(it)
                } == true
            }
            animatableDrawable is Animatable2Compat -> {
                animatableDrawable.unregisterAnimationCallback(callback)
            }
            else -> {
                callbacks?.remove(callback) == true
            }
        }

    override fun clearAnimationCallbacks() {
        when {
            VERSION.SDK_INT >= VERSION_CODES.M && animatableDrawable is Animatable2 -> {
                callbackMap?.clear()
                animatableDrawable.clearAnimationCallbacks()
            }
            animatableDrawable is Animatable2Compat -> {
                animatableDrawable.clearAnimationCallbacks()
            }
            else -> {
                callbacks?.clear()
            }
        }
    }

    override fun start() {
        animatableDrawable.start()
        if (!(VERSION.SDK_INT >= VERSION_CODES.M && animatableDrawable is Animatable2) && animatableDrawable !is Animatable2Compat) {
            val isRunning = isRunning
            if (!isRunning) {
                callbacks?.forEach { it.onAnimationStart(this) }
            }
        }
    }

    override fun stop() {
        animatableDrawable.stop()
        if (!(VERSION.SDK_INT >= VERSION_CODES.M && animatableDrawable is Animatable2) && animatableDrawable !is Animatable2Compat) {
            val isRunning = isRunning
            if (isRunning) {
                callbacks?.forEach { it.onAnimationEnd(this) }
            }
        }
    }

    override fun isRunning(): Boolean = animatableDrawable.isRunning

    override fun toString(): String =
        "${animatableDrawableName}(${imageInfo.toShortString()},${exifOrientationName(imageExifOrientation)},$dataFrom,${bitmapInfo.toShortString()},${transformedList},$requestKey)"
}