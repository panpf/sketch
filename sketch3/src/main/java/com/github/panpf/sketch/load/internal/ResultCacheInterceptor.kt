package com.github.panpf.sketch.load.internal

import android.graphics.BitmapFactory
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.Interceptor
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.load.LoadRequest
import com.github.panpf.sketch.load.LoadResult

class ResultCacheInterceptor : Interceptor<LoadRequest, LoadResult> {
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<LoadRequest, LoadResult>,
        extras: RequestExtras<LoadRequest, LoadResult>?
    ): LoadResult {
        val diskCache = sketch.diskCache
        val request = chain.request
        val resultCacheKey = request.resultCacheKey
        val encodedResultDiskCacheKey = resultCacheKey?.let {
            diskCache.encodeKey(request.uri.toString() + "_" + it)
        }
        val mutex = encodedResultDiskCacheKey?.let {
            diskCache.getOrCreateEditMutexLock(it)
        }
        mutex?.lock()
        try {
            if (encodedResultDiskCacheKey != null) {
                val diskCacheEntry = diskCache[encodedResultDiskCacheKey]
                if (diskCacheEntry != null) {
                    // todo 读取 imageinfo 缓存
                    val bitmapConfig = request.bitmapConfig
                    val inPreferQualityOverSpeed = request.inPreferQualityOverSpeed
                    if (bitmapConfig != null || inPreferQualityOverSpeed == true) {
                        BitmapFactory.Options().apply {
                            if (bitmapConfig != null) {

                            }
                        }
                    }
                    BitmapFactory.decodeFile(diskCacheEntry.file.path)
                }
            }
            val result = chain.proceed(sketch, request, extras)
            if (encodedResultDiskCacheKey != null) {
//                diskCache.
                // todo 将 imageinfo 信息也缓存起来，因为后面要用到
            }
        } finally {
            mutex?.unlock()
        }
        TODO("Not yet implemented")
    }
}