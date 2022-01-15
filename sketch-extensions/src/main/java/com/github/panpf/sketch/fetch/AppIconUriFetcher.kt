package com.github.panpf.sketch.fetch

import android.net.Uri
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.fetch.AppIconUriFetcher.Companion.SCHEME
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.DataFrom.LOCAL
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.UriInvalidException
import java.io.FileDescriptor
import java.io.InputStream

/**
 * Sample: 'app.icon://com.github.panpf.sketch.sample/1120'
 */
fun newAppIconUri(packageName: String, versionCode: Int): Uri =
    Uri.parse("$SCHEME://$packageName/$versionCode")

/**
 * Support 'app.icon://com.github.panpf.sketch.sample/1120' uri
 */
class AppIconUriFetcher(
    val sketch: Sketch,
    val request: LoadRequest,
    val packageName: String,
    val versionCode: Int,
) : Fetcher {

    companion object {
        const val SCHEME = "app.icon"
        const val MIME_TYPE = "application/vnd.android.app-icon"
    }

    override suspend fun fetch(): FetchResult {
        return FetchResult(
            AppIconDataSource(sketch, request, LOCAL, packageName, versionCode),
            MIME_TYPE
        )
    }

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): AppIconUriFetcher? {
            val uri = request.uri
            return if (request is LoadRequest && SCHEME.equals(uri.scheme, ignoreCase = true)) {
                val packageName = uri.authority
                    ?: throw UriInvalidException(request, "App icon uri 'packageName' part invalid")
                val versionCode = uri.lastPathSegment?.toIntOrNull()
                    ?: throw UriInvalidException(request, "App icon uri 'versionCode' part invalid")
                AppIconUriFetcher(sketch, request, packageName, versionCode)
            } else {
                null
            }
        }
    }

    class AppIconDataSource(
        override val sketch: Sketch,
        override val request: LoadRequest,
        override val from: DataFrom,
        val packageName: String,
        val versionCode: Int,
    ) : DataSource {
        override fun length(): Long {
            throw UnsupportedOperationException("Please configure AppIconBitmapDecoder")
        }

        override fun newFileDescriptor(): FileDescriptor? {
            throw UnsupportedOperationException("Please configure AppIconBitmapDecoder")
        }

        override fun newInputStream(): InputStream {
            throw UnsupportedOperationException("Please configure AppIconBitmapDecoder")
        }
    }
}