package com.github.panpf.sketch.cache

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Size

/**
 * [Bitmap] reuse pool, used for caching and reuse [Bitmap],
 * easy to decode direct use, reduce memory allocation
 */
interface BitmapPool {

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

    /**
     * Set inBitmap to [BitmapFactory.Options]. Apply to [BitmapFactory]
     * The inSampleSize and inPreferredConfig attributes of [BitmapFactory.Options] will be used, so you need to set them up before doing so
     *
     * @return If true is returned, inBitmap is set
     */
    fun setInBitmapForBitmapFactory(
        options: BitmapFactory.Options, imageSize: Size, imageMimeType: String?,
    ): Boolean

    /**
     * Set inBitmap to [BitmapFactory.Options]. Apply to [BitmapRegionDecoder]
     * The inSampleSize and inPreferredConfig attributes of [BitmapFactory.Options] will be used, so you need to set them up before doing so
     *
     * @return If true is returned, inBitmap is set
     */
    fun setInBitmapForBitmapRegionDecoder(
        options: BitmapFactory.Options,
        regionSize: Size,
        imageSize: Size
    ): Boolean

    /**
     * Try to put it in the pool first, or call [Bitmap.recycle] to recycle it
     *
     * @return If true is returned, it is in the pool
     */
    fun free(bitmap: Bitmap?, caller: String? = null): Boolean

    var logger: Logger?
}