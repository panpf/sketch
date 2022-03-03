package com.github.panpf.sketch.zoom

fun interface OnScaleChangeListener {
    fun onScaleChanged(scaleFactor: Float, focusX: Float, focusY: Float)
}