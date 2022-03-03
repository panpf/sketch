package com.github.panpf.sketch.zoom

import com.github.panpf.sketch.zoom.internal.Zoomer

fun interface OnMatrixChangeListener {
    fun onMatrixChanged(zoomer: Zoomer)
}