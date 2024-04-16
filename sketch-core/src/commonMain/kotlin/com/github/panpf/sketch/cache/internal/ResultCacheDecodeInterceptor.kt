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

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.source.DataFrom.RESULT_CACHE
import com.github.panpf.sketch.source.DiskCacheDataSource
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.closeQuietly
import com.github.panpf.sketch.util.ifOrNull
import okio.buffer
import okio.use

class ResultCacheDecodeInterceptor : DecodeInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 80

    @WorkerThread
    override suspend fun intercept(chain: DecodeInterceptor.Chain): Result<DecodeResult> {
        val sketch = chain.sketch
        val requestContext = chain.requestContext
        val resultCache = sketch.resultCache
        val resultCachePolicy = requestContext.request.resultCachePolicy

        return if (resultCachePolicy.isReadOrWrite) {
            resultCache.withLock(requestContext.cacheKey) {
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

    @WorkerThread
    private fun readCache(
        sketch: Sketch,
        requestContext: RequestContext,
    ): DecodeResult? {
        val resultCache = sketch.resultCache
        val fileSystem = resultCache.fileSystem
        val cacheKey = requestContext.cacheKey
        val imageSerializer = createImageSerializer() ?: return null
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
            val metadataString = fileSystem.source(snapshot.metadata).buffer().use { it.readUtf8() }
            val metadata = Metadata.fromMetadataString(metadataString)
            val image = imageSerializer.decode(requestContext, metadata.imageInfo, dataSource)
            DecodeResult(
                image = image,
                imageInfo = metadata.imageInfo,
                dataFrom = RESULT_CACHE,
                transformedList = metadata.transformedList,
                extras = metadata.extras
            )
        }
        snapshot.closeQuietly()

        result.onFailure {
            it.printStackTrace()
            sketch.logger.w {
                "ResultCacheDecodeInterceptor. read result cache error. $it. '${requestContext.logKey}'"
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
        val imageSerializer =
            createImageSerializer()?.takeIf { it.supportImage(image) } ?: return false
        val resultCache = sketch.resultCache
        val cacheKey = requestContext.cacheKey
        val editor = resultCache.openEditor(cacheKey)
        if (editor == null) return false
        val result = runCatching {
            resultCache.fileSystem.sink(editor.data).buffer().use {
                imageSerializer.compress(image, it)
            }

            val metadataString = Metadata(
                imageInfo = decodeResult.imageInfo,
                transformedList = transformedList,
                extras = decodeResult.extras
            ).toMetadataString()
            resultCache.fileSystem.sink(editor.metadata).buffer().use { writer ->
                writer.writeUtf8(metadataString)
            }
        }
        return if (result.isFailure) {
            result.exceptionOrNull()?.printStackTrace()
            editor.abort()
            false
        } else {
            editor.commit()
            true
        }
    }

    @Suppress("RedundantOverride")
    override fun equals(other: Any?): Boolean {
        // If you add construction parameters to this class, you need to change it here
        return super.equals(other)
    }

    @Suppress("RedundantOverride")
    override fun hashCode(): Int {
        // If you add construction parameters to this class, you need to change it here
        return super.hashCode()
    }

    override fun toString(): String = "ResultCacheDecodeInterceptor(sortWeight=$sortWeight)"

    private class Metadata(
        val imageInfo: ImageInfo,
        val transformedList: List<String>?,
        val extras: Map<String, String>?
    ) {

        fun toMetadataString(): String = buildString {
            appendLine("width=${imageInfo.width}")
            appendLine("height=${imageInfo.height}")
            appendLine("mimeType=${imageInfo.mimeType}")
            transformedList?.forEach {
                appendLine("transformed=${it}")
            }
            extras?.entries?.forEach {
                appendLine("extras.${it.key}=${it.value}")
            }
        }

        companion object {

            fun fromMetadataString(metadataString: String): Metadata {
                val propertiesMap = metadataString
                    .split("\n")
                    .filter { line -> line.trim().isNotEmpty() }
                    .associate { line ->
                        val delimiterIndex = line.indexOf("=").takeIf { index -> index > 0 }
                            ?: throw IllegalArgumentException("Illegal result cache properties: $metadataString")
                        line.substring(0, delimiterIndex) to line.substring(delimiterIndex + 1)
                    }
                val imageInfo = ImageInfo(
                    width = propertiesMap["width"]!!.toInt(),
                    height = propertiesMap["height"]!!.toInt(),
                    mimeType = propertiesMap["mimeType"]!!,
                )
                val transformedList = propertiesMap.keys.asSequence()
                    .filter { key -> key == "transformed" }
                    .mapNotNull { key -> propertiesMap[key] }
                    .toList()
                val extras = propertiesMap.keys.asSequence()
                    .filter { key -> key.startsWith("extras.") }
                    .associate { key ->
                        val extraKey = key.replace("extras.", "")
                        val extraValue = propertiesMap[key]
                            ?: throw IllegalArgumentException("Illegal result cache properties: $metadataString")
                        extraKey to extraValue
                    }
                return Metadata(
                    imageInfo = imageInfo,
                    transformedList = transformedList,
                    extras = extras
                )
            }
        }
    }
}