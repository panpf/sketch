package com.github.panpf.sketch.extensions.test

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch

fun getContextAndSketch(): Pair<Context, Sketch> {
    val context = InstrumentationRegistry.getInstrumentation().context
    return context to Sketch.Builder(context).build()
}

fun getContext(): Context {
    return InstrumentationRegistry.getInstrumentation().context
}