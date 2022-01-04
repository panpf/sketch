package com.github.panpf.sketch.fetch

import android.graphics.Bitmap
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.internal.AbsBitmapDiskCacheFetcher
import com.github.panpf.sketch.request.LoadException
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.util.SLog
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

    override fun getBitmap(): Bitmap {
        val bitmapPoolHelper = sketch.bitmapPoolHelper
        val iconBitmap =
            readApkIcon(sketch.appContext, apkFilePath, false, MODULE, bitmapPoolHelper.bitmapPool)
        if (iconBitmap == null || iconBitmap.isRecycled) {
            val cause = "Apk icon bitmap invalid. ${request.uriString}"
            SLog.em(MODULE, cause)
            throw LoadException(cause)
        }
        return iconBitmap
    }

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