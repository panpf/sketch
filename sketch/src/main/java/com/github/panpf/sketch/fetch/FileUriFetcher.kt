package com.github.panpf.sketch.fetch

import android.webkit.MimeTypeMap
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.fetch.FileUriFetcher.Companion.SCHEME
import com.github.panpf.sketch.request.ImageRequest
import java.io.File

/**
 * Sample: 'file:///sdcard/sample.jpg'
 */
fun newFileUri(filePath: String): String = "$SCHEME://$filePath"

/**
 * Sample: 'file:///sdcard/sample.jpg'
 */
fun newFileUri(file: File): String = "$SCHEME://${file.path}"

/**
 * Support 'file:///sdcard/sample.jpg', '/sdcard/sample.jpg' uri
 */
class FileUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val file: File,
) : Fetcher {

    companion object {
        const val SCHEME = "file"
    }

    @WorkerThread
    override suspend fun fetch(): FetchResult {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
        return FetchResult(FileDataSource(sketch, request, file), mimeType)
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): FileUriFetcher? {
            val uriString = request.uriString
            val subStartIndex = when {
                SCHEME.equals(request.uri.scheme, ignoreCase = true) -> SCHEME.length + 3
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
            return null
        }

        override fun toString(): String = "FileUriFetcher"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}