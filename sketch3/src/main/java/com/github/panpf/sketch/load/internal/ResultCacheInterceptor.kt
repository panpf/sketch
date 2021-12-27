package com.github.panpf.sketch.load.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.DataFrom
import com.github.panpf.sketch.common.Interceptor
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.common.cache.disk.DiskCache
import com.github.panpf.sketch.load.ImageInfo
import com.github.panpf.sketch.load.LoadRequest
import com.github.panpf.sketch.load.LoadResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

class ResultCacheInterceptor : Interceptor<LoadRequest, LoadResult> {

    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<LoadRequest, LoadResult>,
        extras: RequestExtras<LoadRequest, LoadResult>?
    ): LoadResult {
        val diskCache = sketch.diskCache
        val request = chain.request
        val resultCacheHelper = ResultCacheHelper.from(request, diskCache)
        val mutex = resultCacheHelper?.getOrCreateEditMutexLock()
        mutex?.lock()
        try {
            return withContext(sketch.decodeTaskDispatcher) {
                resultCacheHelper?.readLoadResult()
            } ?: chain.proceed(sketch, request, extras).apply {
                withContext(sketch.decodeTaskDispatcher) {
                    resultCacheHelper?.writeLoadResult(this@apply)
                }
            }
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
                        buildOptionsByRequest(imageInfo)
                    )
                    LoadResult(bitmap, imageInfo, DataFrom.DISK_CACHE)
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

        private fun buildOptionsByRequest(imageInfo: ImageInfo): BitmapFactory.Options? {
            val bitmapConfig = request.bitmapConfig
            val inPreferQualityOverSpeedParam = request.inPreferQualityOverSpeed
            return if (bitmapConfig != null || inPreferQualityOverSpeedParam == true) {
                BitmapFactory.Options().apply {
                    /*
                     * 'bitmapConfig' and 'inPreferQualityOverSpeed' will affect the quality of the image,
                     * so when you read the cached image, you must use a consistent quality configuration to read,
                     * so as to ensure the same effect as possible
                     */
                    if (bitmapConfig != null) {
                        inPreferredConfig = bitmapConfig.getConfigByMimeType(imageInfo.mimeType)
                    }
                    if (inPreferQualityOverSpeedParam == true) {
                        inPreferQualityOverSpeed = true
                    }
                }
            } else {
                null
            }
        }

        companion object {
            @JvmStatic
            fun from(request: LoadRequest, diskCache: DiskCache): ResultCacheHelper? {
                val resultCacheKey = request.resultCacheKey ?: return null
                val bitmapDataDiskCacheKey = request.uri.toString() + "_" + resultCacheKey
                val metaDataDiskCacheKey = bitmapDataDiskCacheKey + "_metadata"
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