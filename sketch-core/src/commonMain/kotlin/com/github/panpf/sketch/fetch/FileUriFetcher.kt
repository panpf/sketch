package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.toUri
import net.thauvin.erik.urlencoder.UrlEncoderUtil
import okio.Path
import okio.Path.Companion.toPath

/**
 * Sample: 'file:///sdcard/sample.jpg'
 */
fun newFileUri(filePath: String): String = "${FileUriFetcher.SCHEME}://$filePath"

/**
 * Sample: 'file:///sdcard/sample.jpg'
 */
fun newFileUri(file: Path): String = "${FileUriFetcher.SCHEME}://${file}"

/**
 * Support 'file:///sdcard/sample.jpg', '/sdcard/sample.jpg' uri
 */
class FileUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val path: Path,
) : Fetcher {

    companion object {
        const val SCHEME = "file"

        fun parseFilePathFromFileUri(uriString: String): String? {
            val uri = uriString.toUri()
            return if (SCHEME.equals(uri.scheme, ignoreCase = true) || uriString.startsWith("/")) {
                val resourcePath = "${uri.authority.orEmpty()}${uri.path.orEmpty()}"
                UrlEncoderUtil.decode(resourcePath)
            } else {
                null
            }
        }
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> = kotlin.runCatching {
        val extension = MimeTypeMap.getExtensionFromUrl(path.name)
        val mimeType = extension?.let { MimeTypeMap.getMimeTypeFromExtension(it) }
        FetchResult(FileDataSource(sketch, request, path), mimeType)
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): FileUriFetcher? {
            val path = parseFilePathFromFileUri(request.uriString) ?: return null
            return FileUriFetcher(sketch, request, path.toPath())
        }

        override fun toString(): String = "FileUriFetcher"

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