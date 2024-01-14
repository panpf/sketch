package com.github.panpf.sketch.util

fun Rect.toAndroidRect(): android.graphics.Rect {
    return android.graphics.Rect(left, top, right, bottom)
}

fun android.graphics.Rect.toSketchRect(): Rect {
    return Rect(left, top, right, bottom)
}