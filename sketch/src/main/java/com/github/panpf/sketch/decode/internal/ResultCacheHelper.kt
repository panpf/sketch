package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap.CompressFormat.PNG
import android.graphics.BitmapFactory
import androidx.annotation.Keep
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.datasource.DataFrom.RESULT_DISK_CACHE
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import com.github.panpf.sketch.util.requiredWorkThread
import kotlinx.coroutines.sync.Mutex
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.FileInputStream

suspend fun <R> tryLockResultCache(
    sketch: Sketch,
    request: ImageRequest,
    block: suspend (helper: ResultCacheHelper?) -> R
): R {
    val helper = newResultCacheHelper(sketch, request)
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

fun ImageRequest.newResultCacheDataKey(): String = "${cacheKey}_result_data"

fun ImageRequest.newResultCacheMetaKey(): String = "${cacheKey}_result_meta"

fun newResultCacheHelper(
    sketch: Sketch,
    request: ImageRequest
): ResultCacheHelper? {
    val cachePolicy = request.resultCachePolicy
    if (!cachePolicy.isReadOrWrite) return null
    val bitmapDataDiskCacheKey = request.newResultCacheDataKey()
    val bitmapMetaDiskCacheKey = request.newResultCacheMetaKey()
    return ResultCacheHelper(
        request,
        sketch.diskCache,
        cachePolicy,
        bitmapDataDiskCacheKey,
        bitmapMetaDiskCacheKey
    )
}

class ResultCacheHelper internal constructor(
    private val request: ImageRequest,
    private val diskCache: DiskCache,
    private val cachePolicy: CachePolicy,
    private val bitmapDataDiskCacheKey: String,
    private val bitmapMetaDiskCacheKey: String,
) {

    companion object {
        const val MODULE = "BitmapResultDiskCacheHelper"
    }

    @WorkerThread
    fun read(): BitmapDecodeResult? {
        requiredWorkThread()
        return if (cachePolicy.readEnabled) {
            val bitmapDataDiskCacheSnapshot = diskCache[bitmapDataDiskCacheKey]
            val bitmapMetaDiskCacheSnapshot = diskCache[bitmapMetaDiskCacheKey]
            try {
                if (bitmapDataDiskCacheSnapshot != null && bitmapMetaDiskCacheSnapshot != null) {
                    val jsonString = bitmapMetaDiskCacheSnapshot.newInputStream().use {
                        it.bufferedReader().readText()
                    }
                    val metaDataSerializer = MetaData.Serializer()
                    val metaData = metaDataSerializer.fromJson(JSONObject(jsonString))
                    val imageInfo = metaData.imageInfo
                    val bitmap = FileInputStream(bitmapDataDiskCacheSnapshot.file.path).use {
                        BufferedInputStream(it).use { bufferedInput ->
                            val options = request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                                .toBitmapOptions()
                            BitmapFactory.decodeStream(bufferedInput, null, options)
                        }
                    }
                    if (bitmap != null) {
                        BitmapDecodeResult(
                            bitmap,
                            metaData.imageInfo,
                            metaData.exifOrientation,
                            RESULT_DISK_CACHE,
                            metaData.transformedList
                        )
                    } else {
                        null
                    }
                } else {
                    bitmapDataDiskCacheSnapshot?.remove()
                    bitmapMetaDiskCacheSnapshot?.remove()
                    null
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                bitmapDataDiskCacheSnapshot?.remove()
                bitmapMetaDiskCacheSnapshot?.remove()
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
            val metaDataEditor = diskCache.edit(bitmapMetaDiskCacheKey)
            try {
                if (bitmapDataEditor != null && metaDataEditor != null) {
                    bitmapDataEditor.newOutputStream().use {
                        result.bitmap.compress(PNG, 100, it)
                    }
                    bitmapDataEditor.commit()

                    val metaData =
                        MetaData(result.imageInfo, result.imageExifOrientation, result.transformedList)
                    metaDataEditor.newOutputStream().bufferedWriter().use {
                        val metaDataSerializer = MetaData.Serializer()
                        val metaDataJsonString = metaDataSerializer.toJson(metaData).toString()
                        it.write(metaDataJsonString)
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
                diskCache.remove(bitmapMetaDiskCacheKey)
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
    ) : JsonSerializable {

        override fun <T : JsonSerializable, T1 : JsonSerializer<T>> getSerializerClass(): Class<T1> {
            @Suppress("UNCHECKED_CAST")
            return Serializer::class.java as Class<T1>
        }

        @Keep
        class Serializer : JsonSerializer<MetaData> {
            override fun toJson(t: MetaData): JSONObject =
                JSONObject().apply {
                    t.apply {
                        put("width", imageInfo.width)
                        put("height", imageInfo.height)
                        put("mimeType", imageInfo.mimeType)
                        put("exifOrientation", exifOrientation)
                        put(
                            "transformedList",
                            transformedList?.takeIf { it.isNotEmpty() }?.let { list ->
                                JSONArray().also { array ->
                                    list.forEach { transformed ->
                                        array.put(
                                            JSONObject().let {
                                                val transformedSerializerClass =
                                                    transformed.getSerializerClass<JsonSerializable, JsonSerializer<JsonSerializable>>()
                                                val transformedSerializer =
                                                    transformedSerializerClass.newInstance()
                                                it.put(
                                                    "transformedSerializerClassName",
                                                    transformedSerializerClass.name
                                                )
                                                it.put(
                                                    "transformedContent",
                                                    transformedSerializer.toJson(transformed)
                                                )
                                            }
                                        )
                                    }
                                }
                            })
                    }
                }

            override fun fromJson(jsonObject: JSONObject): MetaData =
                MetaData(
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
                                (Class.forName(item.getString("transformedSerializerClassName"))
                                    .newInstance() as JsonSerializer<*>)
                                    .fromJson(item.getJSONObject("transformedContent")) as Transformed
                            }
                        },
                )
        }
    }
}