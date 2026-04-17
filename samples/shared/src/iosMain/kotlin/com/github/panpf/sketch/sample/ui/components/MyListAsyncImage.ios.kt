package com.github.panpf.sketch.sample.ui.components

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.videoFramePercent

actual fun ImageRequest.Builder.platformListAsyncImageRequestBuilder() {
    videoFramePercent(0.5f)
}