package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.SketchException

class FixedSizeException(thenRequest: ImageRequest, message: String) :
    SketchException(thenRequest, message, null)