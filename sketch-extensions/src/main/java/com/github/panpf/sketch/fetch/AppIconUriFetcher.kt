package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.AppIconUriFetcher.Companion.SCHEME
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.datasource.UnavailableDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.UriInvalidException
import java.io.FileDescriptor
import java.io.InputStream

/**
 * Sample: 'app.icon://com.github.panpf.sketch.sample/1120'
 */
fun newAppIconUri(packageName: String, versionCode: Int): String =
    "$SCHEME://$packageName/$versionCode"

/**
 * Support 'app.icon://com.github.panpf.sketch.sample/1120' uri
 */
class AppIconUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val packageName: String,
    val versionCode: Int,
) : Fetcher {

    companion object {
        const val SCHEME = "app.icon"
        const val MIME_TYPE = "application/vnd.android.app-icon"
    }

    override suspend fun fetch(): FetchResult = FetchResult(
        AppIconDataSource(sketch, request, LOCAL, packageName, versionCode),
        MIME_TYPE
    )

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): AppIconUriFetcher? {
            val uri = request.uri
            return if (SCHEME.equals(uri.scheme, ignoreCase = true)) {
                val packageName = uri.authority
                    ?: throw UriInvalidException(request, "App icon uri 'packageName' part invalid")
                val versionCode = uri.lastPathSegment?.toIntOrNull()
                    ?: throw UriInvalidException(request, "App icon uri 'versionCode' part invalid")
                AppIconUriFetcher(sketch, request, packageName, versionCode)
            } else {
                null
            }
        }

        override fun toString(): String = "AppIconUriFetcher"
    }

    class AppIconDataSource(
        override val sketch: Sketch,
        override val request: ImageRequest,
        override val dataFrom: DataFrom,
        val packageName: String,
        val versionCode: Int,
    ) : UnavailableDataSource {
        override fun length(): Long =
            throw UnsupportedOperationException("Please configure AppIconBitmapDecoder")

        override fun newFileDescriptor(): FileDescriptor? =
            throw UnsupportedOperationException("Please configure AppIconBitmapDecoder")

        override fun newInputStream(): InputStream =
            throw UnsupportedOperationException("Please configure AppIconBitmapDecoder")

        override fun toString(): String =
            "AppIconDataSource(packageName='$packageName',versionCode=$versionCode)"
    }
}