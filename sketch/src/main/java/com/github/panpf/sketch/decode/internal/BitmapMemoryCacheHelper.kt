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
import com.github.panpf.sketch.request.RequestDepth.MEMORY
import com.github.panpf.sketch.request.internal.RequestDepthException
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.sync.Mutex

fun newBitmapMemoryCacheHelper(
    sketch: Sketch,
    request: DisplayRequest
): BitmapMemoryCacheHelper? {
    val cachePolicy = request.bitmapMemoryCachePolicy ?: ENABLED
    return if (cachePolicy.isReadOrWrite) {
        BitmapMemoryCacheHelper(
            sketch.memoryCache,
            cachePolicy,
            request.cacheKey,
            sketch.logger,
            request,
            sketch.bitmapPool,
        )
    } else {
        null
    }
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

    val lock: Mutex by lazy {
        memoryCache.editLock(cacheKey)
    }

    fun read(): DrawableDecodeResult? =
        if (cachePolicy.readEnabled) {
            val cachedCountBitmap = memoryCache[cacheKey]
            val requestDepth = request.depth
            when {
                cachedCountBitmap != null -> {
                    logger.d(MODULE) {
                        "From memory get bitmap. bitmap=%s. %s"
                            .format(cachedCountBitmap.info, request.key)
                    }
                    cachedCountBitmap.setIsWaiting(
                        "${MODULE}:waitingUse:fromMemory",
                        true
                    )
                    val drawable = SketchCountBitmapDrawable(cachedCountBitmap, MEMORY_CACHE)
                    DrawableDecodeResult(
                        drawable = drawable,
                        imageInfo = cachedCountBitmap.imageInfo,
                        exifOrientation = cachedCountBitmap.exifOrientation,
                        dataFrom = MEMORY_CACHE
                    )
                }
                requestDepth != null && requestDepth >= MEMORY -> {
                    throw RequestDepthException(request, requestDepth, request.depthFrom)
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
            countBitmap.setIsWaiting("${MODULE}:waitingUse:new", true)
            memoryCache.put(cacheKey, countBitmap)
            SketchCountBitmapDrawable(countBitmap, result.dataFrom)
        } else {
            null
        }
}