package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.formatFileSize
import kotlinx.coroutines.sync.Mutex

suspend fun <R> safeAccessMemoryCache(
    sketch: Sketch,
    request: ImageRequest,
    block: suspend (helper: MemoryCacheHelper?) -> R
): R = if (request.memoryCachePolicy.isReadOrWrite) {
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
    val keys = MemoryCacheKeys(request)

    fun read(): CountBitmap? {
        if (!request.memoryCachePolicy.readEnabled) return null
        val countBitmap = memoryCache[keys.cacheKey] ?: return null
        logger.d(MODULE) {
            "From memory get bitmap. bitmap=${countBitmap.info}. ${request.key}"
        }
        return countBitmap
    }

    fun write(countBitmap: CountBitmap): Boolean {
        if (!request.memoryCachePolicy.writeEnabled) {
            return false
        }
        val bitmap = countBitmap.bitmap ?: return false
        if (bitmap.allocationByteCountCompat >= memoryCache.maxSize * 0.7f) {
            logger.w(MODULE) {
                val bitmapSize = bitmap.allocationByteCountCompat.formatFileSize()
                "write. Reject. Bitmap too big ${bitmapSize}, maxSize ${memoryCache.maxSize.formatFileSize()}, ${bitmap.logString}"
            }
            return false
        }
        return memoryCache.put(keys.cacheKey, countBitmap)
    }
}