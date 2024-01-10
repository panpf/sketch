package com.github.panpf.sketch.test.singleton

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.Sketch

fun getTestContextAndSketch(): Pair<Context, Sketch> {
    val context = InstrumentationRegistry.getInstrumentation().context
    return context to context.sketch
}