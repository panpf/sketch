package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPool
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

suspend fun <R> safeAccessMemoryCache(
    sketch: Sketch,
    request: ImageRequest,
    block: suspend (helper: MemoryCacheHelper?) -> R
): R =
    if (request.memoryCachePolicy.isReadOrWrite) {
        val helper = MemoryCacheHelper(sketch, request)
        val lock: Mutex = sketch.memoryCache.editLock(helper.keys.lockKey)
        lock.lock()
        try {
            block(helper)
        } finally {
            lock.unlock()
        }
    } else {
        block(null)
    }

class MemoryCacheKeys constructor(request: ImageRequest) {
    val cacheKey: String by lazy {
        request.cacheKey
    }
    val lockKey: String by lazy {
        request.cacheKey
    }
}

class MemoryCacheHelper(sketch: Sketch, val request: ImageRequest) {

    companion object {
        const val MODULE = "BitmapMemoryCacheHelper"
    }

    private val memoryCache: MemoryCache = sketch.memoryCache
    private val logger: Logger = sketch.logger
    private val bitmapPool: BitmapPool = sketch.bitmapPool
    val keys = MemoryCacheKeys(request)

    fun read(): DrawableDecodeResult? {
        if (!request.memoryCachePolicy.readEnabled) return null
        val countBitmap = memoryCache[keys.cacheKey] ?: return null

        logger.d(MODULE) {
            "From memory get bitmap. bitmap=${countBitmap.info}. ${request.key}"
        }
        val resources = request.context.resources
        return DrawableDecodeResult(
            drawable = SketchCountBitmapDrawable(resources, countBitmap, MEMORY_CACHE),
            imageInfo = countBitmap.imageInfo,
            imageExifOrientation = countBitmap.imageExifOrientation,
            dataFrom = MEMORY_CACHE,
            transformedList = null,
        )
    }

    fun write(result: BitmapDecodeResult): SketchCountBitmapDrawable? {
        if (!request.memoryCachePolicy.writeEnabled) {
            return null
        }
        if (result.bitmap.allocationByteCountCompat >= memoryCache.maxSize * 0.7f) {
            logger.w(MODULE) {
                "write. Reject. Too big ${
                    result.bitmap.allocationByteCountCompat.formatFileSize()
                }, maxSize ${memoryCache.maxSize.formatFileSize()}, ${result.bitmap.logString}"
            }
            return null
        }

        val countBitmap = CountBitmap(
            initBitmap = result.bitmap,
            imageUri = request.uriString,
            requestKey = request.key,
            requestCacheKey = request.cacheKey,
            imageInfo = result.imageInfo,
            imageExifOrientation = result.imageExifOrientation,
            transformedList = result.transformedList,
            logger = logger,
            bitmapPool = bitmapPool
        )
        memoryCache.put(keys.cacheKey, countBitmap)
        return SketchCountBitmapDrawable(request.context.resources, countBitmap, result.dataFrom)
    }
}