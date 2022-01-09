package com.github.panpf.sketch.decode

interface DrawableDecoder {

    suspend fun decodeDrawable(): DrawableDecodeResult?
}