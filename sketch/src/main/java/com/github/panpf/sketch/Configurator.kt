package com.github.panpf.sketch

import com.github.panpf.sketch.Sketch.Builder

fun interface Configurator {
    fun configSketch(builder: Builder)
}