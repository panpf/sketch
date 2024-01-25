/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("FoldInitializerAndIfToElvis")

package com.github.panpf.sketch.cache.internal

import android.graphics.Bitmap.CompressFormat
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.datasource.DataFrom.RESULT_CACHE
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.decodeBitmap
import com.github.panpf.sketch.decode.internal.newDecodeConfigByQualityParams
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactory
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.closeQuietly
import com.github.panpf.sketch.util.ifOrNull
import kotlinx.coroutines.sync.Mutex
import okio.buffer
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.Map.Entry

class ResultCacheDecodeInterceptor : DecodeInterceptor {

    companion object {
        const val MODULE = "ResultCacheDecodeInterceptor"
    }

    override val key: String? = null

    override val sortWeight: Int = 80

    @WorkerThread
    override suspend fun intercept(chain: DecodeInterceptor.Chain): Result<DecodeResult> {
        val sketch = chain.sketch
        val requestContext = chain.requestContext
        val resultCache = sketch.resultCache
        val resultCachePolicy = requestContext.request.resultCachePolicy

        return if (resultCachePolicy.isReadOrWrite) {
            resultCache.lockResultCache(requestContext) {
                ifOrNull(resultCachePolicy.readEnabled) {
                    readCache(sketch, requestContext)
                        ?.let { Result.success(it) }
                } ?: chain.proceed().apply {
                    val result = this.getOrNull()
                    if (result != null && resultCachePolicy.writeEnabled) {
                        writeCache(sketch, requestContext, decodeResult = result)
                    }
                }
            }
        } else {
            chain.proceed()
        }
    }

    private suspend fun <R> DiskCache.lockResultCache(
        requestContext: RequestContext,
        block: suspend () -> R
    ): R {
        val lock: Mutex = editLock(requestContext.cacheKey)
        lock.lock()
        try {
            return block()
        } finally {
            lock.unlock()
        }
    }

    @WorkerThread
    private fun readCache(
        sketch: Sketch,
        requestContext: RequestContext,
    ): DecodeResult? {
        val resultCache = sketch.resultCache
        val fileSystem = resultCache.fileSystem
        val cacheKey = requestContext.cacheKey
        val snapshot = runCatching { resultCache.openSnapshot(cacheKey) }.getOrNull()
        if (snapshot == null) return null
        val result = runCatching {
            val dataSource = DiskCacheDataSource(
                sketch = sketch,
                request = requestContext.request,
                dataFrom = RESULT_CACHE,
                fileSystem = fileSystem,
                path = snapshot.data
            )
            val cacheImageInfo = dataSource.readImageInfoWithBitmapFactory(true)
            val decodeOptions = requestContext.request
                .newDecodeConfigByQualityParams(cacheImageInfo.mimeType)
                .toBitmapOptions()
            val bitmap = dataSource.decodeBitmap(decodeOptions)
            if (bitmap == null) {
                throw DecodeException("Decode bitmap return null. '${requestContext.logKey}'")
            }

            val metadataJSONObject = fileSystem
                .source(snapshot.metadata).buffer()
                .readUtf8()
                .let { JSONObject(it) }
            val imageInfo = ImageInfo(
                width = metadataJSONObject.getInt("width"),
                height = metadataJSONObject.getInt("height"),
                mimeType = metadataJSONObject.getString("mimeType"),
                exifOrientation = metadataJSONObject.getInt("exifOrientation"),
            )
            val transformedList =
                metadataJSONObject.optJSONArray("transformedList")?.let { jsonArray ->
                    (0 until jsonArray.length()).map { index ->
                        jsonArray[index].toString()
                    }
                }
            val extras = metadataJSONObject.optJSONObject("extras")?.let {
                val extras = mutableMapOf<String, String>()
                it.keys().forEach { key ->
                    extras[key] = it.getString(key)
                }
                extras.toMap()
            }
            DecodeResult(
                image = bitmap.asSketchImage(requestContext.request.context.resources),
                imageInfo = imageInfo,
                dataFrom = RESULT_CACHE,
                transformedList = transformedList,
                extras = extras
            )
        }
        snapshot.closeQuietly()

        result.onFailure {
            it.printStackTrace()
            sketch.logger.w(MODULE) {
                "read result cache error. $it. '${requestContext.logKey}'"
            }
            resultCache.remove(cacheKey)
        }
        return result.getOrNull()
    }

    @WorkerThread
    private fun writeCache(
        sketch: Sketch,
        requestContext: RequestContext,
        decodeResult: DecodeResult,
    ): Boolean {
        val transformedList = decodeResult.transformedList
        if (transformedList.isNullOrEmpty()) return false
        val image = decodeResult.image
        if (image !is BitmapImage) return false

        val resultCache = sketch.resultCache
        val cacheKey = requestContext.cacheKey
        val editor = resultCache.openEditor(cacheKey)
        if (editor == null) return false

        val result = runCatching {
            resultCache.fileSystem.sink(editor.data).buffer().outputStream().use {
                image.bitmap.compress(CompressFormat.PNG, 100, it)
            }

            val metadataJsonString = JSONObject().apply {
                put("width", decodeResult.imageInfo.width)
                put("height", decodeResult.imageInfo.height)
                put("mimeType", decodeResult.imageInfo.mimeType)
                put("exifOrientation", decodeResult.imageInfo.exifOrientation)
                put("transformedList", transformedList.let { list ->
                    JSONArray().apply {
                        list.forEach { transformed ->
                            put(transformed)
                        }
                    }
                })
                decodeResult.extras?.entries?.takeIf { it.isNotEmpty() }
                    ?.let<Set<Entry<String, String>>, Unit> { entries ->
                        put("extras", JSONObject().apply {
                            entries.forEach { entry ->
                                put(entry.key, entry.value)
                            }
                        })
                    }
            }.toString()
            resultCache.fileSystem.sink(editor.metadata).buffer().use { writer ->
                writer.writeUtf8(metadataJsonString)
            }
        }
        return if (result.isFailure) {
            editor.abort()
            false
        } else {
            editor.commit()
            true
        }
    }

    override fun toString(): String = "ResultCacheDecodeInterceptor(sortWeight=$sortWeight)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}