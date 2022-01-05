package com.github.panpf.sketch

fun interface SketchFactory {
    fun newSketch(): Sketch
}