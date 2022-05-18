package com.github.panpf.sketch.test.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format

fun getContextAndSketch(): Pair<Context, Sketch> {
    val context = InstrumentationRegistry.getInstrumentation().context
    return context to Sketch.Builder(context).build()
}

fun getContextAndSketch(block: Sketch.Builder.(context: Context) -> Unit): Pair<Context, Sketch> {
    val context = InstrumentationRegistry.getInstrumentation().context
    return context to Sketch.Builder(context).apply {
        block.invoke(this, context)
    }.build()
}

fun getContext(): Context {
    return InstrumentationRegistry.getInstrumentation().context
}

fun getSketch(): Sketch {
    val context = InstrumentationRegistry.getInstrumentation().context
    return Sketch.Builder(context).build()
}

fun getSketch(block: Sketch.Builder.(context: Context) -> Unit): Sketch {
    val context = InstrumentationRegistry.getInstrumentation().context
    return Sketch.Builder(context).apply {
        block.invoke(this, context)
    }.build()
}

val Drawable.intrinsicSize: Size
    get() = Size(intrinsicWidth, intrinsicHeight)

val ImageInfo.size: Size
    get() = Size(width, height)

val Size.ratio: Float
    get() = (width / height.toFloat()).format(1)

val Bitmap.size: Size
    get() = Size(width, height)