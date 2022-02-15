package com.github.panpf.sketch.decode.internal

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.sync.Mutex

suspend fun <R> tryLockBitmapMemoryCache(
    sketch: Sketch,
    request: DisplayRequest,
    block: suspend (helper: BitmapMemoryCacheHelper?) -> R
): R {
    val helper = newBitmapMemoryCacheHelper(sketch, request)
    return if (helper != null) {
        val lockKey = request.cacheKey
        val lock: Mutex = sketch.diskCache.editLock(lockKey)
        lock.lock()
        try {
            block(helper)
        } finally {
            lock.unlock()
        }
    } else {
        block(helper)
    }
}

fun newBitmapMemoryCacheHelper(
    sketch: Sketch,
    request: DisplayRequest
): BitmapMemoryCacheHelper? {
    val cachePolicy = request.bitmapMemoryCachePolicy ?: ENABLED
    if (!cachePolicy.isReadOrWrite) return null
    return BitmapMemoryCacheHelper(
        sketch.memoryCache,
        request.bitmapMemoryCachePolicy ?: ENABLED,
        request.cacheKey,
        sketch.logger,
        request,
        sketch.bitmapPool,
    )
}

class BitmapMemoryCacheHelper internal constructor(
    private val memoryCache: MemoryCache,
    private val cachePolicy: CachePolicy,
    private val cacheKey: String,
    private val logger: Logger,
    private val request: DisplayRequest,
    private val bitmapPool: BitmapPool,
) {

    companion object {
        const val MODULE = "BitmapMemoryCacheHelper"
    }

    fun read(): DrawableDecodeResult? =
        if (cachePolicy.readEnabled) {
            val cachedCountBitmap = memoryCache[cacheKey]
            when {
                cachedCountBitmap != null -> {
                    logger.d(MODULE) {
                        "From memory get bitmap. bitmap=${cachedCountBitmap.info}. ${request.key}"
                    }
                    DrawableDecodeResult(
                        drawable = SketchCountBitmapDrawable(cachedCountBitmap, MEMORY_CACHE),
                        imageInfo = cachedCountBitmap.imageInfo,
                        exifOrientation = cachedCountBitmap.exifOrientation,
                        dataFrom = MEMORY_CACHE
                    )
                }
                else -> {
                    null
                }
            }
        } else {
            null
        }

    fun write(result: BitmapDecodeResult): Drawable? =
        if (cachePolicy.writeEnabled) {
            val countBitmap = CountBitmap(
                initBitmap = result.bitmap,
                requestKey = request.key,
                imageUri = request.uriString,
                imageInfo = result.imageInfo,
                exifOrientation = result.exifOrientation,
                transformedList = result.transformedList,
                logger = logger,
                bitmapPool = bitmapPool
            )
            memoryCache.put(cacheKey, countBitmap)
            SketchCountBitmapDrawable(countBitmap, result.dataFrom)
        } else {
            null
        }
}