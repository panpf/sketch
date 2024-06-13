package com.github.panpf.sketch.util

fun Rect.toAndroidRect(): android.graphics.Rect = android.graphics.Rect(left, top, right, bottom)

fun android.graphics.Rect.toSketchRect(): Rect = Rect(left, top, right, bottom)