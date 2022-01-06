package com.github.panpf.sketch.fetch

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.internal.AbsBitmapDiskCacheFetcher
import com.github.panpf.sketch.request.DataFrom.LOCAL
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.UriInvalidException
import com.github.panpf.sketch.util.readApkIcon

fun newAppIconUri(packageName: String, versionCode: Int): Uri =
    AppIconUriFetcher.newUri(packageName, versionCode)

/**
 * Support 'apk.icon://com.github.panpf.sketch.sample' uri
 */
class AppIconUriFetcher(
    sketch: Sketch,
    request: LoadRequest,
    val packageName: String,
    val versionCode: Int,
) : AbsBitmapDiskCacheFetcher(sketch, request, LOCAL) {

    companion object {
        const val SCHEME = "app.icon"

        @JvmStatic
        fun newUri(packageName: String, versionCode: Int): Uri =
            Uri.parse("$SCHEME://$packageName/$versionCode")
    }

    override fun getBitmap(): Bitmap {
        val packageInfo: PackageInfo = try {
            sketch.appContext.packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            throw Exception("Not found PackageInfo by '$packageName'. ${request.uriString}", e)
        }
        if (packageInfo.versionCode != versionCode) {
            throw Exception("App versionCode mismatch, ${packageInfo.versionCode} != $versionCode. ${request.uriString}")
        }
        val apkFilePath = packageInfo.applicationInfo.sourceDir
        val bitmapPool = sketch.bitmapPoolHelper.bitmapPool
        return readApkIcon(sketch.appContext, apkFilePath, false, bitmapPool)
    }

    override fun getDiskCacheKey(): String = request.uriString

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): AppIconUriFetcher? {
            val uri = request.uri
            return if (request is LoadRequest && uri.scheme == "app.icon") {
                val packageName = uri.authority
                    ?: throw UriInvalidException(request, "App icon uri 'packageName' part invalid. ${request.uriString}")
                val versionCode = uri.lastPathSegment?.toIntOrNull()
                    ?: throw UriInvalidException(request, "App icon uri 'versionCode' part invalid. ${request.uriString}")
                AppIconUriFetcher(sketch, request, packageName, versionCode)
            } else {
                null
            }
        }
    }
}