package com.github.panpf.sketch.fetch

import android.util.Base64
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.internal.AbsStreamDiskCacheFetcher
import com.github.panpf.sketch.LoadException
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Support 'data:image/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z', 'data:img/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z' uri
 */
class Base64UriFetcher(
    sketch: Sketch,
    request: LoadRequest,
    val mimeType: String,
    val imageDataBase64String: String,
) : AbsStreamDiskCacheFetcher(sketch, request) {

    companion object {
        const val SCHEME = "data"
        const val BASE64_IDENTIFIER = "base64,"

        @JvmStatic
        fun makeUri(mimeType: String, imageDataBase64String: String): String =
            "$SCHEME:$mimeType;base64,$imageDataBase64String"
    }

    override fun openInputStream(): InputStream {
        return ByteArrayInputStream(Base64.decode(imageDataBase64String, Base64.DEFAULT))
    }

    override fun getDiskCacheKey(): String = imageDataBase64String

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): Base64UriFetcher? =
            if (request is LoadRequest && request.uri.scheme == SCHEME) {
                val base64ImageString = request.uriString
                val mimeTypeStartSymbolIndex = base64ImageString.indexOf(":")
                val mimeTypeEndSymbolIndex = base64ImageString.indexOf(";")
                val base64IdentifierIndex = base64ImageString.indexOf(BASE64_IDENTIFIER)
                if (mimeTypeStartSymbolIndex != -1 && mimeTypeEndSymbolIndex != -1 && base64IdentifierIndex != -1) {
                    val mimeType = base64ImageString.substring(
                        mimeTypeStartSymbolIndex + 1,
                        mimeTypeEndSymbolIndex
                    )
                    val imageDataBase64String =
                        base64ImageString.substring(base64IdentifierIndex + BASE64_IDENTIFIER.length)
                    Base64UriFetcher(sketch, request, mimeType, imageDataBase64String)
                } else {
                    throw LoadException("Base64 image invalid. ${request.uriString}")
                }
            } else {
                null
            }
    }
}