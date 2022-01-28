package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.DataFrom.RESULT_DISK_CACHE
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.newDecodeConfigByQualityParams
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

class BitmapResultCacheInterceptor : DecodeInterceptor<LoadRequest, BitmapDecodeResult> {

    companion object {
        const val MODULE = "BitmapResultCacheInterceptor"
    }

    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult>,
    ): BitmapDecodeResult {
        val request = chain.request
        val sketch = chain.sketch
        val resultCacheHelper = ResultCacheHelper.from(sketch, request)
        resultCacheHelper?.lock?.lock()
        try {
            return withContext(sketch.decodeTaskDispatcher) {
                resultCacheHelper?.read()
            } ?: chain.proceed(request).apply {
                withContext(sketch.decodeTaskDispatcher) {
                    resultCacheHelper?.write(this@apply)
                }
            }
        } finally {
            resultCacheHelper?.lock?.unlock()
        }
    }

    private class ResultCacheHelper(
        private val request: LoadRequest,
        private val diskCache: DiskCache,
        private val cachePolicy: CachePolicy,
        private val logger: Logger,
        val encodedBitmapDataDiskCacheKey: String,
        val encodedMetaDataDiskCacheKey: String,
    ) {

        val lock: Mutex by lazy {
            diskCache.getOrCreateEditMutexLock(encodedBitmapDataDiskCacheKey)
        }

        @WorkerThread
        fun read(): BitmapDecodeResult? =
            if (cachePolicy.readEnabled) {
                val bitmapDataDiskCacheEntry = diskCache[encodedBitmapDataDiskCacheKey]
                val metaDataDiskCacheEntry = diskCache[encodedMetaDataDiskCacheKey]
                try {
                    if (bitmapDataDiskCacheEntry != null && metaDataDiskCacheEntry != null) {
                        val jsonString = metaDataDiskCacheEntry.newInputStream().use {
                            it.bufferedReader().readText()
                        }
                        val imageInfo = ImageInfo.fromJsonString(jsonString)
                        val bitmap = BitmapFactory.decodeFile(
                            bitmapDataDiskCacheEntry.file.path,
                            request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                                .toBitmapOptions()
                        )
                        if (bitmap.width > 1 && bitmap.height > 1) {
                            BitmapDecodeResult(bitmap, imageInfo, RESULT_DISK_CACHE)
                        } else {
                            bitmap.recycle()
                            val msg =
                                "Invalid image size in result cache: ${bitmap.width}x${bitmap.height}"
                            logger.e(MODULE, msg)
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
                    null
                }
            } else {
                null
            }

        fun write(result: BitmapDecodeResult) {
            if (cachePolicy.writeEnabled && result.transformedList?.any { it.cacheResultToDisk } == true) {
                val bitmapDataEditor = diskCache.edit(encodedBitmapDataDiskCacheKey)
                val metaDataEditor = diskCache.edit(encodedMetaDataDiskCacheKey)
                try {
                    if (bitmapDataEditor != null && metaDataEditor != null) {
                        bitmapDataEditor.newOutputStream().use {
                            result.bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                        }
                        bitmapDataEditor.commit()

                        metaDataEditor.newOutputStream().use {
                            it.bufferedWriter().write(result.imageInfo.toJsonString())
                        }
                        metaDataEditor.commit()
                    } else {
                        bitmapDataEditor?.abort()
                        metaDataEditor?.abort()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    bitmapDataEditor?.abort()
                    metaDataEditor?.abort()
                    diskCache[encodedBitmapDataDiskCacheKey]?.delete()
                    diskCache[encodedMetaDataDiskCacheKey]?.delete()
                }
            }
        }

        companion object {

            @JvmStatic
            fun from(sketch: Sketch, request: LoadRequest): ResultCacheHelper? {
                val cachePolicy = request.bitmapResultDiskCachePolicy ?: ENABLED
                if (!cachePolicy.isReadOrWrite) return null
                val bitmapDataDiskCacheKey = request.cacheKey
                val diskCache: DiskCache = sketch.diskCache
                val metaDataDiskCacheKey = "${bitmapDataDiskCacheKey}_metadata"
                val encodedBitmapDataDiskCacheKey = diskCache.encodeKey(bitmapDataDiskCacheKey)
                val encodedMetaDataDiskCacheKey = diskCache.encodeKey(metaDataDiskCacheKey)
                return ResultCacheHelper(
                    request,
                    diskCache,
                    cachePolicy,
                    sketch.logger,
                    encodedBitmapDataDiskCacheKey,
                    encodedMetaDataDiskCacheKey
                )
            }
        }
    }
}