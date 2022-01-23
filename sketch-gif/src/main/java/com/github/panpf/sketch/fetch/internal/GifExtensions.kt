package com.github.panpf.sketch.fetch.internal

// https://www.matthewflickinger.com/lab/whatsinagif/bits_and_bytes.asp
private val GIF_HEADER_87A = "GIF87a".toByteArray()
private val GIF_HEADER_89A = "GIF89a".toByteArray()

/**
 * Return 'true' if the [HeaderBytes] contains a GIF image.
 */
fun HeaderBytes.isGif(): Boolean =
    rangeEquals(0, GIF_HEADER_89A) || rangeEquals(0, GIF_HEADER_87A)