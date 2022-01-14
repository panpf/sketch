//package com.github.panpf.sketch.decode.internal
//
//import androidx.exifinterface.media.ExifInterface
//import com.github.panpf.sketch.Sketch
//import com.github.panpf.sketch.datasource.FileDataSource
//import com.github.panpf.sketch.decode.BitmapDecodeResult
//import com.github.panpf.sketch.decode.BitmapDecoder
//import com.github.panpf.sketch.decode.ImageInfo
//import com.github.panpf.sketch.fetch.FetchResult
//import com.github.panpf.sketch.request.DataFrom.LOCAL
//import com.github.panpf.sketch.request.LoadRequest
//import com.github.panpf.sketch.util.readApkIcon
//import java.io.File
// todo ApkIconBitmapDecoder 完善
//class ApkIconBitmapDecoder(
//    val sketch: Sketch,
//    val request: LoadRequest,
//    val apkFile: File
//) : BitmapDecoder {
//
//    override suspend fun decodeBitmap(): BitmapDecodeResult {
//        val bitmap = readApkIcon(
//            sketch.appContext,
//            apkFile.path,
//            false,
//            sketch.bitmapPoolHelper.bitmapPool
//        )
//        val imageInfo = ImageInfo(
//            "image/png",
//            bitmap.width,
//            bitmap.height,
//            ExifInterface.ORIENTATION_UNDEFINED
//        )
//        return BitmapDecodeResult(bitmap, imageInfo, LOCAL, true)
//    }
//
//    override fun close() {
//
//    }
//
//    class Factory : BitmapDecoder.Factory {
//
//        override fun create(
//            sketch: Sketch,
//            request: LoadRequest,
//            fetchResult: FetchResult
//        ): BitmapDecoder? {
//            val dataSource = fetchResult.dataSource
//            return if (fetchResult.mimeType == "application/vnd.android.package-archive" && dataSource is FileDataSource) {
//                ApkIconBitmapDecoder(sketch, request, dataSource.file)
//            } else {
//                null
//            }
//        }
//    }
//}