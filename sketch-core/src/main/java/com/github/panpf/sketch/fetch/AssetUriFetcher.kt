package com.github.panpf.sketch.fetch

import android.net.Uri
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetsDataSource
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest

fun newAssetUri(assetFilePath: String): Uri = AssetUriFetcher.newUri(assetFilePath)

/**
 * Support 'asset://test.png' uri
 */
class AssetUriFetcher(
    val sketch: Sketch,
    val request: LoadRequest,
    val assetFileName: String
) : Fetcher {

    companion object {
        const val SCHEME = "asset"

        @JvmStatic
        fun newUri(assetFilePath: String): Uri = Uri.parse("$SCHEME://$assetFilePath")
    }

    override suspend fun fetch(): FetchResult =
        FetchResult(AssetsDataSource(sketch, request, assetFileName))

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): AssetUriFetcher? =
            if (request is LoadRequest && SCHEME.equals(request.uri.scheme, ignoreCase = true)) {
                val assetFileName = request.uriString.substring(("$SCHEME://").length)
                AssetUriFetcher(sketch, request, assetFileName)
            } else {
                null
            }
    }
}