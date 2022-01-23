package com.github.panpf.sketch.fetch.internal

private val SVG_TAG = "<svg ".toByteArray()
private val LEFT_ANGLE_BRACKET = "<".toByteArray()

fun HeaderBytes.isSvg(): Boolean =
    rangeEquals(0, LEFT_ANGLE_BRACKET) && indexOf(SVG_TAG, 0, 1024) != -1