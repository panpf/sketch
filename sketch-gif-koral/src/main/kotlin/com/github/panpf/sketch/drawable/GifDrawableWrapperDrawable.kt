package com.github.panpf.sketch.drawable

import android.graphics.drawable.Animatable
import android.widget.MediaController.MediaPlayerControl
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import com.github.panpf.sketch.drawable.internal.SketchDrawable
import pl.droidsonroids.gif.GifDrawable

class GifDrawableWrapperDrawable(
    val gifDrawable: GifDrawable
) : DrawableWrapperCompat(gifDrawable), Animatable, MediaPlayerControl, SketchDrawable {

    override fun start() {
        gifDrawable.start()
    }

    override fun pause() {
        gifDrawable.pause()
    }

    override fun stop() {
        gifDrawable.stop()
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GifDrawableWrapperDrawable) return false
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