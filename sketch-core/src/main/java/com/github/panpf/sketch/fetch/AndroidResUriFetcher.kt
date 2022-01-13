package com.github.panpf.sketch.fetch

import android.net.Uri
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest

fun newAndroidResUriByName(packageName: String, resType: String, drawableResName: String): Uri =
    AndroidResUriFetcher.newUriByName(packageName, resType, drawableResName)

fun newAndroidResUriById(packageName: String, drawableResId: Int): Uri =
    AndroidResUriFetcher.newUriById(packageName, drawableResId)

/**
 * Support 'android.resource://com.github.panpf.sketch.sample/mipmap/ic_launch' uri
 */
class AndroidResUriFetcher(
    val sketch: Sketch,
    val request: LoadRequest,
    val contentUri: Uri,
) : Fetcher {

    companion object {

        const val SCHEME = "android.resource"

        /**
         * Sample: 'android.resource://com.github.panpf.sketch.sample/mipmap/ic_launch'
         */
        @JvmStatic
        fun newUriByName(packageName: String, resType: String, drawableResName: String): Uri =
            Uri.parse("$SCHEME://$packageName/$resType/$drawableResName")

        /**
         * Sample: 'android.resource://com.github.panpf.sketch.sample/1031232'
         */
        @JvmStatic
        fun newUriById(packageName: String, drawableResId: Int): Uri {
            return Uri.parse("$SCHEME://$packageName/$drawableResId")
        }
    }

    override suspend fun fetch(): FetchResult =
        FetchResult(ContentDataSource(sketch, request, contentUri))

    class Factory : Fetcher.Factory {
        override fun create(
            sketch: Sketch, request: ImageRequest
        ): AndroidResUriFetcher? =
            if (request is LoadRequest && SCHEME.equals(request.uri.scheme, ignoreCase = true)) {
                AndroidResUriFetcher(sketch, request, request.uri)
            } else {
                null
            }
    }
}