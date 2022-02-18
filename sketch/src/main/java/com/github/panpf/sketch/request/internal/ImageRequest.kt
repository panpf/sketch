package com.github.panpf.sketch.request.internal

import android.content.Context
import android.net.Uri
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.Parameters
import com.github.panpf.sketch.request.RequestDepth

interface ImageRequest {
    val context: Context
    val key: String

    val uri: Uri
    val uriString: String
    val listener: Listener<ImageRequest, ImageResult, ImageResult>?
    val parameters: Parameters?
    val depth: RequestDepth?
    val depthFrom: String?
        get() = parameters?.value(REQUEST_DEPTH_FROM)

    companion object {
        const val REQUEST_DEPTH_FROM = "sketch#requestDepthFrom"
    }
}