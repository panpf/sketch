package com.github.panpf.sketch

import com.github.panpf.sketch.Sketch.Builder

fun interface SketchConfigurator {
    fun configSketch(builder: Builder)
}