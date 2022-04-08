package com.github.panpf.sketch.decode

import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.internal.applyResize
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestExtras
import com.github.panpf.sketch.util.readApkIcon

class ApkIconBitmapDecoder(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val fetchResult: FetchResult
) : BitmapDecoder {

    companion object {
        const val MIME_TYPE = "application/vnd.android.package-archive"
    }

    @WorkerThread
    override suspend fun decode(): BitmapDecodeResult {
        val file = fetchResult.dataSource.file()
        val bitmap = readApkIcon(
            sketch.context,
            file.path,
            false,
            sketch.bitmapPool
        )
        val imageInfo = ImageInfo(bitmap.width, bitmap.height, MIME_TYPE)
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
        ): BitmapDecoder? = if (MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)) {
            ApkIconBitmapDecoder(sketch, request, fetchResult)
        } else {
            null
        }

        override fun toString(): String = "ApkIconBitmapDecoder"
    }
}