package com.github.panpf.sketch.zoom

fun interface OnDragFlingListener {
    fun onFling(startX: Float, startY: Float, velocityX: Float, velocityY: Float)
}