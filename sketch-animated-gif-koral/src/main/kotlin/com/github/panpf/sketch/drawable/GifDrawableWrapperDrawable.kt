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

import android.graphics.Canvas
import android.graphics.drawable.Animatable
import android.widget.MediaController.MediaPlayerControl
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import pl.droidsonroids.gif.GifDrawable

/**
 * GifDrawable wrapper, which implements the [Animatable] and [MediaPlayerControl] interfaces
 *
 * @see com.github.panpf.sketch.animated.gif.koral.test.drawable.GifDrawableWrapperDrawableTest
 */
/**
 * GifDrawable wrapper, which implements the [Animatable] and [MediaPlayerControl] interfaces
 *
 * @see com.github.panpf.sketch.animated.gif.koral.test.drawable.GifDrawableWrapperDrawableTest
 */
class GifDrawableWrapperDrawable(
    val gifDrawable: GifDrawable
) : DrawableWrapperCompat(gifDrawable), Animatable2Compat, MediaPlayerControl, SketchDrawable {

    private var callbacks: MutableList<Animatable2Compat.AnimationCallback>? = null
    private var callbackStartOnDraw: Boolean = true
    private var callbackEndOnAnimationCompleted: Boolean = false

    init {
        gifDrawable.addAnimationListener { loopNumber ->
            if (callbackEndOnAnimationCompleted) {
                val loopCount = gifDrawable.loopCount
                if (loopCount > 0 && loopCount == loopNumber + 1) {
                    callbackEndOnAnimationCompleted = false
                    callbacks?.forEach { it.onAnimationEnd(this) }
                }
            }
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (callbackStartOnDraw && gifDrawable.isRunning) {
            callbackStartOnDraw = false
            callbackEndOnAnimationCompleted = true
            callbacks?.forEach { it.onAnimationStart(this) }
        }
    }

    override fun start() {
        if (isRunning) return
        callbackStartOnDraw = false
        callbackEndOnAnimationCompleted = true
        gifDrawable.start()
        callbacks?.forEach { it.onAnimationStart(this) }
    }

    override fun pause() {
        gifDrawable.pause()
    }

    override fun stop() {
        if (!isRunning) return
        callbackEndOnAnimationCompleted = false
        gifDrawable.stop()
        callbacks?.forEach { it.onAnimationEnd(this) }
    }

    override fun isRunning(): Boolean {
        return gifDrawable.isRunning
    }

    override fun getDuration(): Int {
        return gifDrawable.duration
    }

    override fun getCurrentPosition(): Int {
        return gifDrawable.currentPosition
    }

    override fun seekTo(pos: Int) {
        gifDrawable.seekTo(pos)
    }

    override fun isPlaying(): Boolean {
        return gifDrawable.isPlaying
    }

    override fun getBufferPercentage(): Int {
        return gifDrawable.bufferPercentage
    }

    override fun canPause(): Boolean {
        return gifDrawable.canPause()
    }

    override fun canSeekBackward(): Boolean {
        return gifDrawable.canSeekBackward()
    }

    override fun canSeekForward(): Boolean {
        return gifDrawable.canSeekForward()
    }

    override fun getAudioSessionId(): Int {
        return gifDrawable.audioSessionId
    }

    override fun mutate(): GifDrawableWrapperDrawable {
        val mutateDrawable = gifDrawable.mutate()
        return if (mutateDrawable !== gifDrawable) {
            GifDrawableWrapperDrawable(gifDrawable)
        } else {
            this
        }
    }

    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        val callbacks = callbacks
            ?: mutableListOf<Animatable2Compat.AnimationCallback>().apply {
                this@GifDrawableWrapperDrawable.callbacks = this
            }
        callbacks.add(callback)
    }

    override fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean {
        return callbacks?.remove(callback) == true
    }

    override fun clearAnimationCallbacks() {
        callbacks?.clear()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as GifDrawableWrapperDrawable
        if (gifDrawable != other.gifDrawable) return false
        return true
    }

    override fun hashCode(): Int {
        return gifDrawable.hashCode()
    }

    override fun toString(): String {
        return "GifDrawableWrapperDrawable(${gifDrawable.intrinsicWidth}x${gifDrawable.intrinsicHeight})"
    }
}