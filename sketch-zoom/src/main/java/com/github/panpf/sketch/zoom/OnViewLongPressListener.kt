package com.github.panpf.sketch.zoom

import android.view.View

fun interface OnViewLongPressListener {
    fun onViewLongPress(view: View, x: Float, y: Float)
}