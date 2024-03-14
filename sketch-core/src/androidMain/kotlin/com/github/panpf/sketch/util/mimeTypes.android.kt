package com.github.panpf.sketch.util

import android.webkit.MimeTypeMap

internal actual fun platformExtensionToMimeTypeMap(extension: String): String? {
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}

internal actual fun platformMimeTypeToExtensionMap(mimeType: String): String? {
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
}
