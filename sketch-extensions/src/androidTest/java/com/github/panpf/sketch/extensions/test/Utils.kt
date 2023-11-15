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
package com.github.panpf.sketch.extensions.test

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.internal.LruDiskCache
import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.findLifecycle
import kotlinx.coroutines.runBlocking
import java.io.File

fun getTestContextAndNewSketch(): Pair<Context, Sketch> {
    val context = InstrumentationRegistry.getInstrumentation().context
    return context to Sketch.Builder(context).apply {
        val directory = context.newTestDiskCacheDirectory()
        downloadCache(
            LruDiskCache.ForDownloadBuilder(context)
                .directory(File(directory, "download"))
                .build()
        )
        resultCache(
            LruDiskCache.ForResultBuilder(context)
                .directory(File(directory, "result"))
                .build()
        )
    }.build()
}

var sketchCount = 0

/* 低版本（大概是 21 及以下版本同一个文件不能打开多次，因此必须使用不同的缓存目录）*/
fun Context.newTestDiskCacheDirectory(): File {
    return File(externalCacheDir ?: cacheDir, DiskCache.DEFAULT_DIR_NAME + "${sketchCount++}")
}

internal fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$config)"

val Drawable.intrinsicSize: Size
    get() = Size(intrinsicWidth, intrinsicHeight)

fun samplingByTarget(imageSize: Size, targetSize: Size, mimeType: String? = null): Size {
    val sampleSize = calculateSampleSize(imageSize, targetSize, false)
    return calculateSampledBitmapSize(imageSize, sampleSize, mimeType)
}

fun ImageRequest.toRequestContext(resizeSize: Size? = null): RequestContext {
    return RequestContext(this, resizeSize ?: runBlocking { resizeSizeResolver.size() })
}

/**
 * A [Lifecycle] implementation that is always resumed and never destroyed.
 *
 * This is used as a fallback if [findLifecycle] cannot find a more tightly scoped [Lifecycle].
 */
internal object TestGlobalLifecycle : Lifecycle() {

    private val owner = object : LifecycleOwner {
        override val lifecycle: Lifecycle
            get() = this@TestGlobalLifecycle
    }

    override val currentState: State
        get() = State.RESUMED

    override fun addObserver(observer: LifecycleObserver) {
        require(observer is LifecycleEventObserver) {
            "$observer must implement androidx.lifecycle.LifecycleEventObserver."
        }

        // Call the lifecycle methods in order and do not hold a reference to the observer.
        observer.onStateChanged(owner, Event.ON_CREATE)
        observer.onStateChanged(owner, Event.ON_START)
        observer.onStateChanged(owner, Event.ON_RESUME)
    }

    override fun removeObserver(observer: LifecycleObserver) {}

    override fun toString() = "TestGlobalLifecycle"
}