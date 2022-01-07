package com.github.panpf.sketch.request.internal

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPoolHelper
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import com.github.panpf.sketch.drawable.SketchRefBitmap
import com.github.panpf.sketch.request.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.RequestDepth
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.sync.Mutex

class DisplayEngineInterceptor : Interceptor<DisplayRequest, DisplayData> {

    companion object {
        const val MODULE = "DisplayEngineInterceptor"
    }

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<DisplayRequest, DisplayData>,
    ): DisplayData {
        val request = chain.request

        val memoryCacheHelper = DisplayMemoryCacheHelper.from(sketch, request)
        memoryCacheHelper?.lock?.lock()
        try {
            val dataFromMemoryCache = memoryCacheHelper?.read()
            if (dataFromMemoryCache != null) {
                return dataFromMemoryCache
            }

            val loadRequest = request.newLoadRequest()
            val loadData: LoadData = LoadInterceptorChain(
                initialRequest = loadRequest,
                interceptors = sketch.loadInterceptors,
                index = 0,
                request = loadRequest,
            ).proceed(sketch, loadRequest)

            val drawable = memoryCacheHelper?.write(loadData) ?: BitmapDrawable(
                sketch.appContext.resources,
                loadData.bitmap
            )
            return DisplayData(drawable, loadData.info, loadData.from)
        } finally {
            memoryCacheHelper?.lock?.unlock()
        }
    }

    class DisplayMemoryCacheHelper(
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

        fun read(): DisplayData? {
            if (!memoryCachePolicy.readEnabled) {
                return null
            }

            val cachedRefBitmap = memoryCache[memoryCacheKey]
            return when {
                cachedRefBitmap != null -> {
                    logger.d(MODULE) {
                        "From memory get bitmap. bitmap=%s. %s".format(
                            cachedRefBitmap.info, request.key
                        )
                    }
                    cachedRefBitmap.setIsWaitingUse(
                        "${MODULE}:waitingUse:fromMemory",
                        true
                    )
                    val drawable = SketchBitmapDrawable(cachedRefBitmap, MEMORY_CACHE)
                    DisplayData(drawable, cachedRefBitmap.imageInfo, MEMORY_CACHE)
                }
                request.depth >= RequestDepth.MEMORY -> {
                    throw RequestDepthException(request, request.depth)
                }
                else -> {
                    null
                }
            }
        }

        fun write(loadData: LoadData): Drawable? =
            if (memoryCachePolicy.writeEnabled) {
                val refBitmap =
                    SketchRefBitmap(loadData.bitmap, loadData.info, request.key, bitmapPoolHelper)
                refBitmap.setIsWaitingUse("$MODULE:waitingUse:new", true)
                memoryCache.put(memoryCacheKey, refBitmap)
                SketchBitmapDrawable(refBitmap, loadData.from)
            } else {
                null
            }

        companion object {

            @JvmStatic
            fun from(sketch: Sketch, request: DisplayRequest): DisplayMemoryCacheHelper? {
                return if (request.memoryCachePolicy.isReadOrWrite) {
                    DisplayMemoryCacheHelper(
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