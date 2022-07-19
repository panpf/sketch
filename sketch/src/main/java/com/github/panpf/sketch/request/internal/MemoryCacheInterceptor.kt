package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.request.toDisplayData
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.ifOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

class MemoryCacheInterceptor : RequestInterceptor {

    @WorkerThread
    override suspend fun intercept(chain: Chain): ImageData {
        val request = chain.request
        val memoryCache = chain.sketch.memoryCache

        if (request is DisplayRequest) {
            val cachedCountBitmap = ifOrNull(request.memoryCachePolicy.readEnabled) {
                memoryCache.lockMemoryCache(request) {
                    chain.sketch.memoryCache[request.memoryCacheKey]
                }
            }
            if (cachedCountBitmap != null) {
                val countDrawable = SketchCountBitmapDrawable(
                    request.context.resources, cachedCountBitmap, DataFrom.MEMORY_CACHE
                ).apply {
                    withContext(Dispatchers.Main) {
                        chain.requestContext.pendingCountDrawable(this@apply, "loadBefore")
                    }
                }

                return DrawableDecodeResult(
                    drawable = countDrawable,
                    imageInfo = cachedCountBitmap.imageInfo,
                    imageExifOrientation = cachedCountBitmap.imageExifOrientation,
                    dataFrom = DataFrom.MEMORY_CACHE,
                    transformedList = cachedCountBitmap.transformedList,
                ).toDisplayData()
            }

            val depth = request.depth
            if (depth >= Depth.MEMORY) {
                throw DepthException("Request depth limited to $depth. ${request.uriString}")
            }

            return if (request.memoryCachePolicy.writeEnabled) {
                memoryCache.lockMemoryCache(request) {
                    chain.proceed(request).also { imageData ->
                        val countDrawable = imageData.asOrThrow<DisplayData>()
                            .drawable.asOrNull<SketchCountBitmapDrawable>()
                        if (countDrawable != null) {
                            chain.sketch.memoryCache
                                .put(request.memoryCacheKey, countDrawable.countBitmap)
                        }
                    }
                }
            } else {
                chain.proceed(request)
            }
        } else {
            return chain.proceed(request)
        }
    }

    private suspend fun <R> MemoryCache.lockMemoryCache(
        request: ImageRequest,
        block: suspend () -> R
    ): R {
        val lock: Mutex = editLock(request.memoryCacheLockKey)
        lock.lock()
        try {
            return block()
        } finally {
            lock.unlock()
        }
    }

    override fun toString(): String = "MemoryCacheInterceptor"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

val ImageRequest.memoryCacheKey: String
    get() = cacheKey

val ImageRequest.memoryCacheLockKey: String
    get() = cacheKey