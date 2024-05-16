package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataFrom.LOCAL
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

        fun parseFilePathFromFileUri(uri: String): String? {
            val uri1 = uri.toUri()
            return if (SCHEME.equals(uri1.scheme, ignoreCase = true) || uri.startsWith("/")) {
                val resourcePath = "${uri1.authority.orEmpty()}${uri1.path.orEmpty()}"
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
        FetchResult(FileDataSource(sketch, request, path, LOCAL), mimeType)
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): FileUriFetcher? {
            val path = parseFilePathFromFileUri(request.uri) ?: return null
            return FileUriFetcher(sketch, request, path.toPath())
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Factory
        }

        override fun hashCode(): Int {
            return this@Factory::class.hashCode()
        }

        override fun toString(): String = "FileUriFetcher"
    }
}