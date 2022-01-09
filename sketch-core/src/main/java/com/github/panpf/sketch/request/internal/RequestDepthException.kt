package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.RequestDepth
import com.github.panpf.sketch.util.SketchException

class RequestDepthException(
    thenRequest: ImageRequest,
    val depth: RequestDepth,
) : SketchException(thenRequest, "Request depth only to $depth", null)