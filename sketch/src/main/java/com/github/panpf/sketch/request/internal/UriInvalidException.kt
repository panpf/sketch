package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.util.SketchException

class UriInvalidException constructor(val uri: String, message: String) :
    SketchException(message, null)