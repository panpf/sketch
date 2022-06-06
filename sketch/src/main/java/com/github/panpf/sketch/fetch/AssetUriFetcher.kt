package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
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
    val sketch: Sketch,
    val request: ImageRequest,
    val assetFileName: String
) : Fetcher {

    companion object {
        const val SCHEME = "asset"
    }

    override suspend fun fetch(): FetchResult {
        val mimeType = getMimeTypeFromUrl(assetFileName)
        return FetchResult(AssetDataSource(sketch, request, assetFileName), mimeType)
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): AssetUriFetcher? =
            if (SCHEME.equals(request.uri.scheme, ignoreCase = true)) {
                val uriString = request.uriString
                val subStartIndex = SCHEME.length + 3
                val subEndIndex = uriString.indexOf("?").takeIf { it != -1 }
                    ?: uriString.indexOf("#").takeIf { it != -1 }
                    ?: uriString.length
                val assetFileName = uriString.substring(subStartIndex, subEndIndex)
                AssetUriFetcher(sketch, request, assetFileName)
            } else {
                null
            }

        override fun toString(): String = "AssetUriFetcher"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}