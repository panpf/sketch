package com.github.panpf.sketch.common

class RequestExtras<REQUEST : ImageRequest, DATA : ImageData>(
    val listener: Listener<REQUEST, DATA>?,
    val httpFetchProgressListener: ProgressListener<REQUEST>?
)