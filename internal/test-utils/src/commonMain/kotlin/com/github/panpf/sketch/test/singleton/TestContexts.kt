package com.github.panpf.sketch.test.singleton

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newSketch

object SketchHolder {
    init {
        SingletonSketch.setSafe { newSketch() }
    }

    val sketch = SingletonSketch.get(getTestContext())
}

fun getTestContextAndSketch(): Pair<PlatformContext, Sketch> {
    val context = getTestContext()
    return context to SketchHolder.sketch
}