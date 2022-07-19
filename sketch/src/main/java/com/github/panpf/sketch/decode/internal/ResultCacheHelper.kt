package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap.CompressFormat.PNG
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.datasource.DataFrom.RESULT_DISK_CACHE
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.requiredWorkThread
import kotlinx.coroutines.sync.Mutex
import org.json.JSONObject

suspend fun <R> safeAccessResultCache(
    sketch: Sketch,
    request: ImageRequest,
    block: suspend (helper: ResultCacheHelper?) -> R
): R =
    if (request.resultCachePolicy.isReadOrWrite) {
        val helper = ResultCacheHelper(sketch, request)
        val lock: Mutex = sketch.resultDiskCache.editLock(helper.keys.lockKey)
        lock.lock()
        try {
            block(helper)
        } finally {
            lock.unlock()
        }
    } else {
        block(null)
    }

class ResultCacheKeys(request: ImageRequest) {
    val bitmapDataDiskCacheKey: String by lazy {
        "${request.cacheKey}_result_data"
    }
    val bitmapMetaDiskCacheKey: String by lazy {
        "${request.cacheKey}_result_meta"
    }
    val lockKey: String by lazy {
        "${request.cacheKey}_result"
    }
}

class ResultCacheHelper(val sketch: Sketch, val request: ImageRequest) {

    companion object {
        const val MODULE = "BitmapResultDiskCacheHelper"
    }

    private val diskCache: DiskCache = sketch.resultDiskCache
    val keys = ResultCacheKeys(request)

    @WorkerThread
    fun read(): BitmapDecodeResult? {
        requiredWorkThread()
        if (!request.resultCachePolicy.readEnabled) return null

        val bitmapDataDiskCacheSnapshot = diskCache[keys.bitmapDataDiskCacheKey]
        val bitmapMetaDiskCacheSnapshot = diskCache[keys.bitmapMetaDiskCacheKey]
        if (bitmapDataDiskCacheSnapshot == null || bitmapMetaDiskCacheSnapshot == null) {
            kotlin.runCatching { bitmapDataDiskCacheSnapshot?.remove() }
            kotlin.runCatching { bitmapMetaDiskCacheSnapshot?.remove() }
            return null
        }

        return try {
            val metaDataJSONObject = bitmapMetaDiskCacheSnapshot.newInputStream()
                .use { it.bufferedReader().readText() }
                .let { JSONObject(it) }
            val imageInfo = ImageInfo(
                width = metaDataJSONObject.getInt("width"),
                height = metaDataJSONObject.getInt("height"),
                mimeType = metaDataJSONObject.getString("mimeType")
            )
            val exifOrientation = metaDataJSONObject.getInt("exifOrientation")
            val transformedList =
                metaDataJSONObject.optJSONArray("transformedList")?.let { jsonArray ->
                    (0 until jsonArray.length()).map { index ->
                        jsonArray[index].toString()
                    }
                }

            val dataSource =
                DiskCacheDataSource(sketch, request, RESULT_DISK_CACHE, bitmapDataDiskCacheSnapshot)
            val cacheImageInfo = dataSource.readImageInfoWithBitmapFactory()
            val options = request.newDecodeConfigByQualityParams(cacheImageInfo.mimeType)
                .toBitmapOptions()
            if (!request.disallowReuseBitmap) {
                sketch.bitmapPool.setInBitmap(
                    options, cacheImageInfo.width, cacheImageInfo.height, cacheImageInfo.mimeType
                )
            }
            dataSource.decodeBitmap(options)?.let { bitmap ->
                BitmapDecodeResult(
                    bitmap = bitmap,
                    imageInfo = imageInfo,
                    imageExifOrientation = exifOrientation,
                    dataFrom = RESULT_DISK_CACHE,
                    transformedList = transformedList
                )
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            bitmapDataDiskCacheSnapshot.remove()
            bitmapMetaDiskCacheSnapshot.remove()
            null
        }
    }

    @WorkerThread
    fun write(result: BitmapDecodeResult): Boolean {
        requiredWorkThread()
        if (!request.resultCachePolicy.writeEnabled) {
            return false
        }

        val bitmapDataEditor = diskCache.edit(keys.bitmapDataDiskCacheKey)
        val metaDataEditor = diskCache.edit(keys.bitmapMetaDiskCacheKey)
        if (bitmapDataEditor == null || metaDataEditor == null) {
            kotlin.runCatching { bitmapDataEditor?.abort() }
            kotlin.runCatching { metaDataEditor?.abort() }
            return false
        }
        return try {
            bitmapDataEditor.newOutputStream().buffered().use {
                result.bitmap.compress(PNG, 100, it)
            }
            bitmapDataEditor.commit()

            metaDataEditor.newOutputStream().bufferedWriter().use {
                val metaJSONObject = JSONObject().apply {
                    put("width", result.imageInfo.width)
                    put("height", result.imageInfo.height)
                    put("mimeType", result.imageInfo.mimeType)
                    put("exifOrientation", result.imageExifOrientation)
                    put("transformedList", result.transformedList)
                }
                it.write(metaJSONObject.toString())
            }
            metaDataEditor.commit()
            true
        } catch (e: Throwable) {
            e.printStackTrace()
            bitmapDataEditor.abort()
            metaDataEditor.abort()
            diskCache.remove(keys.bitmapDataDiskCacheKey)
            diskCache.remove(keys.bitmapMetaDiskCacheKey)
            false
        }
    }
}