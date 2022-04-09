package com.github.panpf.sketch

fun interface SketchConfigurator {
    fun createSketchConfig(): Sketch.Builder.() -> Unit
}