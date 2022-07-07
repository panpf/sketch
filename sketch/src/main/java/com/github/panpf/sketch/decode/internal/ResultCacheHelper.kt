package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap.CompressFormat.PNG
import android.graphics.BitmapFactory
import androidx.annotation.Keep
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.datasource.DataFrom.RESULT_DISK_CACHE
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.JsonSerializable
import com.github.panpf.sketch.util.JsonSerializer
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.requiredWorkThread
import kotlinx.coroutines.sync.Mutex
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.FileInputStream

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

class ResultCacheHelper(sketch: Sketch, val request: ImageRequest) {

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
            val metaData = bitmapMetaDiskCacheSnapshot.newInputStream()
                .use { inputStream -> inputStream.bufferedReader().readText() }
                .let { jsonString -> MetaData.Serializer().fromJson(JSONObject(jsonString)) }
            val imageInfo = metaData.imageInfo
            FileInputStream(bitmapDataDiskCacheSnapshot.file.path).use {
                BufferedInputStream(it).use { bufferedInput ->
                    val options = request.newDecodeConfigByQualityParams(imageInfo.mimeType)
                        .toBitmapOptions()
                    BitmapFactory.decodeStream(bufferedInput, null, options)
                }
            }?.let { bitmap ->
                BitmapDecodeResult(
                    bitmap = bitmap,
                    imageInfo = metaData.imageInfo,
                    imageExifOrientation = metaData.exifOrientation,
                    dataFrom = RESULT_DISK_CACHE,
                    transformedList = metaData.transformedList
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
        if (!request.resultCachePolicy.writeEnabled
            || result.transformedList?.any { it.cacheResultToDisk } != true
        ) {
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
            bitmapDataEditor.newOutputStream().use {
                result.bitmap.compress(PNG, 100, it)
            }
            bitmapDataEditor.commit()

            val metaData =
                MetaData(
                    result.imageInfo,
                    result.imageExifOrientation,
                    result.transformedList
                )
            metaDataEditor.newOutputStream().bufferedWriter().use {
                val metaDataSerializer = MetaData.Serializer()
                val metaDataJsonString = metaDataSerializer.toJson(metaData).toString()
                it.write(metaDataJsonString)
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
                    put("width", t.imageInfo.width)
                    put("height", t.imageInfo.height)
                    put("mimeType", t.imageInfo.mimeType)
                    put("exifOrientation", t.exifOrientation)
                    put(
                        "transformedList",
                        t.transformedList?.takeIf { it.isNotEmpty() }?.let { list ->
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
                        }
                    )
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
                                item.getString("transformedSerializerClassName")
                                    .let { Class.forName(it) }
                                    .newInstance().asOrThrow<JsonSerializer<*>>()
                                    .fromJson(item.getJSONObject("transformedContent"))!!
                                    .asOrThrow()
                            }
                        },
                )
        }
    }
}