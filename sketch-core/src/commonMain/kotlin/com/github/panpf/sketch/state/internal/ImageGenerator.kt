package com.github.panpf.sketch.state.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Key

interface ImageGenerator : Key {
    fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image?
}