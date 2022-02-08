package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap.CompressFormat.PNG
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
import kotlinx.coroutines.sync.Mutex
import org.json.JSONException
import org.json.JSONObject

// todo 改成 editor

fun newBitmapResultDiskCacheHelper(
    sketch: Sketch,
    request: LoadRequest
): BitmapResultDiskCacheHelper? {
    val cachePolicy = request.bitmapResultDiskCachePolicy ?: ENABLED
    if (!cachePolicy.isReadOrWrite) return null
    val bitmapDataDiskCacheKey = "${request.cacheKey}_result_data"
    val metaDataDiskCacheKey = "${request.cacheKey}_result_meta"
    return BitmapResultDiskCacheHelper(
        request,
        sketch.diskCache,
        cachePolicy,
        bitmapDataDiskCacheKey,
        metaDataDiskCacheKey
    )
}

class BitmapResultDiskCacheHelper internal constructor(
    private val request: LoadRequest,
    private val diskCache: DiskCache,
    private val cachePolicy: CachePolicy,
    private val bitmapDataDiskCacheKey: String,
    private val metaDataDiskCacheKey: String,
) {

    companion object {
        const val MODULE = "BitmapResultDiskCacheHelper"
    }

    val lock: Mutex by lazy {
        diskCache.editLock(bitmapDataDiskCacheKey)
    }

    @WorkerThread
    fun read(): BitmapDecodeResult? =
        if (cachePolicy.readEnabled) {
            val bitmapDataDiskCacheSnapshot = diskCache[bitmapDataDiskCacheKey]
            val metaDataDiskCacheSnapshot = diskCache[metaDataDiskCacheKey]
            try {
                if (bitmapDataDiskCacheSnapshot != null && metaDataDiskCacheSnapshot != null) {
                    val jsonString = metaDataDiskCacheSnapshot.newInputStream().use {
                        it.bufferedReader().readText()
                    }
                    val metaData = MetaData.fromJsonString(jsonString)
                    val imageInfo = metaData.imageInfo
                    val bitmap = BitmapFactory.decodeFile(
                        bitmapDataDiskCacheSnapshot.file.path,
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .toBitmapOptions()
                    )
                    BitmapDecodeResult(bitmap, metaData.imageInfo, metaData.exifOrientation, RESULT_DISK_CACHE)
                } else {
                    bitmapDataDiskCacheSnapshot?.remove()
                    metaDataDiskCacheSnapshot?.remove()
                    null
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                bitmapDataDiskCacheSnapshot?.remove()
                metaDataDiskCacheSnapshot?.remove()
                null
            }
        } else {
            null
        }

    fun write(result: BitmapDecodeResult): Boolean =
        if (cachePolicy.writeEnabled && result.transformedList?.any { it.cacheResultToDisk } == true) {
            val bitmapDataEditor = diskCache.edit(bitmapDataDiskCacheKey)
            val metaDataEditor = diskCache.edit(metaDataDiskCacheKey)
            try {
                if (bitmapDataEditor != null && metaDataEditor != null) {
                    bitmapDataEditor.newOutputStream().use {
                        result.bitmap.compress(PNG, 100, it)
                    }
                    bitmapDataEditor.commit()

                    val metaData = MetaData(result.imageInfo, result.exifOrientation)
                    metaDataEditor.newOutputStream().bufferedWriter().use {
                        it.write(metaData.toJsonString())
                    }
                    metaDataEditor.commit()
                } else {
                    bitmapDataEditor?.abort()
                    metaDataEditor?.abort()
                }
                true
            } catch (e: Throwable) {
                e.printStackTrace()
                bitmapDataEditor?.abort()
                metaDataEditor?.abort()
                diskCache.remove(bitmapDataDiskCacheKey)
                diskCache.remove(metaDataDiskCacheKey)
                false
            }
        } else {
            false
        }

    data class MetaData constructor(
        val imageInfo: ImageInfo,
        val exifOrientation: Int,
    ) {

        // todo transformedList 也要缓存
        fun toJsonString(): String = JSONObject().apply {
            put("width", imageInfo.width)
            put("height", imageInfo.height)
            put("mimeType", imageInfo.mimeType)
            put("exifOrientation", exifOrientation)
        }.toString()

        companion object {
            @Throws(JSONException::class)
            fun fromJsonString(jsonString: String): MetaData {
                val json = JSONObject(jsonString)
                return MetaData(
                    imageInfo = ImageInfo(
                        width = json.getInt("width"),
                        height = json.getInt("height"),
                        mimeType = json.getString("mimeType")
                    ),
                    exifOrientation = json.getInt("exifOrientation"),
                )
            }
        }
    }
}