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


import android.annotation.SuppressLint
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.drawable.Animatable
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Handler
import android.os.Looper
import androidx.appcompat.graphics.drawable.DrawableWrapper
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.util.BitmapInfo
import com.github.panpf.sketch.util.getBitmapByteSize
import com.github.panpf.sketch.util.requiredMainThread

@SuppressLint("RestrictedApi")
class SketchAnimatableDrawable constructor(
    private val animatableDrawable: Drawable,
    override val imageUri: String,
    override val requestKey: String,
    override val requestCacheKey: String,
    override val imageInfo: ImageInfo,
    override val dataFrom: DataFrom,
    override val transformedList: List<String>?,
    override val extras: Map<String, String>?,
) : DrawableWrapper(animatableDrawable), SketchDrawable, Animatable2Compat {

    private var callbacks: MutableList<Animatable2Compat.AnimationCallback>? = null
    private var callbackMap: HashMap<Animatable2Compat.AnimationCallback, Animatable2.AnimationCallback>? =
        null
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    init {
        require(animatableDrawable is Animatable) {
            "animatableDrawable must implement the Animatable"
        }
    }

    override val bitmapInfo: BitmapInfo by lazy {
        BitmapInfo(
            animatableDrawable.intrinsicWidth,
            animatableDrawable.intrinsicHeight,
            getBitmapByteSize(
                animatableDrawable.intrinsicWidth,
                animatableDrawable.intrinsicHeight,
                ARGB_8888
            ),
            ARGB_8888
        )
    }

    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        requiredMainThread()    // Consistent with AnimatedImageDrawable
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
                callbackMap?.get(callback)
                    ?.let { animatableDrawable.unregisterAnimationCallback(it) } == true
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
        val animatableDrawable = animatableDrawable as Animatable
        if (animatableDrawable.isRunning) {
            return
        }
        animatableDrawable.start()
        val callbacks = callbacks
        if (callbacks != null && !(VERSION.SDK_INT >= VERSION_CODES.M && animatableDrawable is Animatable2) && animatableDrawable !is Animatable2Compat) {
            handler.post {
                for (callback in callbacks) {
                    callback.onAnimationStart(this)
                }
            }
        }
    }

    override fun stop() {
        val animatableDrawable = animatableDrawable as Animatable
        if (!animatableDrawable.isRunning) {
            return
        }
        animatableDrawable.stop()
        val callbacks = callbacks
        if (callbacks != null && !(VERSION.SDK_INT >= VERSION_CODES.M && animatableDrawable is Animatable2) && animatableDrawable !is Animatable2Compat) {
            handler.post {
                for (callback in callbacks) {
                    callback.onAnimationEnd(this)
                }
            }
        }
    }

    override fun isRunning(): Boolean {
        val animatableDrawable = animatableDrawable
        if (animatableDrawable !is Animatable) {
            throw IllegalArgumentException("Drawable must implement the Animatable interface")
        }
        return animatableDrawable.isRunning
    }

    @SuppressLint("RestrictedApi")
    override fun mutate(): SketchAnimatableDrawable {
        val mutateDrawable = wrappedDrawable.mutate()
        return if (mutateDrawable !== wrappedDrawable) {
            SketchAnimatableDrawable(
                animatableDrawable = mutateDrawable,
                imageUri = imageUri,
                requestKey = requestKey,
                requestCacheKey = requestCacheKey,
                imageInfo = imageInfo,
                dataFrom = dataFrom,
                transformedList = transformedList,
                extras = extras,
            )
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SketchAnimatableDrawable) return false
        if (animatableDrawable != other.animatableDrawable) return false
        if (imageUri != other.imageUri) return false
        if (requestKey != other.requestKey) return false
        if (requestCacheKey != other.requestCacheKey) return false
        if (imageInfo != other.imageInfo) return false
        if (dataFrom != other.dataFrom) return false
        if (transformedList != other.transformedList) return false
        return true
    }

    override fun hashCode(): Int {
        var result = animatableDrawable.hashCode()
        result = 31 * result + imageUri.hashCode()
        result = 31 * result + requestKey.hashCode()
        result = 31 * result + requestCacheKey.hashCode()
        result = 31 * result + imageInfo.hashCode()
        result = 31 * result + dataFrom.hashCode()
        result = 31 * result + (transformedList?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "SketchAnimatableDrawable(" +
                animatableDrawable +
                ", " + imageInfo.toShortString() +
                "," + dataFrom +
                "," + bitmapInfo.toShortString() +
                "," + transformedList +
                "," + requestKey +
                ")"
}