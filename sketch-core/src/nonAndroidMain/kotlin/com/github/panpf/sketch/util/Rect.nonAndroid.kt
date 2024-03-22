package com.github.panpf.sketch.util

typealias SkiaRect = org.jetbrains.skia.Rect
typealias SketchRect = Rect

fun SketchRect.toSkiaRect(): SkiaRect = SkiaRect(
    left = left.toFloat(),
    top = top.toFloat(),
    right = right.toFloat(),
    bottom = bottom.toFloat()
)