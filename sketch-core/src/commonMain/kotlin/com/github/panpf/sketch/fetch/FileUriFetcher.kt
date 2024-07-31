package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.Uri
import com.github.panpf.sketch.util.toUri
import okio.Path
import okio.Path.Companion.toPath

/**
 * Sample: 'file:///sdcard/sample.jpg'
 */
fun newFileUri(path: String): String = "${FileUriFetcher.SCHEME}://$path"

/**
 * Sample: 'file:///sdcard/sample.jpg'
 */
fun newFileUri(path: Path): String = "${FileUriFetcher.SCHEME}://${path}"

/**
 * Check if the uri is a file uri
 *
 * Support 'file:///sdcard/sample.jpg', '/sdcard/sample.jpg' uri
 */
fun isFileUri(uri: Uri): Boolean =
    (uri.scheme == null || FileUriFetcher.SCHEME.equals(uri.scheme, ignoreCase = true))
            && uri.authority?.takeIf { it.isNotEmpty() } == null
            && uri.path?.takeIf { it.isNotEmpty() } != null

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
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> = kotlin.runCatching {
        val extension = MimeTypeMap.getExtensionFromUrl(path.name)
        val mimeType = extension?.let { MimeTypeMap.getMimeTypeFromExtension(it) }
        FetchResult(FileDataSource(sketch, request, path, LOCAL), mimeType)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FileUriFetcher
        if (sketch != other.sketch) return false
        if (request != other.request) return false
        if (path != other.path) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sketch.hashCode()
        result = 31 * result + request.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }

    override fun toString(): String {
        return "FileUriFetcher('$path')"
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): FileUriFetcher? {
            val uri = request.uri.toUri()
            return if (isFileUri(uri)) {
                FileUriFetcher(sketch, request, uri.path!!.toPath())
            } else {
                null
            }
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