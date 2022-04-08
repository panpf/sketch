package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestDepth
import com.github.panpf.sketch.util.SketchException

class RequestDepthException constructor(
    thenRequest: ImageRequest,
    val depth: RequestDepth,
    val depthFrom: String?,
) : SketchException(thenRequest, "Request depth only to $depth, from '${depthFrom}'", null)