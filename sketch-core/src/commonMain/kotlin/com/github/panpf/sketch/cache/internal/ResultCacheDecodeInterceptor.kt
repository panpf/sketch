/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.cache.createImageSerializer
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.source.DataFrom.RESULT_CACHE
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.closeQuietly
import okio.buffer
import okio.use

/**
 * Result cache decode interceptor, used to read and write the decode result to the disk cache
 *
 * @see com.github.panpf.sketch.core.common.test.cache.internal.ResultCacheDecodeInterceptorTest
 */
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
            val resultCacheKey = requestContext.resultCacheKey
            resultCache.withLock(resultCacheKey) {
                val decodeResultFromCache = readCache(requestContext)
                if (decodeResultFromCache != null) {
                    Result.success(decodeResultFromCache)
                } else {
                    chain.proceed().apply {
                        val newDecodeResult = getOrNull()
                        if (newDecodeResult != null) {
                            writeCache(requestContext, decodeResult = newDecodeResult)
                        }
                    }
                }
            }
        } else {
            chain.proceed()
        }
    }

    @WorkerThread
    private fun readCache(requestContext: RequestContext): DecodeResult? {
        if (!requestContext.request.resultCachePolicy.readEnabled) return null
        val resultCache = requestContext.sketch.resultCache
        val fileSystem = resultCache.fileSystem
        val resultCacheKey = requestContext.resultCacheKey
        val imageSerializer = createImageSerializer()
        val snapshot = runCatching { resultCache.openSnapshot(resultCacheKey) }.getOrNull()
        if (snapshot == null) return null
        val result = runCatching {
            val dataSource = FileDataSource(
                path = snapshot.data,
                fileSystem = requestContext.sketch.fileSystem,
                dataFrom = RESULT_CACHE,
            )
            val metadataString = fileSystem.source(snapshot.metadata).buffer().use { it.readUtf8() }
            val metadata = Metadata.fromMetadataString(metadataString)
            val image = imageSerializer.decode(requestContext, metadata.imageInfo, dataSource)
            DecodeResult(
                image = image,
                imageInfo = metadata.imageInfo,
                dataFrom = RESULT_CACHE,
                resize = metadata.resize,
                transformeds = metadata.transformeds,
                extras = metadata.extras
            )
        }
        snapshot.closeQuietly()

        result.onFailure {
            it.printStackTrace()
            requestContext.sketch.logger.w {
                "ResultCacheDecodeInterceptor. read result cache error. $it. '${requestContext.logKey}'"
            }
            resultCache.remove(resultCacheKey)
        }
        return result.getOrNull()
    }

    @WorkerThread
    private fun writeCache(
        requestContext: RequestContext,
        decodeResult: DecodeResult,
    ): Boolean {
        if (!requestContext.request.resultCachePolicy.writeEnabled) return false
        val transformeds = decodeResult.transformeds
        if (transformeds.isNullOrEmpty()) return false
        val image = decodeResult.image
        val imageSerializer =
            createImageSerializer().takeIf { it.supportImage(image) } ?: return false
        val resultCache = requestContext.sketch.resultCache
        val resultCacheKey = requestContext.resultCacheKey
        val editor = resultCache.openEditor(resultCacheKey)
        if (editor == null) return false
        val result = runCatching {
            resultCache.fileSystem.sink(editor.data).buffer().use {
                imageSerializer.compress(image, it)
            }

            val metadataString = Metadata(
                imageInfo = decodeResult.imageInfo,
                transformeds = transformeds,
                resize = decodeResult.resize,
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && this::class == other::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String = "ResultCacheDecodeInterceptor(sortWeight=$sortWeight)"

    class Metadata(
        val imageInfo: ImageInfo,
        val resize: Resize,
        val transformeds: List<String>?,
        val extras: Map<String, String>?
    ) {

        fun toMetadataString(): String = buildString {
            appendLine("width=${imageInfo.width}")
            appendLine("height=${imageInfo.height}")
            appendLine("mimeType=${imageInfo.mimeType}")
            appendLine("resizeWidth=${resize.size.width}")
            appendLine("resizeHeight=${resize.size.height}")
            appendLine("resizePrecision=${resize.precision}")
            appendLine("resizeScale=${resize.scale}")
            transformeds?.forEach {
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
                val resize = Resize(
                    size = Size(
                        width = propertiesMap["resizeWidth"]!!.toInt(),
                        height = propertiesMap["resizeHeight"]!!.toInt()
                    ),
                    precision = Precision.valueOf(propertiesMap["resizePrecision"]!!),
                    scale = Scale.valueOf(propertiesMap["resizeScale"]!!)
                )
                val transformeds = propertiesMap.keys.asSequence()
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
                    resize = resize,
                    transformeds = transformeds,
                    extras = extras
                )
            }
        }
    }
}