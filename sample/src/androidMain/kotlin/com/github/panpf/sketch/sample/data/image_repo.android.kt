package com.github.panpf.sketch.sample.data

import android.graphics.RectF
import android.provider.MediaStore.Images.Media
import androidx.core.content.PermissionChecker
import com.caverock.androidsvg.SVG
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.decode.internal.readExifOrientation
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrThrow
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.tools4k.coroutines.withToIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer

actual suspend fun localImages(
    context: PlatformContext,
    startPosition: Int,
    pageSize: Int
): List<String> {
    val checkSelfPermission = PermissionChecker
        .checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
    if (checkSelfPermission != PermissionChecker.PERMISSION_GRANTED) {
        return emptyList()
    }
    return withToIO {
        val cursor = context.contentResolver.query(
            /* uri = */ Media.EXTERNAL_CONTENT_URI,
            /* projection = */
            arrayOf(
                Media.TITLE,
                Media.DATA,
                Media.SIZE,
                Media.DATE_TAKEN,
            ),
            /* selection = */
            null,
            /* selectionArgs = */
            null,
            /* sortOrder = */
            Media.DATE_TAKEN + " DESC" + " limit " + startPosition + "," + pageSize
        )
        ArrayList<String>(cursor?.count ?: 0).apply {
            cursor?.use {
                while (cursor.moveToNext()) {
                    val uri =
                        cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA))
                    add(uri)
                }
            }
        }
    }
}

actual suspend fun readImageInfoOrNull(
    context: PlatformContext,
    sketch: Sketch,
    uri: String,
): ImageInfo? = withContext(Dispatchers.IO) {
    runCatching {
        val fetcher = sketch.components.newFetcherOrThrow(ImageRequest(context, uri))
        val dataSource = fetcher.fetch().getOrThrow().dataSource
        if (uri.endsWith(".svg")) {
            dataSource.readSVGImageInfo()
        } else {
            val imageInfo = dataSource.readImageInfoWithBitmapFactoryOrThrow()
            val exifOrientation = dataSource.readExifOrientation()
            val exifOrientationHelper = ExifOrientationHelper(exifOrientation)
            val newSize = exifOrientationHelper.applyToSize(imageInfo.size)
            imageInfo.copy(size = newSize)
        }
    }.apply {
        if (isFailure) {
            exceptionOrNull()?.printStackTrace()
        }
    }.getOrNull()
}

private fun DataSource.readSVGImageInfo(useViewBoundsAsIntrinsicSize: Boolean = true): ImageInfo {
    val svg = openSource().buffer().inputStream().use { SVG.getFromInputStream(it) }
    val width: Int
    val height: Int
    val viewBox: RectF? = svg.documentViewBox
    if (useViewBoundsAsIntrinsicSize && viewBox != null) {
        width = viewBox.width().toInt()
        height = viewBox.height().toInt()
    } else {
        width = svg.documentWidth.toInt()
        height = svg.documentHeight.toInt()
    }
    return ImageInfo(width = width, height = height, mimeType = SvgDecoder.MIME_TYPE)
}