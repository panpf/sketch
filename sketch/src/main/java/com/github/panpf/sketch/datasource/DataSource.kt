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
package com.github.panpf.sketch.datasource

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.IOException
import java.io.InputStream

interface DataSource {

    val sketch: Sketch

    val request: ImageRequest

    val dataFrom: DataFrom

    @WorkerThread
    @Throws(IOException::class)
    fun length(): Long

    @WorkerThread
    @Throws(IOException::class)
    fun newInputStream(): InputStream

    @WorkerThread
    @Throws(IOException::class)
    fun file(): File = runBlocking {
        val resultCache = sketch.resultCache
        val resultCacheKey = request.uriString + "_data_source"
        resultCache.editLock(resultCacheKey).withLock {
            val snapshot = resultCache[resultCacheKey]
            if (snapshot != null) {
                snapshot
            } else {
                val editor = resultCache.edit(resultCacheKey)
                    ?: throw IOException("Disk cache cannot be used")
                try {
                    newInputStream().use { inputStream ->
                        editor.newOutputStream().buffered().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    editor.commit()
                } catch (e: Throwable) {
                    editor.abort()
                    throw e
                }
                resultCache[resultCacheKey]
                    ?: throw IOException("Disk cache cannot be used after edit")
            }
        }.file
    }
}