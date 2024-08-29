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
import com.github.panpf.sketch.annotation.WorkerThread
import okio.IOException
import okio.Path
import okio.Source

/**
 * Provides access to the image data.
 */
interface DataSource {

    val key: String

    val dataFrom: DataFrom

    @Throws(IOException::class)
    fun openSource(): Source = openSourceOrNull() ?: throw IOException("Not supported Source")

    @Throws(IOException::class)
    fun openSourceOrNull(): Source?

    @Throws(IOException::class)
    fun getFile(sketch: Sketch): Path =
        getFileOrNull(sketch) ?: throw IOException("Not supported File")

    @Throws(IOException::class)
    fun getFileOrNull(sketch: Sketch): Path?
}

//suspend fun DataSource.cacheFileOrThrow(
//    sketch: Sketch,
//    request: ImageRequest,
//): Path {
//    val resultCache = sketch.resultCache
//    val resultCacheKey = "${key}_data_source"
//    val snapshot = resultCache.withLock(resultCacheKey) {
//        val snapshot = resultCache.openSnapshot(resultCacheKey)
//        if (snapshot != null) {
//            snapshot
//        } else {
//            val editor = resultCache.openEditor(resultCacheKey)
//                ?: throw IOException("Disk cache cannot be used")
//            try {
//                openSource().buffer().use { source ->
//                    resultCache.fileSystem.sink(editor.data).buffer().use { sink ->
//                        sink.writeAll(source)
//                    }
//                }
//                editor.commitAndOpenSnapshot()
//            } catch (e: Throwable) {
//                editor.abort()
//                throw e
//            } ?: throw IOException("Disk cache cannot be used after edit")
//        }
//    }
//    return snapshot.use { it.data }
//}
//
//suspend fun DataSource.cacheFileOrNull(
//    sketch: Sketch,
//    request: ImageRequest,
//): Path? = try {
//    cacheFileOrThrow(sketch, request)
//} catch (e: Throwable) {
//    e.printStackTrace()
//    null
//}

@WorkerThread
expect fun getDataSourceCacheFile(
    sketch: Sketch,
    dataSource: DataSource,
): Path?