package com.github.panpf.sketch.sample.data.paging

import android.graphics.RectF
import android.provider.MediaStore
import androidx.core.content.PermissionChecker
import com.caverock.androidsvg.SVG
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.internal.readImageInfoWithBitmapFactoryOrThrow
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.tools4k.coroutines.withToIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer

actual suspend fun readPhotosFromPhotoAlbum(
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
            /* uri = */ MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            /* projection = */
            arrayOf(
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_TAKEN,
            ),
            /* selection = */
            null,
            /* selectionArgs = */
            null,
            /* sortOrder = */
            MediaStore.Images.Media.DATE_TAKEN + " DESC" + " limit " + startPosition + "," + pageSize
        )
        ArrayList<String>(cursor?.count ?: 0).apply {
            cursor?.use {
                while (cursor.moveToNext()) {
                    val uri =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
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
    ignoreExifOrientation: Boolean
): ImageInfo? = withContext(Dispatchers.IO) {
    runCatching {
        val fetcher = sketch.components.newFetcherOrThrow(ImageRequest(context, uri))
        val dataSource = fetcher.fetch().getOrThrow().dataSource
        if (uri.endsWith(".svg")) {
            dataSource.readSVGImageInfo()
        } else {
            dataSource.readImageInfoWithBitmapFactoryOrThrow(ignoreExifOrientation)
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
    return ImageInfo(
        width,
        height,
        SvgDecoder.MIME_TYPE,
        ExifOrientation.UNDEFINED
    )
}
