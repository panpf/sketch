package com.github.panpf.sketch.extensions.test

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.internal.LruDiskCache
import java.io.File

fun getTestContextAndNewSketch(): Pair<Context, Sketch> {
    val context = InstrumentationRegistry.getInstrumentation().context
    return context to Sketch.Builder(context).apply {
        val directory = context.newTestDiskCacheDirectory()
        downloadDiskCache(
            LruDiskCache.ForDownloadBuilder(context)
                .directory(File(directory, "download_cache"))
                .build()
        )
        resultDiskCache(
            LruDiskCache.ForResultBuilder(context)
                .directory(File(directory, "result_cache"))
                .build()
        )
    }.build()
}

fun getTestContext(): Context {
    return InstrumentationRegistry.getInstrumentation().context
}

var sketchCount = 0

/* 低版本（大概是 21 及以下版本同一个文件不能打开多次，因此必须使用不同的缓存目录）*/
fun Context.newTestDiskCacheDirectory(): File {
    return File(externalCacheDir ?: cacheDir, DiskCache.DEFAULT_DIR_NAME + "${sketchCount++}")
}