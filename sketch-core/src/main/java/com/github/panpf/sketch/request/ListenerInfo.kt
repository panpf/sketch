package com.github.panpf.sketch.request

import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ImageResult

class ListenerInfo<REQUEST : ImageRequest, DATA : ImageResult>(
    val lifecycleListener: Listener<REQUEST, DATA>?,
    val httpFetchProgressListener: ProgressListener<REQUEST>?
)