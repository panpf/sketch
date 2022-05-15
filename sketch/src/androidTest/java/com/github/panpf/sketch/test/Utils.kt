package com.github.panpf.sketch.test

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch

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