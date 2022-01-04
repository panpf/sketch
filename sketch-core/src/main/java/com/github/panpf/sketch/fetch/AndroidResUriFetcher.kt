package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest

/**
 * Support 'android.resource://com.github.panpf.sketch.sample/mipmap/ic_launch' uri
 */
class AndroidResUriFetcher(
    val sketch: Sketch,
    val request: LoadRequest,
) : Fetcher {

    companion object {

        const val SCHEME = "android.resource"

        /**
         * Sample: 'android.resource://com.github.panpf.sketch.sample/mipmap/ic_launch'
         */
        @JvmStatic
        fun makeUriByName(packageName: String, resType: String, drawableResName: String): String {
            return "$SCHEME://$packageName/$resType/$drawableResName"
        }

        /**
         * Sample: 'android.resource://com.github.panpf.sketch.sample/1031232'
         */
        @JvmStatic
        fun makeUriById(packageName: String, drawableResId: Int): String {
            return "$SCHEME://$packageName/$drawableResId"
        }
    }

    override suspend fun fetch(): FetchResult =
        FetchResult(ContentDataSource(sketch.appContext, request.uri))

    class Factory : Fetcher.Factory {
        override fun create(
            sketch: Sketch, request: ImageRequest
        ): AndroidResUriFetcher? =
            if (request is LoadRequest && request.uri.scheme == SCHEME) {
                AndroidResUriFetcher(sketch, request)
            } else {
                null
            }
    }
}