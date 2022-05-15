package com.github.panpf.sketch.fetch

import android.util.Base64
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DataFrom.MEMORY
import com.github.panpf.sketch.fetch.Base64UriFetcher.Companion.BASE64_IDENTIFIER
import com.github.panpf.sketch.fetch.Base64UriFetcher.Companion.SCHEME
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.UriInvalidException

/**
 * 'data:image/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z', 'data:img/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z' uri
 */
fun newBase64Uri(mimeType: String, imageDataBase64String: String): String =
    "$SCHEME:$mimeType;${BASE64_IDENTIFIER}$imageDataBase64String"

/**
 * Support 'data:image/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z', 'data:img/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z' uri
 */
class Base64UriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val mimeType: String,
    val imageDataBase64StringLazy: Lazy<String>,
) : Fetcher {

    companion object {
        const val SCHEME = "data"
        const val BASE64_IDENTIFIER = "base64,"
    }

    override suspend fun fetch(): FetchResult {
        val bytes = Base64.decode(imageDataBase64StringLazy.value, Base64.DEFAULT)
        return FetchResult(ByteArrayDataSource(sketch, request, MEMORY, bytes), mimeType)
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): Base64UriFetcher? =
            if (SCHEME.equals(request.uri.scheme, ignoreCase = true)) {
                val base64ImageString = request.uriString
                val mimeTypeStartSymbolIndex = base64ImageString.indexOf(":")
                val mimeTypeEndSymbolIndex = base64ImageString.indexOf(";")
                val base64IdentifierIndex = base64ImageString.indexOf(BASE64_IDENTIFIER)
                if (mimeTypeStartSymbolIndex != -1 && mimeTypeEndSymbolIndex != -1 && base64IdentifierIndex != -1) {
                    val mimeType = base64ImageString.substring(
                        mimeTypeStartSymbolIndex + 1,
                        mimeTypeEndSymbolIndex
                    )
                    Base64UriFetcher(sketch, request, mimeType, lazy {
                        base64ImageString.substring(base64IdentifierIndex + BASE64_IDENTIFIER.length)
                    })
                } else {
                    throw UriInvalidException(request, "Invalid base64 image")
                }
            } else {
                null
            }

        override fun toString(): String = "Base64UriFetcher"

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