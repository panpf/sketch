package com.github.panpf.sketch.fetch

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.datasource.UnavailableDataSource
import com.github.panpf.sketch.fetch.AppIconUriFetcher.Companion.SCHEME
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.UriInvalidException
import com.github.panpf.sketch.util.ifOrNull
import java.io.InputStream

/**
 * Sample: 'app.icon://com.github.panpf.sketch.sample/1120'
 */
fun newAppIconUri(packageName: String, versionCode: Int): String =
    "$SCHEME://$packageName/$versionCode"

/**
 * Extract the icon of the installed app
 *
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

    @WorkerThread
    override suspend fun fetch(): FetchResult = FetchResult(
        AppIconDataSource(sketch, request, LOCAL, packageName, versionCode),
        MIME_TYPE
    )

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): AppIconUriFetcher? {
            val uri = request.uri
            return ifOrNull(SCHEME.equals(uri.scheme, ignoreCase = true)) {
                val packageName = uri.authority
                    ?.takeIf { it.isNotEmpty() && it.isNotBlank() }
                    ?: throw UriInvalidException("App icon uri 'packageName' part invalid: ${request.uriString}")
                val versionCode = uri.lastPathSegment
                    ?.takeIf { it.isNotEmpty() && it.isNotBlank() }
                    ?.toIntOrNull()
                    ?: throw UriInvalidException("App icon uri 'versionCode' part invalid: ${request.uriString}")
                AppIconUriFetcher(sketch, request, packageName, versionCode)
            }
        }

        override fun toString(): String = "AppIconUriFetcher"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
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

        override fun newInputStream(): InputStream =
            throw UnsupportedOperationException("Please configure AppIconBitmapDecoder")

        override fun toString(): String =
            "AppIconDataSource(packageName='$packageName',versionCode=$versionCode)"
    }
}