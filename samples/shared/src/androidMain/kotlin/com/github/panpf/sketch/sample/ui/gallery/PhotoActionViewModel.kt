/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.sample.ui.gallery

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.sample.ui.base.ActionResult
import com.github.panpf.sketch.sample.ui.util.checkPermissionGranted
import com.github.panpf.sketch.sample.util.sha256String
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4a.fileprovider.ktx.getShareFileUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import java.io.File

class PhotoActionViewModel(val sketch: Sketch) : ViewModel() {

    suspend fun share(imageUri: String): ActionResult {
        val fetchResultResult = withContext(Dispatchers.IO) {
            val request = ImageRequest(sketch.context, imageUri)
            val requestContext = RequestContext(sketch, request, Size.Empty)
            val fetcher = sketch.components.newFetcherOrThrow(requestContext)
            fetcher.fetch()
        }
        if (fetchResultResult.isFailure) {
            return ActionResult.error("Failed to share picture: ${fetchResultResult.exceptionOrNull()!!.message}")
        }
        val fetchResult = fetchResultResult.getOrThrow()

        val fileExtension = MimeTypeMap.getExtensionFromUrl(imageUri)
            ?: MimeTypeMap.getExtensionFromMimeType(fetchResult.mimeType ?: "")
            ?: "jpeg"
        val shareTempDir = sketch.context.getExternalFilesDir("share")
        val imageFile = File(shareTempDir, "share_temp.$fileExtension")
        imageFile.delete()

        try {
            withContext(Dispatchers.IO) {
                fetchResult.dataSource.openSource().buffer().use { input ->
                    imageFile.outputStream().sink().buffer().use { output ->
                        output.writeAll(input)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ActionResult.error("Failed to save picture: ${e.message}")
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, sketch.context.getShareFileUri(imageFile))
            type = fetchResult.mimeType ?: "image/*"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        sketch.context.startActivity(shareIntent)
        return ActionResult.success()
    }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    suspend fun save(imageUri: String): ActionResult {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageToGalleryWithMediaStore(sketch.context, imageUri)
        } else if (checkPermissionGranted(
                context = sketch.context,
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            saveImageToGalleryWithDirectory(imageUri)
        } else {
            ActionResult.error("Unable to save image because storage permission was not granted")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun saveImageToGalleryWithMediaStore(context: Context, imageUri: String): ActionResult {
        val request = ImageRequest(sketch.context, imageUri)
        val requestContext = RequestContext(sketch, request, Size.Empty)
        val fetcher = sketch.components.newFetcherOrThrow(requestContext)
        if (fetcher is FileUriFetcher) {
            return ActionResult.error("Local files do not need to be saved")
        }
        val fetchResultResult = withContext(Dispatchers.IO) {
            fetcher.fetch()
        }
        if (fetchResultResult.isFailure) {
            return ActionResult.error("Failed to save picture: ${fetchResultResult.exceptionOrNull()!!.message}")
        }
        val fetchResult = fetchResultResult.getOrThrow()

        val resolver = context.contentResolver
        val values = ContentValues().apply {
            val extension = MimeTypeMap.getExtensionFromMimeType(fetchResult.mimeType ?: "")
                ?: MimeTypeMap.getExtensionFromUrl(imageUri)
                ?: "jpeg"
            val mimeType = "image/$extension"
            val fileName = "${imageUri.sha256String()}.$extension"
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
            ActionResult.success("Saved to gallery")
        } else {
            val exception = saveResult.exceptionOrNull()
            ActionResult.error("Failed to save picture: ${exception?.message}")
        }
    }

    @Suppress("DEPRECATION")
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    suspend fun saveImageToGalleryWithDirectory(imageUri: String): ActionResult {
        val request = ImageRequest(sketch.context, imageUri)
        val requestContext = RequestContext(sketch, request, Size.Empty)
        val fetcher = sketch.components.newFetcherOrThrow(requestContext)
        if (fetcher is FileUriFetcher) {
            return ActionResult.error("Local files do not need to be saved")
        }
        val fetchResultResult = withContext(Dispatchers.IO) {
            fetcher.fetch()
        }
        if (fetchResultResult.isFailure) {
            return ActionResult.error("Failed to save picture: ${fetchResultResult.exceptionOrNull()!!.message}")
        }
        val fetchResult = fetchResultResult.getOrThrow()

        val picturesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val saveDir = File(picturesDir, "sketch")
        saveDir.mkdirs()
        val extension = MimeTypeMap.getExtensionFromMimeType(fetchResult.mimeType ?: "")
            ?: MimeTypeMap.getExtensionFromUrl(imageUri)
            ?: "jpeg"
        val fileName = "${imageUri.sha256String()}.$extension"
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
            ActionResult.success("Saved to gallery")
        } else {
            val exception = saveResult.exceptionOrNull()
            ActionResult.error("Failed to save picture: ${exception?.message}")
        }
    }
}