package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking


fun ImageRequest.toRequestContextSync(sketch: Sketch, size: Size): RequestContext {
    return toRequestContext(sketch, size)
}

fun ImageRequest.toRequestContextSync(sketch: Sketch): RequestContext {
    return runBlocking { toRequestContext(sketch) }
}