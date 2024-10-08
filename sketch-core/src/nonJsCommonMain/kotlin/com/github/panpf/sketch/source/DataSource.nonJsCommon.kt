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

package com.github.panpf.sketch.source

import com.github.panpf.sketch.Sketch
import kotlinx.coroutines.runBlocking
import okio.IOException
import okio.Path
import okio.buffer
import okio.use

@Throws(IOException::class)
actual fun DataSource.cacheFile(sketch: Sketch): Path = runBlocking {
    val resultCache = sketch.resultCache
    val resultCacheKey = "${key}_data_source"
    val snapshot = resultCache.withLock(resultCacheKey) {
        val snapshot = resultCache.openSnapshot(resultCacheKey)
        if (snapshot != null) {
            snapshot
        } else {
            val editor = resultCache.openEditor(resultCacheKey)
                ?: throw IOException("Disk cache cannot be used")
            try {
                openSource().buffer().use { source ->
                    resultCache.fileSystem.sink(editor.data).buffer().use { sink ->
                        sink.writeAll(source)
                    }
                }
                editor.commitAndOpenSnapshot()
            } catch (e: Throwable) {
                editor.abort()
                throw IOException("Error writing to cache", e)
            }
        }
    } ?: throw IOException("Disk cache cannot be used after edit")
    snapshot.use { it.data }
}