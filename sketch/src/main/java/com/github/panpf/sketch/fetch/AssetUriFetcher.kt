package com.github.panpf.sketch.fetch

import android.webkit.MimeTypeMap
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.fetch.AssetUriFetcher.Companion.SCHEME
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.getMimeTypeFromUrl

/**
 * Sample: 'asset://test.png'
 */
fun newAssetUri(assetFilePath: String): String = "$SCHEME://$assetFilePath"

/**
 * Support 'asset://test.png' uri
 */
class AssetUriFetcher(
    val request: ImageRequest,
    val assetFileName: String
) : Fetcher {

    companion object {
        const val SCHEME = "asset"
    }

    override suspend fun fetch(): FetchResult {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromUrl(assetFileName)
        return FetchResult(AssetDataSource(request, assetFileName), mimeType)
    }

    class Factory : Fetcher.Factory {
        override fun create(request: ImageRequest): AssetUriFetcher? =
            if (SCHEME.equals(request.uri.scheme, ignoreCase = true)) {
                val uriString = request.uriString
                val subStartIndex = SCHEME.length + 3
                val subEndIndex = uriString.indexOf("?").takeIf { it != -1 }
                    ?: uriString.indexOf("#").takeIf { it != -1 }
                    ?: uriString.length
                val assetFileName = uriString.substring(subStartIndex, subEndIndex)
                AssetUriFetcher(request, assetFileName)
            } else {
                null
            }

        override fun toString(): String = "AssetUriFetcher"
    }
}