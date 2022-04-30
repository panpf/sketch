package com.github.panpf.sketch.extensions.test

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sketch

fun contextAndSketch(): Pair<Context, Sketch> {
    val context = InstrumentationRegistry.getInstrumentation().context
    return context to context.sketch
}

fun context(): Context {
    return InstrumentationRegistry.getInstrumentation().context
}