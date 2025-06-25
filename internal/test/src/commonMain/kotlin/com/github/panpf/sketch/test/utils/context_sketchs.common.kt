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

package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.appCacheDirectory
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import okio.Path

expect fun getTestContext(): PlatformContext

suspend inline fun runInNewSketchWithUse(
    crossinline builder: Sketch.Builder.(context: PlatformContext) -> Unit,
    crossinline block2: suspend (PlatformContext, Sketch) -> Unit
) {
    val context = getTestContext()
    val sketch = Sketch(context) {
        logger(level = Logger.Level.Verbose)
        val directory = context.newAloneTestDiskCacheDirectory()
        downloadCacheOptions(DiskCache.Options(appCacheDirectory = directory))
        resultCacheOptions(DiskCache.Options(appCacheDirectory = directory))
        builder.invoke(this, context)
    }
    try {
        block2(context, sketch)
    } finally {
        try {
            sketch.downloadCache.clear()
            sketch.resultCache.clear()
            sketch.shutdown()
            val fileSystem = sketch.fileSystem
            fileSystem.deleteRecursively(sketch.downloadCache.directory)
            fileSystem.delete(sketch.downloadCache.directory)
            fileSystem.deleteRecursively(sketch.resultCache.directory)
            fileSystem.delete(sketch.resultCache.directory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

suspend inline fun runInNewSketchWithUse(
    crossinline block2: suspend (PlatformContext, Sketch) -> Unit
) {
    val context = getTestContext()
    val sketch = Sketch(context) {
        logger(level = Logger.Level.Verbose)
        val directory = context.newAloneTestDiskCacheDirectory()
        downloadCacheOptions(DiskCache.Options(appCacheDirectory = directory))
        resultCacheOptions(DiskCache.Options(appCacheDirectory = directory))
    }
    try {
        block2(context, sketch)
    } finally {
        try {
            sketch.downloadCache.clear()
            sketch.resultCache.clear()
            sketch.shutdown()
            val fileSystem = sketch.fileSystem
            fileSystem.deleteRecursively(sketch.downloadCache.directory)
            fileSystem.delete(sketch.downloadCache.directory)
            fileSystem.deleteRecursively(sketch.resultCache.directory)
            fileSystem.delete(sketch.resultCache.directory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

var sketchCount = 0
val sketchCountLock = SynchronizedObject()

fun PlatformContext.newAloneTestDiskCacheDirectory(): Path? {
    val testDiskCacheDirectory = appCacheDirectory() ?: return null
    val newSketchCount = synchronized(sketchCountLock) { sketchCount++ }
    return testDiskCacheDirectory.resolve("test_alone_${newSketchCount}")
}