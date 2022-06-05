package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.datasource.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.formatFileSize
import kotlinx.coroutines.sync.Mutex

suspend fun <R> tryLockMemoryCache(
    sketch: Sketch,
    request: ImageRequest,
    block: suspend (helper: MemoryCacheHelper?) -> R
): R {
    val helper = newMemoryCacheHelper(sketch, request)
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

fun ImageRequest.newMemoryCacheKey(): String = cacheKey

fun newMemoryCacheHelper(sketch: Sketch, request: ImageRequest): MemoryCacheHelper? {
    val cachePolicy = request.memoryCachePolicy
    if (!cachePolicy.isReadOrWrite) return null
    return MemoryCacheHelper(
        sketch.memoryCache,
        request.memoryCachePolicy,
        request.newMemoryCacheKey(),
        sketch.logger,
        request,
        sketch.bitmapPool,
    )
}

class MemoryCacheHelper internal constructor(
    private val memoryCache: MemoryCache,
    private val cachePolicy: CachePolicy,
    private val cacheKey: String,
    private val logger: Logger,
    private val request: ImageRequest,
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
                        drawable = SketchCountBitmapDrawable(
                            request.context.resources,
                            cachedCountBitmap,
                            MEMORY_CACHE
                        ),
                        imageInfo = cachedCountBitmap.imageInfo,
                        imageExifOrientation = cachedCountBitmap.imageExifOrientation,
                        dataFrom = MEMORY_CACHE,
                        transformedList = null,
                    )
                }
                else -> {
                    null
                }
            }
        } else {
            null
        }

    fun write(result: BitmapDecodeResult): SketchCountBitmapDrawable? =
        if (cachePolicy.writeEnabled) {
            if (result.bitmap.allocationByteCountCompat < memoryCache.maxSize * 0.7f) {
                val countBitmap = CountBitmap(
                    initBitmap = result.bitmap,
                    requestKey = request.key,
                    imageUri = request.uriString,
                    imageInfo = result.imageInfo,
                    imageExifOrientation = result.imageExifOrientation,
                    transformedList = result.transformedList,
                    logger = logger,
                    bitmapPool = bitmapPool
                )
                memoryCache.put(cacheKey, countBitmap)
                SketchCountBitmapDrawable(request.context.resources, countBitmap, result.dataFrom)
            } else {
                logger.w(MODULE) {
                    "write. Reject. Too big ${
                        result.bitmap.allocationByteCountCompat.formatFileSize()
                    }, maxSize ${memoryCache.maxSize.formatFileSize()}, ${result.bitmap.logString}"
                }
                null
            }
        } else {
            null
        }
}