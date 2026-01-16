package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.ProgressListener

class TestProgressListener : ProgressListener {
    override fun onUpdateProgress(
        request: ImageRequest,
        progress: Progress
    ) {
    }
}