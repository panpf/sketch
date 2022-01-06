package com.github.panpf.sketch.request.internal

import android.graphics.drawable.BitmapDrawable
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import com.github.panpf.sketch.drawable.SketchRefBitmap
import com.github.panpf.sketch.request.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.RequestDepth

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
        val memoryCache = sketch.memoryCache
        val memoryCacheKey = request.memoryCacheKey
        val memoryCachePolicy = request.memoryCachePolicy

        val memoryCacheEditLock = if (memoryCachePolicy.isReadOrWrite) {
            memoryCache.getOrCreateEditMutexLock(memoryCacheKey)
        } else {
            null
        }
        memoryCacheEditLock?.lock()
        try {
            if (memoryCachePolicy.readEnabled) {
                val cachedRefBitmap = memoryCache[memoryCacheKey]
                if (cachedRefBitmap != null) {
                    sketch.logger.d(MODULE) {
                        "From memory get bitmap. bitmap=%s. %s".format(
                            cachedRefBitmap.info, request.key
                        )
                    }
                    cachedRefBitmap.setIsWaitingUse("$MODULE:waitingUse:fromMemory", true)
                    val drawable = SketchBitmapDrawable(cachedRefBitmap, MEMORY_CACHE)
                    return DisplayData(drawable, cachedRefBitmap.imageInfo, MEMORY_CACHE)
                } else if (request.depth >= RequestDepth.MEMORY) {
                    throw RequestDepthException(request, request.depth)
                }
            }

            val loadRequest = request.newLoadRequest()
            val loadData: LoadData = LoadInterceptorChain(
                initialRequest = loadRequest,
                interceptors = sketch.loadInterceptors,
                index = 0,
                request = loadRequest,
            ).proceed(sketch, loadRequest)

            val bitmap = loadData.bitmap
            val drawable = if (memoryCachePolicy.writeEnabled) {
                val refBitmap =
                    SketchRefBitmap(bitmap, loadData.info, request.key, sketch.bitmapPoolHelper)
                refBitmap.setIsWaitingUse("$MODULE:waitingUse:new", true)
                memoryCache.put(memoryCacheKey, refBitmap)
                SketchBitmapDrawable(refBitmap, loadData.from)
            } else {
                BitmapDrawable(sketch.appContext.resources, bitmap)
            }
            return DisplayData(drawable, loadData.info, loadData.from)
        } finally {
            memoryCacheEditLock?.unlock()
        }
    }
}