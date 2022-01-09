package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.util.SketchException

class UriEmptyException(thenRequest: ImageRequest) :
    SketchException(thenRequest, "Request uri is empty or blank", null)