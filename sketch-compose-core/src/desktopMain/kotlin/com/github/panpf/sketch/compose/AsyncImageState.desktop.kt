package com.github.panpf.sketch.compose

import com.github.panpf.sketch.compose.request.AwtImage2ComposeImageRequestInterceptor
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageRequest.Builder

actual fun updateRequestBuilder(request: ImageRequest, builder: Builder) {
    builder.components {
        addRequestInterceptor(AwtImage2ComposeImageRequestInterceptor())
    }
}