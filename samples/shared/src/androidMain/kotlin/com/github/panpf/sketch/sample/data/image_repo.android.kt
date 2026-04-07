package com.github.panpf.sketch.sample.data

import android.content.ContentUris
import android.provider.MediaStore
import com.github.panpf.sketch.PlatformContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun localImages(context: PlatformContext): List<String> =
    withContext(Dispatchers.IO) {
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )
        val cursor = context.contentResolver.query(
            /* uri = */ contentUri,
            /* projection = */ projection,
            /* selection = */ null,
            /* selectionArgs = */ null,
            /* sortOrder = */ MediaStore.Images.Media.DATE_ADDED + " DESC"
        ) ?: return@withContext emptyList<String>()
        if (cursor.count == 0) {
            return@withContext emptyList()
        }

        cursor.use {
            mutableListOf<String>().apply {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val imageUri = ContentUris.withAppendedId(contentUri, id)
                    add(imageUri.toString())
                }
            }
        }
    }