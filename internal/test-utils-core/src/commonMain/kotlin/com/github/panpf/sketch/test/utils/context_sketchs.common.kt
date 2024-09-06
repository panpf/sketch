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

// TODO Change to use the newly created Sketch in the block,
//  and automatically clear the downloadCache and resultCache after the block ends to
//  prevent more and more junk files from being generated.
fun getTestContextAndNewSketch(block: Sketch.Builder.(context: PlatformContext) -> Unit): Pair<PlatformContext, Sketch> {
    val context = getTestContext()
    val newSketch = Sketch.Builder(context).apply {
        logger(level = Logger.Level.Verbose)
        val directory = context.newAloneTestDiskCacheDirectory()
        downloadCacheOptions(DiskCache.Options(appCacheDirectory = directory))
        resultCacheOptions(DiskCache.Options(appCacheDirectory = directory))
        block.invoke(this, context)
    }.build()
    return context to newSketch
}

var sketchCount = 0
val sketchCountLock = SynchronizedObject()

fun PlatformContext.newAloneTestDiskCacheDirectory(): Path? {
    val testDiskCacheDirectory = appCacheDirectory() ?: return null
    val newSketchCount = synchronized(sketchCountLock) { sketchCount++ }
    return testDiskCacheDirectory.resolve("test_alone_${newSketchCount}")
}