package com.github.panpf.sketch.request.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.request.DataFrom.DISK_CACHE
import com.github.panpf.sketch.request.ImageInfo
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.util.SLog
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

class LoadResultCacheInterceptor : Interceptor<LoadRequest, LoadResult> {

    companion object {
        const val MODULE = "LoadResultCacheInterceptor"
    }

    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<LoadRequest, LoadResult>,
        httpFetchProgressListenerDelegate: ProgressListenerDelegate<LoadRequest>?
    ): LoadResult {
        val diskCache = sketch.diskCache
        val request = chain.request
        val resultCacheHelper = ResultCacheHelper.from(request, diskCache)
        val mutex = resultCacheHelper?.getOrCreateEditMutexLock()
        mutex?.lock()
        try {
            if (resultCacheHelper != null && request.resultDiskCachePolicy.readEnabled) {
                val cacheLoadResult = withContext(sketch.decodeTaskDispatcher) {
                    resultCacheHelper.readLoadResult()
                }
                if (cacheLoadResult != null) {
                    return cacheLoadResult
                }
            }

            val loadResult = chain.proceed(sketch, request, httpFetchProgressListenerDelegate)

            if (resultCacheHelper != null && request.resultDiskCachePolicy.writeEnabled) {
                withContext(sketch.decodeTaskDispatcher) {
                    resultCacheHelper.writeLoadResult(loadResult)
                }
            }
            return loadResult
        } finally {
            mutex?.unlock()
        }
    }

    private class ResultCacheHelper(
        private val request: LoadRequest,
        private val diskCache: DiskCache,
        val encodedBitmapDataDiskCacheKey: String,
        val encodedMetaDataDiskCacheKey: String,
    ) {

        fun getOrCreateEditMutexLock(): Mutex =
            diskCache.getOrCreateEditMutexLock(encodedBitmapDataDiskCacheKey)

        fun readLoadResult(): LoadResult? {
            if (!request.resultDiskCachePolicy.readEnabled) {
                return null
            }

            val bitmapDataDiskCacheEntry = diskCache[encodedBitmapDataDiskCacheKey]
            val metaDataDiskCacheEntry = diskCache[encodedMetaDataDiskCacheKey]
            try {
                return if (bitmapDataDiskCacheEntry != null && metaDataDiskCacheEntry != null) {
                    val jsonString = metaDataDiskCacheEntry.newInputStream().use {
                        it.bufferedReader().readText()
                    }
                    val imageInfo = ImageInfo.fromJsonString(jsonString)
                    val bitmap = BitmapFactory.decodeFile(
                        bitmapDataDiskCacheEntry.file.path,
                        request.newDecodeOptionsByQualityParams(imageInfo.mimeType)
                    )
                    if (bitmap.width > 1 && bitmap.height > 1) {
                        LoadResult(bitmap, imageInfo, DISK_CACHE)
                    } else {
                        bitmap.recycle()
                        SLog.emf(
                            MODULE,
                            "Invalid image size in result cache. size=%dx%d, uri=%s, diskCacheKey=%s",
                            bitmap.width, bitmap.height, request.uri, encodedBitmapDataDiskCacheKey
                        )
                        bitmapDataDiskCacheEntry.delete()
                        metaDataDiskCacheEntry.delete()
                        null
                    }
                } else {
                    bitmapDataDiskCacheEntry?.delete()
                    metaDataDiskCacheEntry?.delete()
                    null
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                bitmapDataDiskCacheEntry?.delete()
                metaDataDiskCacheEntry?.delete()
                throw e
            }
        }

        fun writeLoadResult(result: LoadResult) {
            if (!request.resultDiskCachePolicy.writeEnabled) {
                return
            }

            val bitmapDataEditor = diskCache.edit(encodedBitmapDataDiskCacheKey) ?: return
            try {
                bitmapDataEditor.newOutputStream().use {
                    result.bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                bitmapDataEditor.commit()
            } catch (e: Exception) {
                e.printStackTrace()
                bitmapDataEditor.abort()
                return
            }

            val metaDataEditor = diskCache.edit(encodedMetaDataDiskCacheKey) ?: return
            try {
                metaDataEditor.newOutputStream().use {
                    it.bufferedWriter().write(result.info.toJsonString())
                }
                metaDataEditor.commit()
            } catch (e: Exception) {
                e.printStackTrace()
                metaDataEditor.abort()
                diskCache[encodedBitmapDataDiskCacheKey]?.delete()
                return
            }
        }

        companion object {

            @JvmStatic
            fun from(request: LoadRequest, diskCache: DiskCache): ResultCacheHelper? {
                if (request.resultDiskCachePolicy.isReadOrWrite) return null
                val bitmapDataDiskCacheKey = request.resultDiskCacheKey ?: return null
                val metaDataDiskCacheKey = "${bitmapDataDiskCacheKey}_metadata"
                val encodedBitmapDataDiskCacheKey = diskCache.encodeKey(bitmapDataDiskCacheKey)
                val encodedMetaDataDiskCacheKey = diskCache.encodeKey(metaDataDiskCacheKey)
                return ResultCacheHelper(
                    request,
                    diskCache,
                    encodedBitmapDataDiskCacheKey,
                    encodedMetaDataDiskCacheKey
                )
            }
        }
    }
}