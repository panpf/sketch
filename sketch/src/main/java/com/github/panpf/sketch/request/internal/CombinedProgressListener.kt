package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ProgressListener

class CombinedProgressListener<REQUEST : ImageRequest>(
    val fromViewProgressListener: ProgressListener<REQUEST>,
    val fromBuilderProgressListener: ProgressListener<REQUEST>,
) : ProgressListener<REQUEST> {

    override fun onUpdateProgress(request: REQUEST, totalLength: Long, completedLength: Long) {
        fromViewProgressListener.onUpdateProgress(request, totalLength, completedLength)
        fromBuilderProgressListener.onUpdateProgress(request, totalLength, completedLength)
    }
}