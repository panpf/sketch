package com.github.panpf.sketch.decode

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestDepth
import com.github.panpf.sketch.request.internal.RequestDepthException
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

class DefaultDrawableDecoder(
    private val sketch: Sketch,
    private val request: DisplayRequest,
    private val fetchResult: FetchResult
) : DrawableDecoder {

    companion object {
        const val MODULE = "DefaultDrawableDecoder"
    }

    override suspend fun decodeDrawable(): DrawableDecodeResult =
        withContext(sketch.decodeTaskDispatcher) {
            val memoryCacheHelper = BitmapMemoryCacheHelper.from(sketch, request)
            memoryCacheHelper?.lock?.lock()
            return@withContext try {
                memoryCacheHelper?.read()
                    ?: BitmapDecodeInterceptorChain(
                        initialRequest = request,
                        interceptors = sketch.bitmapDecodeInterceptors,
                        index = 0,
                        sketch = sketch,
                        request = request,
                        fetchResult = fetchResult
                    ).proceed(request).run {
                        val drawable = memoryCacheHelper?.write(this)
                            ?: SketchBitmapDrawable(
                                request.key,
                                request.uriString,
                                this.imageInfo,
                                this.dataFrom,
                                this.transformedList,
                                this.bitmap
                            )
                        DrawableDecodeResult(drawable, this.imageInfo, this.dataFrom)
                    }
            } finally {
                memoryCacheHelper?.lock?.unlock()
            }
        }

    override fun close() {

    }

    class Factory : DrawableDecoder.Factory {
        override fun create(
            sketch: Sketch, request: DisplayRequest, fetchResult: FetchResult
        ): DrawableDecoder = DefaultDrawableDecoder(sketch, request, fetchResult)

        override fun toString(): String = "DefaultDrawableDecoder"
    }

    private class BitmapMemoryCacheHelper(
        private val memoryCache: MemoryCache,
        private val bitmapMemoryCachePolicy: CachePolicy,
        private val bitmapMemoryCacheKey: String,
        private val logger: Logger,
        private val request: DisplayRequest,
        private val bitmapPool: BitmapPool,
    ) {

        val lock: Mutex by lazy {
            memoryCache.editLock(bitmapMemoryCacheKey)
        }

        fun read(): DrawableDecodeResult? {
            if (!bitmapMemoryCachePolicy.readEnabled) {
                return null
            }

            val cachedCountBitmap = memoryCache[bitmapMemoryCacheKey]
            val requestDepth = request.depth
            return when {
                cachedCountBitmap != null -> {
                    logger.d(MODULE) {
                        "From memory get bitmap. bitmap=%s. %s"
                            .format(cachedCountBitmap.info, request.key)
                    }
                    cachedCountBitmap.setIsWaiting("$MODULE:waitingUse:fromMemory", true)
                    val drawable = SketchCountBitmapDrawable(cachedCountBitmap, MEMORY_CACHE)
                    DrawableDecodeResult(drawable, cachedCountBitmap.imageInfo, MEMORY_CACHE)
                }
                requestDepth != null && requestDepth >= RequestDepth.MEMORY -> {
                    throw RequestDepthException(request, requestDepth, request.depthFrom)
                }
                else -> {
                    null
                }
            }
        }

        fun write(bitmapDecodeResult: BitmapDecodeResult): Drawable? =
            if (bitmapMemoryCachePolicy.writeEnabled) {
                val countBitmap = CountBitmap(
                    bitmapDecodeResult.bitmap,
                    request.key,
                    request.uriString,
                    bitmapDecodeResult.imageInfo,
                    bitmapDecodeResult.transformedList,
                    logger,
                    bitmapPool
                )
                countBitmap.setIsWaiting("$MODULE:waitingUse:new", true)
                memoryCache.put(bitmapMemoryCacheKey, countBitmap)
                SketchCountBitmapDrawable(countBitmap, bitmapDecodeResult.dataFrom)
            } else {
                null
            }

        companion object {

            @JvmStatic
            fun from(sketch: Sketch, request: DisplayRequest): BitmapMemoryCacheHelper? {
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
        }
    }
}