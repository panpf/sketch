package com.github.panpf.sketch.sample.data

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.isContentUri
import com.github.panpf.sketch.fetch.isFileUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.sample.image.photoUri2PhotoInfo
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.util.checkPermissionGranted
import com.github.panpf.sketch.sample.util.md5
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toUri
import com.github.panpf.tools4a.fileprovider.ktx.getShareFileUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import java.io.File

actual class PhotoService actual constructor(val sketch: Sketch) {

    actual suspend fun loadFromGallery(
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
            val sortOrder =
                "${MediaStore.Images.Media.DATE_ADDED} DESC limit $pageStart,$pageSize"
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

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    actual suspend fun saveToGallery(imageUri: String): Result<String?> {
        val uri = imageUri.toUri()
        if (isContentUri(uri) || isFileUri(uri)) {
            return Result.failure(Exception("Local photos do not need to be saved to the gallery"))
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            savePhotoToGalleryWithMediaStore(sketch.context, imageUri)
        } else if (checkPermissionGranted(
                context = sketch.context,
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            savePhotoToGalleryWithDirectory(imageUri)
        } else {
            Result.failure(Exception("Storage permission was not granted"))
        }
    }

    actual suspend fun share(imageUri: String): Result<String?> {
        val fetchResultResult = withContext(Dispatchers.IO) {
            val request = ImageRequest(sketch.context, imageUri)
            val requestContext = RequestContext(sketch, request, Size.Empty)
            val fetcher = sketch.components.newFetcherOrThrow(requestContext)
            fetcher.fetch()
        }
        if (fetchResultResult.isFailure) {
            return Result.failure(fetchResultResult.exceptionOrNull()!!)
        }
        val fetchResult = fetchResultResult.getOrThrow()

        val fileExtension = MimeTypeMap.getExtensionFromUrl(imageUri)
            ?: MimeTypeMap.getExtensionFromMimeType(fetchResult.mimeType ?: "")
            ?: "jpeg"
        val shareTempDir = sketch.context.getExternalFilesDir("share")
        val imageFile = File(shareTempDir, "share_temp.$fileExtension")
        imageFile.delete()
        val result = withContext(Dispatchers.IO) {
            runCatching {
                fetchResult.dataSource.openSource().buffer().use { input ->
                    imageFile.outputStream().sink().buffer().use { output ->
                        output.writeAll(input)
                    }
                }
            }
        }
        if (result.isFailure) {
            return Result.failure(result.exceptionOrNull()!!)
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, sketch.context.getShareFileUri(imageFile))
            type = fetchResult.mimeType ?: "image/*"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        sketch.context.startActivity(shareIntent)
        return Result.success(null)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun savePhotoToGalleryWithMediaStore(
        context: Context,
        imageUri: String
    ): Result<String?> {
        val fetchResultResult = withContext(Dispatchers.IO) {
            val request = ImageRequest(sketch.context, imageUri)
            val requestContext = RequestContext(sketch, request, Size.Empty)
            val fetcher = sketch.components.newFetcherOrThrow(requestContext)
            fetcher.fetch()
        }
        if (fetchResultResult.isFailure) {
            return Result.failure(fetchResultResult.exceptionOrNull()!!)
        }
        val fetchResult = fetchResultResult.getOrThrow()

        val resolver = context.contentResolver
        val values = ContentValues().apply {
            val extension = MimeTypeMap.getExtensionFromMimeType(fetchResult.mimeType ?: "")
                ?: MimeTypeMap.getExtensionFromUrl(imageUri)
                ?: "jpeg"
            val mimeType = "image/$extension"
            val fileName = "${imageUri.md5()}.$extension"
            val relativePath = Environment.DIRECTORY_PICTURES + "/sketch"
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        val saveResult = withContext(Dispatchers.IO) {
            runCatching {
                fetchResult.dataSource.openSource().buffer().use { input ->
                    resolver.openOutputStream(uri)!!.sink().buffer().use { output ->
                        output.writeAll(input)
                    }
                }
            }
        }
        return if (saveResult.isSuccess) {
            Result.success(null)
        } else {
            Result.failure(saveResult.exceptionOrNull()!!)
        }
    }

    @Suppress("DEPRECATION")
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    suspend fun savePhotoToGalleryWithDirectory(imageUri: String): Result<String?> {
        val fetchResultResult = withContext(Dispatchers.IO) {
            val request = ImageRequest(sketch.context, imageUri)
            val requestContext = RequestContext(sketch, request, Size.Empty)
            val fetcher = sketch.components.newFetcherOrThrow(requestContext)
            fetcher.fetch()
        }
        if (fetchResultResult.isFailure) {
            return Result.failure(fetchResultResult.exceptionOrNull()!!)
        }
        val fetchResult = fetchResultResult.getOrThrow()

        val picturesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val saveDir = File(picturesDir, "sketch")
        saveDir.mkdirs()
        val extension = MimeTypeMap.getExtensionFromMimeType(fetchResult.mimeType ?: "")
            ?: MimeTypeMap.getExtensionFromUrl(imageUri)
            ?: "jpeg"
        val fileName = "${imageUri.md5()}.$extension"
        val saveFile = File(saveDir, fileName)
        val saveResult = withContext(Dispatchers.IO) {
            runCatching {
                fetchResult.dataSource.openSource().buffer().use { input ->
                    saveFile.outputStream().sink().buffer().use { output ->
                        output.writeAll(input)
                    }
                }
            }
        }
        return if (saveResult.isSuccess) {
            MediaScannerConnection.scanFile(
                /* context = */ sketch.context,
                /* paths = */ arrayOf(saveFile.absolutePath),
                /* mimeTypes = */ arrayOf("image/$extension"),
                /* callback = */ null
            )
            Result.success(null)
        } else {
            Result.failure(saveResult.exceptionOrNull()!!)
        }
    }
}