package com.github.panpf.sketch.request.internal

import android.net.Uri
import com.github.panpf.sketch.request.Parameters

interface ImageRequest {
    val uri: Uri
//
//    /**
//     * Requests same key are queued for execution
//     */
//    val key: String
    val parameters: Parameters?
}



