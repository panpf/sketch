package com.github.panpf.sketch.sample.data

import android.content.ContentResolver
import android.content.ContentUris
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class GalleryLocalPhotoListRepo actual constructor(val sketch: Sketch) : LocalPhotoListRepo {

    actual override suspend fun loadLocalPhotoList(
        pageStart: Int,
        pageSize: Int
    ): List<Photo> = withContext(Dispatchers.IO) {
        val context = sketch.context
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED
        )
        val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val queryArgs = Bundle().apply {
                putInt(ContentResolver.QUERY_ARG_OFFSET, pageStart)
                putInt(ContentResolver.QUERY_ARG_LIMIT, pageSize)
                putStringArray(
                    ContentResolver.QUERY_ARG_SORT_COLUMNS,
                    arrayOf(MediaStore.Files.FileColumns.DATE_ADDED)
                )
                putInt(
                    ContentResolver.QUERY_ARG_SORT_DIRECTION,
                    ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                )
            }
            context.contentResolver.query(
                /* uri = */ contentUri,
                /* projection = */ projection,
                /* queryArgs = */ queryArgs,
                /* cancellationSignal = */ null,
            )
        } else {
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC limit $pageStart,$pageSize"
            context.contentResolver.query(
                /* uri = */ contentUri,
                /* projection = */ projection,
                /* selection = */ null,
                /* selectionArgs = */ null,
                /* sortOrder = */ sortOrder
            )
        } ?: return@withContext emptyList()
        if (cursor.count == 0) {
            return@withContext emptyList()
        }

        cursor.use {
            mutableListOf<Photo>().apply {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val imageUri = ContentUris.withAppendedId(contentUri, id)
                    val photo = photoUri2PhotoInfo(sketch, imageUri.toString())
                    add(photo)
                }
            }
        }
    }
}