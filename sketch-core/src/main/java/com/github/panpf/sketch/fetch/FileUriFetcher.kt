package com.github.panpf.sketch.fetch

import android.net.Uri
import android.webkit.MimeTypeMap
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest
import java.io.File

fun newFileUri(filePath: String): Uri = FileUriFetcher.newUri(filePath)

/**
 * Support 'file:///sdcard/sample.jpg', '/sdcard/sample.jpg' uri
 */
class FileUriFetcher(
    val sketch: Sketch,
    val request: LoadRequest,
    val file: File,
) : Fetcher {

    companion object {
        const val SCHEME = "file"

        @JvmStatic
        fun newUri(filePath: String): Uri = Uri.parse("$SCHEME://$filePath")
    }

    override suspend fun fetch(): FetchResult {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
        return FetchResult(FileDataSource(sketch, request, file), mimeType)
    }

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): FileUriFetcher? {
            val uriString = request.uriString
            if (request is LoadRequest) {
                val subStartIndex = when {
                    SCHEME.equals(request.uri.scheme, ignoreCase = true) -> SCHEME.length + 3   // file://
                    uriString.startsWith("/") -> 0
                    else -> -1
                }
                if (subStartIndex != -1) {
                    val subEndIndex = uriString.indexOf("?").takeIf { it != -1 }
                        ?: uriString.indexOf("#").takeIf { it != -1 }
                        ?: uriString.length
                    val file = File(uriString.substring(subStartIndex, subEndIndex))
                    return FileUriFetcher(sketch, request, file)
                }
            }
            return null
        }
    }
}