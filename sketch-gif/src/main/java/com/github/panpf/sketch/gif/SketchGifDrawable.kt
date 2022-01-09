package com.github.panpf.sketch.gif

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

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Animatable
import android.widget.MediaController.MediaPlayerControl
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.github.panpf.sketch.drawable.SketchDrawable
import java.io.InputStream
import java.nio.ByteBuffer

interface SketchGifDrawable : SketchDrawable, Animatable, MediaPlayerControl {
    /**
     * Frees any memory allocated native way.
     * Operation is irreversible. After this call, nothing will be drawn.
     * This method is idempotent, subsequent calls have no effect.
     * Like [android.graphics.Bitmap.recycle] this is an advanced call and
     * is invoked implicitly by finalizer.
     */
    fun recycle()

    /**
     * @return true if drawable is recycled
     */
    fun isRecycled(): Boolean

    /**
     * Causes the animation to start over.
     * If rewinding input source fails then state is not affected.
     * This method is thread-safe.
     */
    fun reset()

    /**
     * Returns GIF comment
     *
     * @return comment or null if there is no one defined in file
     */
    fun getComment(): String?

    /**
     * Sets loop count of the animation. Loop count must be in range `<0 ,65535>`
     *
     * @this loopCount loop count, 0 means infinity
     */
    fun getLoopCount(): Int

    /**
     * @return number of frames in GIF, at least one
     */
    fun getNumberOfFrames(): Int

    /**
     * Sets new animation speed factor.<br></br>
     * Note: If animation is in progress was already called)
     * then effects will be visible starting from the next frame. Duration of the currently rendered
     * frame is not affected.
     *
     * @param factor new speed factor, eg. 0.5f means half speed, 1.0f - normal, 2.0f - double speed
     * @throws IllegalArgumentException if factor&lt;=0
     */
    fun setSpeed(@FloatRange(from = 0.0, fromInclusive = false) factor: Float)

    /**
     * Like but uses index of the frame instead of time.
     * If `frameIndex` exceeds number of frames, seek stops at the end, no exception is thrown.
     *
     * @param frameIndex index of the frame to seek to (zero based)
     * @throws IllegalArgumentException if `frameIndex`&lt;0
     */
    fun seekToFrame(@IntRange(from = 0, to = Int.MAX_VALUE.toLong()) frameIndex: Int)

    /**
     * Like [.seekToFrame] but performs operation synchronously and returns that frame.
     *
     * @param frameIndex index of the frame to seek to (zero based)
     * @return frame at desired index
     * @throws IndexOutOfBoundsException if frameIndex&lt;0
     */
    fun seekToFrameAndGet(@IntRange(from = 0, to = Int.MAX_VALUE.toLong()) frameIndex: Int): Bitmap?

    /**
     * Like but performs operation synchronously and returns that frame.
     *
     * @param position position to seek to in milliseconds
     * @return frame at desired position
     * @throws IndexOutOfBoundsException if position&lt;0
     */
    fun seekToPositionAndGet(
        @IntRange(
            from = 0,
            to = Int.MAX_VALUE.toLong()
        ) position: Int
    ): Bitmap?

    /**
     * Returns the minimum number of bytes that can be used to store pixels of the single frame.
     * Returned value is the same for all the frames since it is based on the size of GIF screen.
     *
     * This method should not be used to calculate the memory usage of the bitmap.
     * Instead see [.getAllocationByteCount].
     *
     * @return the minimum number of bytes that can be used to store pixels of the single frame
     */
    fun getFrameByteCount(): Int

    /**
     * Returns size of the memory needed to store pixels of this object. It counts possible length of all frame buffers.
     * Returned value may be lower than amount of actually allocated memory if GIF uses dispose to previous method but frame requiring it
     * has never been needed yet. Returned value does not change during runtime.
     *
     * @return possible size of the memory needed to store pixels of this object
     */
    fun getAllocationByteCount(): Long

    /**
     * Returns the maximum possible size of the allocated memory used to store pixels and metadata of this object.
     * It counts length of all frame buffers. Returned value does not change over time.
     *
     * @return maximum possible size of the allocated memory needed to store metadata of this object
     */
    fun getMetadataAllocationByteCount(): Long

    /**
     * Returns length of the input source obtained at the opening time or -1 if
     * length cannot be determined. Returned value does not change during runtime.
     * If GifDrawable is constructed from [InputStream] -1 is always returned.
     * In case of byte array and [ByteBuffer] length is always known.
     * In other cases length -1 can be returned if length cannot be determined.
     *
     * @return number of bytes backed by input source or -1 if it is unknown
     */
    fun getInputSourceByteCount(): Long

    /**
     * Returns in pixels[] a copy of the data in the current frame. Each value is a packed int representing a [Color].
     *
     * @param pixels the array to receive the frame's colors
     * @throws ArrayIndexOutOfBoundsException if the pixels array is too small to receive required number of pixels
     */
    fun getPixels(pixels: IntArray)

    /**
     * Returns the [Color] at the specified location. Throws an exception
     * if x or y are out of bounds (negative or &gt;= to the width or height
     * respectively). The returned color is a non-premultiplied ARGB value.
     *
     * @param x The x coordinate (0...width-1) of the pixel to return
     * @param y The y coordinate (0...height-1) of the pixel to return
     * @return The argb [Color] at the specified coordinate
     * @throws IllegalArgumentException if x, y exceed the drawable's bounds
     * @throws IllegalStateException    if drawable is recycled
     */
    fun getPixel(x: Int, y: Int): Int

    /**
     * @return the paint used to render this drawable
     */
    fun getPaint(): Paint

    /**
     * Adds a new animation listener
     *
     * @param listener animation listener to be added, not null
     * @throws java.lang.NullPointerException if listener is null
     */
    fun addAnimationListener(listener: AnimationListener)

    /**
     * Removes an animation listener
     *
     * @param listener animation listener to be removed
     * @return true if listener collection has been modified
     */
    fun removeAnimationListener(listener: AnimationListener?): Boolean

    /**
     * Retrieves a copy of currently buffered frame.
     *
     * @return current frame
     */
    fun getCurrentFrame(): Bitmap?

    /**
     * Returns zero-based index of recently rendered frame in given loop or -1 when drawable is recycled.
     *
     * @return index of recently rendered frame or -1 when drawable is recycled
     */
    fun getCurrentFrameIndex(): Int

    /**
     * Returns zero-based index of currently played animation loop. If animation is infinite or
     * drawable is recycled 0 is returned.
     *
     * @return index of currently played animation loop
     */
    fun getCurrentLoop(): Int

    /**
     * Returns whether all animation loops has ended. If drawable is recycled false is returned.
     *
     * @return true if all animation loops has ended
     */
    fun isAnimationCompleted(): Boolean

    /**
     * Returns duration of the given frame (in milliseconds). If there is no data (no Graphics
     * Control Extension blocks or drawable is recycled) 0 is returned.
     *
     * @param index index of the frame
     * @return duration of the given frame in milliseconds
     * @throws IndexOutOfBoundsException if index &lt; 0 or index &gt;= number of frames
     */
    fun getFrameDuration(@IntRange(from = 0) index: Int): Int

    /**
     * 跟随页面是否可见停止或播放gif
     *
     * @param userVisible          页面是否可见
     * @param fromDisplayCompleted 当图片加载完毕，但是页面不可见时需要停留在第一帧
     */
    fun followPageVisible(userVisible: Boolean, fromDisplayCompleted: Boolean)

    /**
     * Interface which can be used to run some code when particular animation event occurs.
     */
    interface AnimationListener {
        /**
         * Called when a single loop of the animation is completed.
         *
         * @param loopNumber 0-based number of the completed loop, 0 for infinite animations
         */
        fun onAnimationCompleted(loopNumber: Int)
    }
}