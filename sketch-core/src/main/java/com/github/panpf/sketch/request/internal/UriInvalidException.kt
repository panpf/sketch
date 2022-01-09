package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.util.SketchException

class UriInvalidException(thenRequest: ImageRequest, message: String) :
    SketchException(thenRequest, message, null)