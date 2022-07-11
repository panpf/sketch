package com.github.panpf.sketch.zoom

import com.github.panpf.sketch.Sketch

interface ReadModeDecider {

    fun should(
        sketch: Sketch, imageWidth: Int, imageHeight: Int, viewWidth: Int, viewHeight: Int
    ): Boolean
}