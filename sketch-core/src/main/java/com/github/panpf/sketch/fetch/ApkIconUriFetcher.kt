package com.github.panpf.sketch.fetch

import android.graphics.Bitmap
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.internal.AbsBitmapDiskCacheFetcher
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.util.createFileUriDiskCacheKey
import com.github.panpf.sketch.util.readApkIcon

/**
 * Support 'apk.icon:///sdcard/test.apk' uri
 */
class ApkIconUriFetcher(
    sketch: Sketch,
    request: LoadRequest,
    val apkFilePath: String,
) : AbsBitmapDiskCacheFetcher(sketch, request) {

    companion object {
        const val MODULE = "ApkIconUriFetcher"
        const val SCHEME = "apk.icon"

        @JvmStatic
        fun makeUri(filePath: String): String {
            return "$SCHEME://$filePath"
        }
    }

    override fun getBitmap(): Bitmap = readApkIcon(
        sketch.appContext,
        apkFilePath,
        false,
        sketch.bitmapPoolHelper.bitmapPool
    )

    override fun getDiskCacheKey(): String =
        createFileUriDiskCacheKey(request.uriString, apkFilePath)

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): ApkIconUriFetcher? =
            if (request is LoadRequest && request.uri.scheme == SCHEME) {
                val apkFilePath = request.uriString.substring(("${SCHEME}://").length)
                ApkIconUriFetcher(sketch, request, apkFilePath)
            } else {
                null
            }
    }
}