package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.datasource.PathDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.MimeTypeMap
import net.thauvin.erik.urlencoder.UrlEncoderUtil
import okio.Path
import okio.Path.Companion.toPath

/**
 * Sample: 'path:///sdcard/sample.jpg'
 */
fun newPathUri(filePath: String): String = "${PathUriFetcher.SCHEME}://$filePath"

/**
 * Sample: 'path:///sdcard/sample.jpg'
 */
fun newPathUri(file: Path): String = "${PathUriFetcher.SCHEME}://${file}"

/**
 * Support 'path:///sdcard/sample.jpg', '/sdcard/sample.jpg' uri
 */
class PathUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val path: Path,
) : Fetcher {

    companion object {
        const val SCHEME = "path"
        const val SCHEME2 = "file"

        fun parseFilePathFromFileUri(uriString: String): String? {
            val startFlag = "$SCHEME://"
            val startFlag2 = "$SCHEME2://"
            return if (uriString.startsWith(startFlag, ignoreCase = true)) {
                val subStartIndex = startFlag.length
                val subEndIndex = uriString.indexOf("?").takeIf { it != -1 }
                    ?: uriString.indexOf("#").takeIf { it != -1 }
                    ?: uriString.length
                val filePath = uriString.substring(subStartIndex, subEndIndex)
                UrlEncoderUtil.decode(filePath)
            } else if (uriString.startsWith(startFlag2, ignoreCase = true)) {
                val subStartIndex = startFlag2.length
                val subEndIndex = uriString.indexOf("?").takeIf { it != -1 }
                    ?: uriString.indexOf("#").takeIf { it != -1 }
                    ?: uriString.length
                val filePath = uriString.substring(subStartIndex, subEndIndex)
                UrlEncoderUtil.decode(filePath)
            } else if (uriString.startsWith("/")) {
                uriString
            } else {
                null
            }
        }
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> = kotlin.runCatching {
        val extension = MimeTypeMap.getExtensionFromUrl(path.name)
        val mimeType = extension?.let { MimeTypeMap.getMimeTypeFromExtension(it) }
        FetchResult(PathDataSource(sketch, request, path), mimeType)
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): PathUriFetcher? {
            val path = parseFilePathFromFileUri(request.uriString) ?: return null
            return PathUriFetcher(sketch, request, path.toPath())
        }

        override fun toString(): String = "PathUriFetcher"

        @Suppress("RedundantOverride")
        override fun equals(other: Any?): Boolean {
            // If you add construction parameters to this class, you need to change it here
            return super.equals(other)
        }

        @Suppress("RedundantOverride")
        override fun hashCode(): Int {
            // If you add construction parameters to this class, you need to change it here
            return super.hashCode()
        }
    }
}