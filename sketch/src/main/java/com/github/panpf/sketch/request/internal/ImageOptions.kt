package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.Parameters
import com.github.panpf.sketch.request.RequestDepth

interface ImageOptions {
    val depth: RequestDepth?
    val parameters: Parameters?

    val depthFrom: String?
        get() = parameters?.value(ImageRequest.REQUEST_DEPTH_FROM)

    open fun isEmpty(): Boolean = depth == null && parameters?.isEmpty() != false
}