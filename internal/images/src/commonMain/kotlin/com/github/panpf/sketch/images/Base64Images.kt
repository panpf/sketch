package com.github.panpf.sketch.images

object Base64Images {

    const val KOTLIN_ICON =
        "data:image/jpeg;base64,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"
}