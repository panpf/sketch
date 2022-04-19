package com.github.panpf.sketch.decode

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.internal.applyResize
import com.github.panpf.sketch.fetch.AppIconUriFetcher
import com.github.panpf.sketch.fetch.AppIconUriFetcher.AppIconDataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestExtras
import com.github.panpf.sketch.util.toBitmap

class AppIconBitmapDecoder(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val packageName: String,
    private val versionCode: Int,
) : BitmapDecoder {

    @WorkerThread
    override suspend fun decode(): BitmapDecodeResult {
        val packageManager = sketch.context.packageManager
        val packageInfo: PackageInfo = try {
            packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            throw Exception("Not found PackageInfo by '$packageName'", e)
        }
        @Suppress("DEPRECATION")
        if (packageInfo.versionCode != versionCode) {
            throw Exception("App versionCode mismatch, ${packageInfo.versionCode} != $versionCode")
        }
        val iconDrawable = packageInfo.applicationInfo.loadIcon(packageManager)
            ?: throw Exception("loadIcon return null '$packageName'")
        val bitmap = iconDrawable.toBitmap(bitmapPool = sketch.bitmapPool)
        val imageInfo = ImageInfo(
            bitmap.width,
            bitmap.height,
            AppIconUriFetcher.MIME_TYPE,
        )
        return BitmapDecodeResult(
            bitmap,
            imageInfo,
            ExifInterface.ORIENTATION_UNDEFINED,
            LOCAL
        ).applyResize(sketch.bitmapPool, request.resize)
    }

    class Factory : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestExtras: RequestExtras,
            fetchResult: FetchResult
        ): BitmapDecoder? {
            val dataSource = fetchResult.dataSource
            return if (
                AppIconUriFetcher.MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)
                && dataSource is AppIconDataSource
            ) {
                AppIconBitmapDecoder(
                    sketch, request, dataSource.packageName, dataSource.versionCode
                )
            } else {
                null
            }
        }

        override fun toString(): String = "AppIconBitmapDecoder"
    }
}