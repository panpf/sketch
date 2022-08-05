package com.github.panpf.sketch.cache

import android.graphics.Bitmap
import com.github.panpf.sketch.util.Logger

/**
 * [Bitmap] reuse pool, used for caching and reuse [Bitmap],
 * easy to decode direct use, reduce memory allocation
 */
interface BitmapPool {

    var logger: Logger?

    /**
     * Maximum allowed sum of the size of the all cache
     */
    val maxSize: Long

    /**
     * Sum of the size of the all cache
     */
    val size: Long

    /**
     * Puts the specified [bitmap] into the pool.
     *
     * @return If true, it was successfully placed, otherwise call the [bitmap. recycle] method to recycle the [Bitmap].
     * @see android.graphics.Bitmap.isMutable
     * @see android.graphics.Bitmap.recycle
     */
    fun put(bitmap: Bitmap, caller: String? = null): Boolean

    /**
     * Get a reusable [Bitmap].
     */
    fun getDirty(width: Int, height: Int, config: Bitmap.Config): Bitmap?

    /**
     * Get a reusable [Bitmap]. Note that all colors are erased before returning
     */
    fun get(width: Int, height: Int, config: Bitmap.Config): Bitmap?

    /**
     * Get a reusable [Bitmap] if none is available, create a new one. Note that all colors are erased before returning.
     */
    fun getOrCreate(width: Int, height: Int, config: Bitmap.Config): Bitmap

    /**
     * Returns true if the specified configured Bitmap exists in the pool
     */
    fun exist(width: Int, height: Int, config: Bitmap.Config): Boolean

    /**
     * Trim memory based on the [level]
     *
     * @param level see [android.content.ComponentCallbacks2].TRIM_MEMORY_*
     * @see android.content.ComponentCallbacks2
     */
    fun trim(level: Int)

    /**
     * Clear all cached bitmaps
     */
    fun clear()
}