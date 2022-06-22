package com.github.panpf.sketch.request

import com.github.panpf.sketch.util.SketchException

class DepthException constructor(val depth: Depth) :
    SketchException("Request depth only to $depth", null)