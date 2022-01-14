package com.github.panpf.sketch.fetch

import android.net.Uri
import android.util.Base64
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.internal.AbsStreamDiskCacheFetcher
import com.github.panpf.sketch.request.DataFrom.MEMORY
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.UriInvalidException
import com.github.panpf.sketch.util.MD5Utils
import java.io.ByteArrayInputStream
import java.io.InputStream

fun newBase64Uri(mimeType: String, imageDataBase64String: String): Uri =
    Base64UriFetcher.newUri(mimeType, imageDataBase64String)

/**
 * Support 'data:image/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z', 'data:img/jpeg;base64,/9j/4QaORX...C8bg/U7T/in//Z' uri
 */
class Base64UriFetcher(
    sketch: Sketch,
    request: LoadRequest,
    override val mimeType: String,
    val imageDataBase64StringLazy: Lazy<String>,
) : AbsStreamDiskCacheFetcher(sketch, request, MEMORY) {

    companion object {
        const val SCHEME = "data"
        const val BASE64_IDENTIFIER = "base64,"

        @JvmStatic
        fun newUri(mimeType: String, imageDataBase64String: String): Uri =
            Uri.parse("$SCHEME:$mimeType;base64,$imageDataBase64String")
    }

    override fun openInputStream(): InputStream {
        return ByteArrayInputStream(Base64.decode(imageDataBase64StringLazy.value, Base64.DEFAULT))
    }

    override fun getDiskCacheKey(): String = MD5Utils.md5(request.uriString)

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): Base64UriFetcher? =
            if (request is LoadRequest && SCHEME.equals(request.uri.scheme, ignoreCase = true)) {
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
                    throw UriInvalidException(request, "Base64 image invalid")
                }
            } else {
                null
            }
    }
}