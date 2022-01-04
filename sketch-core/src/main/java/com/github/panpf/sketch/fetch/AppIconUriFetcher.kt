package com.github.panpf.sketch.fetch

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.internal.AbsBitmapDiskCacheFetcher
import com.github.panpf.sketch.request.LoadException
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.util.SLog
import com.github.panpf.sketch.util.readApkIcon

/**
 * Support 'apk.icon://com.github.panpf.sketch.sample' uri
 */
class AppIconUriFetcher(
    sketch: Sketch,
    request: LoadRequest,
    val packageName: String,
    val versionCode: Int,
) : AbsBitmapDiskCacheFetcher(sketch, request) {

    companion object {
        const val MODULE = "AppIconUriFetcher"
        const val SCHEME = "app.icon"

        @JvmStatic
        fun makeUri(packageName: String, versionCode: Int): String {
            return "$SCHEME://$packageName/$versionCode"
        }
    }

    override fun getBitmap(): Bitmap {
        val packageInfo: PackageInfo = try {
            sketch.appContext.packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            val cause = "Not found PackageInfo by '$packageName'. ${request.uriString}"
            SLog.emt(MODULE, e, cause)
            throw LoadException(cause, e)
        }
        if (packageInfo.versionCode != versionCode) {
            val cause =
                "App versionCode mismatch, ${packageInfo.versionCode} != ${versionCode}. ${request.uriString}"
            SLog.em(MODULE, cause)
            throw LoadException(cause)
        }
        val apkFilePath = packageInfo.applicationInfo.sourceDir
        val bitmapPool = sketch.bitmapPoolHelper.bitmapPool
        val iconBitmap = readApkIcon(sketch.appContext, apkFilePath, false, MODULE, bitmapPool)
        if (iconBitmap == null || iconBitmap.isRecycled) {
            val cause = "App icon bitmap invalid. ${request.uriString}"
            SLog.em(MODULE, cause)
            throw LoadException(cause)
        }
        return iconBitmap
    }

    override fun getDiskCacheKey(): String = request.uriString

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): AppIconUriFetcher? {
            val uri = request.uri
            return if (request is LoadRequest && uri.scheme == "app.icon") {
                val packageName = uri.authority
                    ?: throw LoadException("App icon uri 'packageName' part invalid. ${request.uriString}")
                val versionCode = uri.lastPathSegment?.toIntOrNull()
                    ?: throw LoadException("App icon uri 'versionCode' part invalid. ${request.uriString}")
                AppIconUriFetcher(sketch, request, packageName, versionCode)
            } else {
                null
            }
        }
    }
}