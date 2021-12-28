package com.github.panpf.sketch.common

class ListenerInfo<REQUEST : ImageRequest, DATA : ImageResult>(
    val lifecycleListener: Listener<REQUEST, DATA>?,
    val httpFetchProgressListener: ProgressListener<REQUEST>?
)