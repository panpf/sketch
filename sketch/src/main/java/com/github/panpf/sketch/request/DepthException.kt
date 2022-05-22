package com.github.panpf.sketch.request

import com.github.panpf.sketch.util.SketchException

class DepthException constructor(
    thenRequest: ImageRequest,
    val depth: Depth,
    val depthFrom: String?,
) : SketchException(thenRequest, "Request depth only to $depth, from '${depthFrom}'", null)