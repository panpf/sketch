package com.github.panpf.sketch.decode

import com.github.panpf.sketch.SketchException

class NotFoundGifSoException : SketchException {
    constructor(cause: UnsatisfiedLinkError) : super(cause) {}
    constructor(cause: ExceptionInInitializerError) : super(cause) {}

    @get:Synchronized
    override val cause: Throwable
        get() = super.cause!!
}