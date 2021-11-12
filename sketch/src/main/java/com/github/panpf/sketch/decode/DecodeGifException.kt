package com.github.panpf.sketch.decode

import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.SketchException

class DecodeGifException(
    cause: Throwable,
    val request: LoadRequest,
    val outWidth: Int,
    val outHeight: Int,
    val outMimeType: String
) : SketchException(cause) {

    @get:Synchronized
    override val cause: Throwable
        get() = super.cause!!
}