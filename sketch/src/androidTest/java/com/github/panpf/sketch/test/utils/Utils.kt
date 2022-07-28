package com.github.panpf.sketch.test.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapRegionDecoder
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.graphics.drawable.DrawableCompat
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.internal.LruDiskCache
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.Logger.Level.VERBOSE
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format
import java.io.File
import java.io.InputStream

fun getTestContext(): Context {
    return InstrumentationRegistry.getInstrumentation().context
}

var sketchCount = 0

fun newSketch(block: Sketch.Builder.(context: Context) -> Unit): Sketch {
    val context = InstrumentationRegistry.getInstrumentation().context
    return Sketch.Builder(context).apply {
        logger(Logger(VERBOSE))
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
        block.invoke(this, context)
    }.build()
}

/* 低版本（大概是 21 及以下版本同一个文件不能打开多次，因此必须使用不同的缓存目录）*/
fun Context.newTestDiskCacheDirectory(): File {
    return File(externalCacheDir ?: cacheDir, DiskCache.DEFAULT_DIR_NAME + "${sketchCount++}")
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

val Drawable.intrinsicSize: Size
    get() = Size(intrinsicWidth, intrinsicHeight)

val ImageInfo.size: Size
    get() = Size(width, height)

val Size.ratio: Float
    get() = (width / height.toFloat()).format(1)

val Bitmap.size: Size
    get() = Size(width, height)

fun InputStream.newBitmapRegionDecoderInstanceCompat(): BitmapRegionDecoder? =
    if (VERSION.SDK_INT >= VERSION_CODES.S) {
        BitmapRegionDecoder.newInstance(this)
    } else {
        @Suppress("DEPRECATION")
        BitmapRegionDecoder.newInstance(this, false)
    }

/**
 * Get a reusable [Bitmap] if none is available, create a new one. Note that all colors are erased before returning.
 */
fun BitmapPool.exist(width: Int, height: Int, config: Bitmap.Config): Boolean {
    val bitmap = getDirty(width, height, config)
    if (bitmap != null) {
        put(bitmap, "exist")
    }
    return bitmap != null
}

/**
 * Returns exist of the entry named [key]
 */
fun DiskCache.exist(key: String): Boolean = get(key) != null

val Drawable.alphaCompat: Int
    get() = DrawableCompat.getAlpha(this)

fun samplingByTarget(imageSize: Size, targetSize: Size, mimeType: String? = null): Size {
    val sampleSize = calculateSampleSize(imageSize, targetSize)
    return calculateSampledBitmapSize(imageSize, sampleSize, mimeType)
}