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
package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap.CompressFormat.PNG
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.datasource.DataFrom.RESULT_CACHE
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.sync.Mutex
import org.json.JSONArray
import org.json.JSONObject

class BitmapResultCacheDecodeInterceptor : BitmapDecodeInterceptor {

    companion object {
        const val MODULE = "BitmapResultCacheDecodeInterceptor"
    }

    @WorkerThread
    override suspend fun intercept(chain: BitmapDecodeInterceptor.Chain): BitmapDecodeResult {
        val sketch = chain.sketch
        val request = chain.request
        val resultCache = sketch.resultCache

        if (request.resultCachePolicy.readEnabled) {
            val cachedResult = resultCache.lockResultCache(chain.request) {
                read(sketch, request)
            }
            if (cachedResult != null) {
                return cachedResult
            }
        }

        return if (request.resultCachePolicy.writeEnabled) {
            resultCache.lockResultCache(chain.request) {
                chain.proceed().apply {
                    write(sketch, request, this)
                }
            }
        } else {
            chain.proceed()
        }
    }

    private suspend fun <R> DiskCache.lockResultCache(
        request: ImageRequest,
        block: suspend () -> R
    ): R {
        val lock: Mutex = editLock(request.resultCacheLockKey)
        lock.lock()
        try {
            return block()
        } finally {
            lock.unlock()
        }
    }

    @WorkerThread
    private fun read(sketch: Sketch, request: ImageRequest): BitmapDecodeResult? {
        val resultCache = sketch.resultCache
        val bitmapDataDiskCacheSnapshot = resultCache[request.resultCacheDataKey]
        val bitmapMetaDiskCacheSnapshot = resultCache[request.resultCacheMetaKey]
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
                mimeType = metaDataJSONObject.getString("mimeType"),
                exifOrientation = metaDataJSONObject.getInt("exifOrientation"),
            )
            val transformedList =
                metaDataJSONObject.optJSONArray("transformedList")?.let { jsonArray ->
                    (0 until jsonArray.length()).map { index ->
                        jsonArray[index].toString()
                    }
                }

            val dataSource =
                DiskCacheDataSource(sketch, request, RESULT_CACHE, bitmapDataDiskCacheSnapshot)
            val cacheImageInfo = dataSource.readImageInfoWithBitmapFactory(true)
            val options = request.newDecodeConfigByQualityParams(cacheImageInfo.mimeType)
                .toBitmapOptions()
            if (!request.disallowReuseBitmap) {
                setInBitmap(
                    bitmapPool = sketch.bitmapPool,
                    logger = sketch.logger,
                    options = options,
                    imageSize = Size(cacheImageInfo.width, cacheImageInfo.height),
                    imageMimeType = ImageFormat.PNG.mimeType
                )
            }
            try {
                dataSource.decodeBitmap(options)
            } catch (throwable: IllegalArgumentException) {
                val inBitmap = options.inBitmap
                if (inBitmap != null && isInBitmapError(throwable)) {
                    val message = "Bitmap decode error. Because inBitmap. uri=${request.uriString}"
                    sketch.logger.e(MODULE, throwable, message)

                    options.inBitmap = null
                    freeBitmap(sketch.bitmapPool, sketch.logger, inBitmap, "decode:error")
                    try {
                        dataSource.decodeBitmap(options)
                    } catch (throwable2: Throwable) {
                        throw BitmapDecodeException("Bitmap decode error2: $throwable", throwable2)
                    }
                } else {
                    throw BitmapDecodeException("Bitmap decode error: $throwable", throwable)
                }
            }?.let { bitmap ->
                BitmapDecodeResult(bitmap, imageInfo, RESULT_CACHE, transformedList)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            bitmapDataDiskCacheSnapshot.remove()
            bitmapMetaDiskCacheSnapshot.remove()
            null
        }
    }

    @WorkerThread
    private fun write(sketch: Sketch, request: ImageRequest, result: BitmapDecodeResult): Boolean {
        val resultCache = sketch.resultCache
        val bitmapDataEditor = resultCache.edit(request.resultCacheDataKey)
        val metaDataEditor = resultCache.edit(request.resultCacheMetaKey)
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
                    put("exifOrientation", result.imageInfo.exifOrientation)
                    put("transformedList", result.transformedList?.let { list ->
                        JSONArray().apply {
                            list.forEach { transformed ->
                                put(transformed)
                            }
                        }
                    })
                }
                it.write(metaJSONObject.toString())
            }
            metaDataEditor.commit()
            true
        } catch (e: Throwable) {
            e.printStackTrace()
            bitmapDataEditor.abort()
            metaDataEditor.abort()
            resultCache.remove(request.resultCacheDataKey)
            resultCache.remove(request.resultCacheMetaKey)
            false
        }
    }

    override fun toString(): String = "BitmapResultCacheDecodeInterceptor"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

val ImageRequest.resultCacheDataKey: String
    get() = "${cacheKey}_result_data"

val ImageRequest.resultCacheMetaKey: String
    get() = "${cacheKey}_result_meta"

val ImageRequest.resultCacheLockKey: String
    get() = "${cacheKey}_result"