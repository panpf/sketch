package com.github.panpf.sketch.request.internal

import android.graphics.drawable.BitmapDrawable
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import com.github.panpf.sketch.drawable.SketchRefBitmap
import com.github.panpf.sketch.request.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.request.DisplayException
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.RequestDepth
import com.github.panpf.sketch.util.SLog

class DisplayEngineInterceptor : Interceptor<DisplayRequest, DisplayResult> {

    companion object {
        const val MODULE = "DisplayEngineInterceptor"
    }

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<DisplayRequest, DisplayResult>,
    ): DisplayResult {
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
                    if (SLog.isLoggable(SLog.DEBUG)) {
                        SLog.dmf(
                            MODULE,
                            "From memory get bitmap. bitmap=%s. %s",
                            cachedRefBitmap.info, request.key
                        )
                    }
                    cachedRefBitmap.setIsWaitingUse("$MODULE:waitingUse:fromMemory", true)
                    val drawable = SketchBitmapDrawable(cachedRefBitmap, MEMORY_CACHE)
                    return DisplayResult(drawable, cachedRefBitmap.imageInfo, MEMORY_CACHE)
                } else if (request.depth >= RequestDepth.MEMORY) {
                    throw DisplayException("Request depth only to MEMORY")
                }
            }

            val loadRequest = request.toLoadRequest()
            val loadResult: LoadResult = LoadInterceptorChain(
                initialRequest = loadRequest,
                interceptors = sketch.loadInterceptors,
                index = 0,
                request = loadRequest,
            ).proceed(sketch, loadRequest)

            val bitmap = loadResult.bitmap
            val drawable = if (memoryCachePolicy.writeEnabled) {
                val refBitmap =
                    SketchRefBitmap(bitmap, loadResult.info, request.key, sketch.bitmapPoolHelper)
                refBitmap.setIsWaitingUse("$MODULE:waitingUse:new", true)
                memoryCache.put(memoryCacheKey, refBitmap)
                SketchBitmapDrawable(refBitmap, loadResult.from)
            } else {
                BitmapDrawable(sketch.appContext.resources, bitmap)
            }
            return DisplayResult(drawable, loadResult.info, loadResult.from)
        } finally {
            memoryCacheEditLock?.unlock()
        }
    }
}