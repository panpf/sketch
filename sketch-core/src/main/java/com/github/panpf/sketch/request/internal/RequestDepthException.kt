package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.SketchException
import com.github.panpf.sketch.request.RequestDepth

class RequestDepthException(
    thenRequest: ImageRequest,
    val depth: RequestDepth,
) : SketchException(thenRequest, "Request depth only to $depth", null)