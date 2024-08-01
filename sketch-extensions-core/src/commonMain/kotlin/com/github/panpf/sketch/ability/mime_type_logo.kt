package com.github.panpf.sketch.ability

import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.util.MimeTypeMap

fun getMimeTypeFromImageResult(result: ImageResult?, uri: String? = null): String? {
    var mimeType: String? = null
    if (result is ImageResult.Success) {
        mimeType = result.imageInfo.mimeType.takeIf { it.isNotEmpty() }
    }
    if (mimeType == null && result is ImageResult.Error) {
        mimeType =
            MimeTypeMap.getMimeTypeFromUrl(result.request.uri.toString())
                ?.takeIf { it.isNotEmpty() }
    }
    if (mimeType == null && uri != null) {
        mimeType = MimeTypeMap.getMimeTypeFromUrl(uri)?.takeIf { it.isNotEmpty() }
    }
    return mimeType
}