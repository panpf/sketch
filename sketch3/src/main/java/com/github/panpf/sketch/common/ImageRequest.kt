package com.github.panpf.sketch.common

import android.net.Uri
import android.os.Bundle

interface ImageRequest {
    val uri: Uri
    val extras: Bundle?
}



