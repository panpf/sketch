package com.github.panpf.sketch.request

import com.github.panpf.sketch.util.SketchException

interface ImageResult {

    val request: ImageRequest

    interface Success : ImageResult

    interface Error : ImageResult {
        val exception: SketchException
    }
}