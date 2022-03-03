package com.github.panpf.sketch.zoom

import android.view.View

fun interface OnViewTapListener {
    fun onViewTap(view: View, x: Float, y: Float)
}