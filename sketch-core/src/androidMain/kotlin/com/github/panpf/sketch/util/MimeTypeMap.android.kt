package com.github.panpf.sketch.util

import android.webkit.MimeTypeMap

internal actual fun platformExtensionToMimeType(extension: String): String? {
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}

internal actual fun platformMimeTypeToExtension(mimeType: String): String? {
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
}