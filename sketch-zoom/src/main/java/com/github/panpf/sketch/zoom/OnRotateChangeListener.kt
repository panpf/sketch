package com.github.panpf.sketch.zoom

import com.github.panpf.sketch.zoom.internal.Zoomer

fun interface OnRotateChangeListener {
    fun onRotateChanged(zoomer: Zoomer)
}