package com.github.panpf.sketch.decode

import com.github.panpf.sketch.SketchException
import com.github.panpf.sketch.process.ImageProcessor

class ProcessImageException(cause: Throwable, val imageUri: String, val processor: ImageProcessor) :
    SketchException(cause) {

    @get:Synchronized
    override val cause: Throwable
        get() = super.cause!!
}