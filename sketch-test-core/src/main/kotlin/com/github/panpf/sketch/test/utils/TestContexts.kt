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
package com.github.panpf.sketch.test.utils

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.internal.LruDiskCache
import com.github.panpf.sketch.test.utils.newTestDiskCacheDirectory
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Logger.Level.VERBOSE
import java.io.File

fun getTestContext(): Context {
    return InstrumentationRegistry.getInstrumentation().context
}

fun newSketch(block: Sketch.Builder.(context: Context) -> Unit): Sketch {
    val context = InstrumentationRegistry.getInstrumentation().context
    return Sketch.Builder(context).apply {
        logger(Logger(VERBOSE))
        val directory = context.newTestDiskCacheDirectory()
        downloadCache(DiskCache.Options(directory = File(directory, "download")))
        resultCache(DiskCache.Options(directory = File(directory, "result")))
        block.invoke(this, context)
    }.build()
}

fun newSketch(): Sketch {
    return newSketch {}
}

fun getTestContextAndNewSketch(): Pair<Context, Sketch> {
    val context = InstrumentationRegistry.getInstrumentation().context
    return context to newSketch()
}

fun getTestContextAndNewSketch(block: Sketch.Builder.(context: Context) -> Unit): Pair<Context, Sketch> {
    val context = InstrumentationRegistry.getInstrumentation().context
    return context to newSketch(block)
}