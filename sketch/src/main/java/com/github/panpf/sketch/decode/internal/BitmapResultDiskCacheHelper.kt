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
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.datasource.DataFrom.RESULT_DISK_CACHE
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.newDecodeConfigByQualityParams
import com.github.panpf.sketch.util.requiredWorkThread
import kotlinx.coroutines.sync.Mutex
import org.json.JSONArray
import org.json.JSONObject

suspend fun <R> tryLockBitmapResultDiskCache(
    sketch: Sketch,
    request: LoadRequest,
    block: suspend (helper: BitmapResultDiskCacheHelper?) -> R
): R {
    val helper = newBitmapResultDiskCacheHelper(sketch, request)
    return if (helper != null) {
        val lockKey = "${request.cacheKey}_result"
        val lock: Mutex = sketch.diskCache.editLock(lockKey)
        lock.lock()
        try {
            block(helper)
        } finally {
            lock.unlock()
        }
    } else {
        block(helper)
    }
}

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

    @WorkerThread
    fun read(): BitmapDecodeResult? {
        requiredWorkThread()
        return if (cachePolicy.readEnabled) {
            val bitmapDataDiskCacheSnapshot = diskCache[bitmapDataDiskCacheKey]
            val metaDataDiskCacheSnapshot = diskCache[metaDataDiskCacheKey]
            try {
                if (bitmapDataDiskCacheSnapshot != null && metaDataDiskCacheSnapshot != null) {
                    val jsonString = metaDataDiskCacheSnapshot.newInputStream().use {
                        it.bufferedReader().readText()
                    }
                    val metaData = MetaData(JSONObject(jsonString))
                    val imageInfo = metaData.imageInfo
                    val bitmap = BitmapFactory.decodeFile(
                        bitmapDataDiskCacheSnapshot.file.path,
                        request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                            .toBitmapOptions()
                    )
                    BitmapDecodeResult(
                        bitmap,
                        metaData.imageInfo,
                        metaData.exifOrientation,
                        RESULT_DISK_CACHE,
                        metaData.transformedList
                    )
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
    }

    @WorkerThread
    fun write(result: BitmapDecodeResult): Boolean {
        requiredWorkThread()
        return if (cachePolicy.writeEnabled && result.transformedList?.any { it.cacheResultToDisk } == true) {
            val bitmapDataEditor = diskCache.edit(bitmapDataDiskCacheKey)
            val metaDataEditor = diskCache.edit(metaDataDiskCacheKey)
            try {
                if (bitmapDataEditor != null && metaDataEditor != null) {
                    bitmapDataEditor.newOutputStream().use {
                        result.bitmap.compress(PNG, 100, it)
                    }
                    bitmapDataEditor.commit()

                    val metaData =
                        MetaData(result.imageInfo, result.exifOrientation, result.transformedList)
                    metaDataEditor.newOutputStream().bufferedWriter().use {
                        it.write(metaData.serializationToJSON().toString())
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
    }

    data class MetaData constructor(
        val imageInfo: ImageInfo,
        val exifOrientation: Int,
        val transformedList: List<Transformed>?,
    ) {

        constructor(jsonObject: JSONObject) : this(
            imageInfo = ImageInfo(
                width = jsonObject.getInt("width"),
                height = jsonObject.getInt("height"),
                mimeType = jsonObject.getString("mimeType")
            ),
            exifOrientation = jsonObject.getInt("exifOrientation"),
            transformedList = jsonObject.optJSONArray("transformedList")
                ?.takeIf { it.length() > 0 }?.run {
                    0.until(length()).map { index ->
                        val item = getJSONObject(index)
                        Class.forName(item.getString("transformedClassName"))
                            .getConstructor(JSONObject::class.java)
                            .newInstance(item.getJSONObject("transformedContent")) as Transformed
                    }
                },
        )

        fun serializationToJSON(): JSONObject = JSONObject().apply {
            put("width", imageInfo.width)
            put("height", imageInfo.height)
            put("mimeType", imageInfo.mimeType)
            put("exifOrientation", exifOrientation)
            put("transformedList", transformedList?.takeIf { it.isNotEmpty() }?.let { list ->
                JSONArray().also { array ->
                    list.forEach { transformed ->
                        array.put(
                            JSONObject().let {
                                it.put("transformedClassName", transformed::class.java.name)
                                it.put("transformedContent", transformed.serializationToJSON())
                            }
                        )
                    }
                }
            })
        }
    }
}