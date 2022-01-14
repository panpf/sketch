package com.github.panpf.sketch.fetch

import android.graphics.Bitmap
import android.net.Uri
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.internal.AbsBitmapDiskCacheFetcher
import com.github.panpf.sketch.request.DataFrom.LOCAL
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.util.createFileUriDiskCacheKey
import com.github.panpf.sketch.util.readApkIcon

fun newApkIconUri(filePath: String): Uri = ApkIconUriFetcher.newUri(filePath)

/**
 * Support 'apk.icon:///sdcard/test.apk' uri
 */
class ApkIconUriFetcher(
    sketch: Sketch,
    request: LoadRequest,
    val apkFilePath: String,
) : AbsBitmapDiskCacheFetcher(sketch, request, LOCAL) {

    companion object {
        const val SCHEME = "apk.icon"

        @JvmStatic
        fun newUri(filePath: String): Uri = Uri.parse("$SCHEME://$filePath")
    }

    override val mimeType: String
        get() = "application/vnd.android.package-archive"

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
            if (request is LoadRequest && SCHEME.equals(request.uri.scheme, ignoreCase = true)) {
                val apkFilePath = request.uriString.substring(("${SCHEME}://").length)
                ApkIconUriFetcher(sketch, request, apkFilePath)
            } else {
                null
            }
    }
}