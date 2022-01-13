package com.github.panpf.sketch.fetch

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DrawableResDataSource
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.UriInvalidException

fun newDrawableResUri(@DrawableRes drawableResId: Int): Uri =
    DrawableResUriFetcher.newUri(drawableResId)

/**
 * Support 'drawable.resource://5353453' uri
 */
class DrawableResUriFetcher(
    val sketch: Sketch,
    val request: LoadRequest,
    @RawRes @DrawableRes val drawableResId: Int
) : Fetcher {

    companion object {
        const val SCHEME = "drawable.resource"

        @JvmStatic
        fun newUri(@DrawableRes drawableResId: Int): Uri = Uri.parse("$SCHEME://$drawableResId")
    }

    override suspend fun fetch(): FetchResult =
        FetchResult(DrawableResDataSource(sketch, request, drawableResId))

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): DrawableResUriFetcher? {
            val uri = request.uri
            return if (request is LoadRequest && SCHEME.equals(uri.scheme, ignoreCase = true)) {
                val drawableResId = uri.authority?.toIntOrNull()
                    ?: throw UriInvalidException(request, "Drawable resource uri 'drawableResId' part invalid. ${request.uriString}")
                DrawableResUriFetcher(sketch, request, drawableResId)
            } else {
                null
            }
        }
    }
}