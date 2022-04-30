package com.github.panpf.sketch.decode

import android.content.pm.PackageManager
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.internal.applyResize
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestExtras
import com.github.panpf.sketch.util.toBitmap
import java.io.IOException

class ApkIconBitmapDecoder(
    private val request: ImageRequest,
    private val fetchResult: FetchResult
) : BitmapDecoder {

    companion object {
        const val MIME_TYPE = "application/vnd.android.package-archive"
    }

    @WorkerThread
    override suspend fun decode(): BitmapDecodeResult {
        val file = fetchResult.dataSource.file()
        val packageManager = request.context.packageManager
        val packageInfo =
            packageManager.getPackageArchiveInfo(file.path, PackageManager.GET_ACTIVITIES)
                ?: throw IOException("getPackageArchiveInfo return null. ${file.path}")
        packageInfo.applicationInfo.sourceDir = file.path
        packageInfo.applicationInfo.publicSourceDir = file.path
        val drawable = packageManager.getApplicationIcon(packageInfo.applicationInfo)
        val bitmap = drawable.toBitmap(bitmapPool = request.sketch.bitmapPool)
        val imageInfo = ImageInfo(bitmap.width, bitmap.height, MIME_TYPE)
        return BitmapDecodeResult(bitmap, imageInfo, ExifInterface.ORIENTATION_UNDEFINED, LOCAL)
            .applyResize(request.sketch, request.resize)
    }

    class Factory : BitmapDecoder.Factory {

        override fun create(
            request: ImageRequest,
            requestExtras: RequestExtras,
            fetchResult: FetchResult
        ): BitmapDecoder? = if (MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)) {
            ApkIconBitmapDecoder(request, fetchResult)
        } else {
            null
        }

        override fun toString(): String = "ApkIconBitmapDecoder"
    }
}