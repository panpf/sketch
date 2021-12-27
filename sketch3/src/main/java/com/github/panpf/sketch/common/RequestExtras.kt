package com.github.panpf.sketch.common

class RequestExtras<REQUEST : ImageRequest, DATA : ImageResult>(
    val listener: Listener<REQUEST, DATA>?,
    val httpFetchProgressListener: ProgressListener<REQUEST>?
)