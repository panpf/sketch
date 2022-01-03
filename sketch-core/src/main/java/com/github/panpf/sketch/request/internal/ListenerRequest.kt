package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener

interface ListenerRequest<REQUEST : ImageRequest, RESULT : ImageResult> {
    val listener: Listener<REQUEST, RESULT>?
    val networkProgressListener: ProgressListener<REQUEST>?
}