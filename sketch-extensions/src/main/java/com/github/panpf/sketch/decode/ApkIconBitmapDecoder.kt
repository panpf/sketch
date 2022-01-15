package com.github.panpf.sketch.decode

import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.DataFrom.LOCAL
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.readApkIcon

class ApkIconBitmapDecoder(
    val sketch: Sketch,
    val request: LoadRequest,
    val fetchResult: FetchResult
) : BitmapDecoder {

    companion object {
        const val MIME_TYPE = "application/vnd.android.package-archive"
    }

    override suspend fun decodeBitmap(): BitmapDecodeResult {
        val file = fetchResult.dataSource.file()
        val bitmap = readApkIcon(
            sketch.appContext,
            file.path,
            false,
            sketch.bitmapPoolHelper
        )
        val imageInfo = ImageInfo(
            MIME_TYPE,
            bitmap.width,
            bitmap.height,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        return BitmapDecodeResult(bitmap, imageInfo, LOCAL, true)
    }

    override fun close() {

    }

    class Factory : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch, request: LoadRequest, fetchResult: FetchResult
        ): BitmapDecoder? = if (MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)) {
            ApkIconBitmapDecoder(sketch, request, fetchResult)
        } else {
            null
        }
    }
}