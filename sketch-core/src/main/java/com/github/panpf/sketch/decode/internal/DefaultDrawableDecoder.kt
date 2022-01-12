package com.github.panpf.sketch.decode.internal

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPoolHelper
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import com.github.panpf.sketch.drawable.SketchRefBitmap
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
    private val dataSource: DataSource
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
                        dataSource = dataSource
                    ).proceed(request).run {
                        val drawable = memoryCacheHelper?.write(this)
                            ?: BitmapDrawable(sketch.appContext.resources, this.bitmap)
                        DrawableDecodeResult(drawable, this.info, this.from)
                    }
            } finally {
                memoryCacheHelper?.lock?.unlock()
            }
        }

    class Factory : DrawableDecoder.Factory {
        override fun create(
            sketch: Sketch,
            request: DisplayRequest,
            dataSource: DataSource
        ): DrawableDecoder = DefaultDrawableDecoder(sketch, request, dataSource)
    }

    private class BitmapMemoryCacheHelper(
        private val memoryCache: MemoryCache,
        private val memoryCachePolicy: CachePolicy,
        private val memoryCacheKey: String,
        private val logger: Logger,
        private val request: DisplayRequest,
        private val bitmapPoolHelper: BitmapPoolHelper,
    ) {

        val lock: Mutex by lazy {
            memoryCache.getOrCreateEditMutexLock(memoryCacheKey)
        }

        fun read(): DrawableDecodeResult? {
            if (!memoryCachePolicy.readEnabled) {
                return null
            }

            val cachedRefBitmap = memoryCache[memoryCacheKey]
            return when {
                cachedRefBitmap != null -> {
                    logger.d(MODULE) {
                        "From memory get bitmap. bitmap=%s. %s"
                            .format(cachedRefBitmap.info, request.key)
                    }
                    cachedRefBitmap.setIsWaitingUse("${MODULE}:waitingUse:fromMemory", true)
                    val drawable = SketchBitmapDrawable(cachedRefBitmap, MEMORY_CACHE)
                    DrawableDecodeResult(drawable, cachedRefBitmap.imageInfo, MEMORY_CACHE)
                }
                request.depth >= RequestDepth.MEMORY -> {
                    throw RequestDepthException(request, request.depth, request.depthFrom)
                }
                else -> {
                    null
                }
            }
        }

        fun write(bitmapDecodeResult: BitmapDecodeResult): Drawable? =
            if (memoryCachePolicy.writeEnabled) {
                val refBitmap = SketchRefBitmap(
                    bitmapDecodeResult.bitmap,
                    request.uriString,
                    bitmapDecodeResult.info,
                    request.key,
                    bitmapPoolHelper
                )
                refBitmap.setIsWaitingUse("${MODULE}:waitingUse:new", true)
                memoryCache.put(memoryCacheKey, refBitmap)
                SketchBitmapDrawable(refBitmap, bitmapDecodeResult.from)
            } else {
                null
            }

        companion object {

            @JvmStatic
            fun from(sketch: Sketch, request: DisplayRequest): BitmapMemoryCacheHelper? {
                return if (request.memoryCachePolicy.isReadOrWrite) {
                    BitmapMemoryCacheHelper(
                        sketch.memoryCache,
                        request.memoryCachePolicy,
                        request.memoryCacheKey,
                        sketch.logger,
                        request,
                        sketch.bitmapPoolHelper,
                    )
                } else {
                    null
                }
            }
        }
    }
}