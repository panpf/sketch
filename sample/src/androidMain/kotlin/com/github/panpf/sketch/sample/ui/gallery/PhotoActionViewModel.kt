/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.ui.base.ActionResult
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.MimeTypeMap
import com.github.panpf.tools4a.fileprovider.ktx.getShareFileUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.ByteString.Companion.encodeUtf8
import okio.buffer
import okio.sink
import java.io.File

class PhotoActionViewModel(application: Application) : LifecycleAndroidViewModel(application) {

    suspend fun share(imageUri: String): ActionResult {
        val application = application1
        val fetchResult = withContext(Dispatchers.IO) {
            val fetcher = application1.sketch.components
                .newFetcherOrThrow(ImageRequest(application1, imageUri))
            fetcher.fetch()
        }.let {
            it.getOrNull()
                ?: return ActionResult.error("Failed to save picture: ${it.exceptionOrNull()!!.message}")
        }

        val fileExtension = MimeTypeMap.getExtensionFromUrl(imageUri)
            ?: MimeTypeMap.getExtensionFromMimeType(fetchResult.mimeType ?: "")
            ?: "jpeg"
        val imageFile =
            File(application.getExternalFilesDir("share"), "share_temp.$fileExtension").apply {
                delete()
            }

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

        application.startActivity(Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, application.getShareFileUri(imageFile))
            type = fetchResult.mimeType ?: "image/*"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        })
        return ActionResult.success()
    }

    @Suppress("DEPRECATION")
    suspend fun save(imageUri: String): ActionResult {
        val application = application1
        val fetcher = withContext(Dispatchers.IO) {
            application.sketch.components.newFetcherOrThrow(ImageRequest(application, imageUri))
        }
        if (fetcher is FileUriFetcher) {
            return ActionResult.error("Local files do not need to be saved")
        }

        val fetchResult = withContext(Dispatchers.IO) {
            fetcher.fetch()
        }.let {
            it.getOrNull()
                ?: return ActionResult.error("Failed to save picture: ${it.exceptionOrNull()!!.message}")
        }

        val picturesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val outDir = File(picturesDir, "sketch3").apply { mkdirs() }
        val fileExtension = MimeTypeMap.getExtensionFromUrl(imageUri)
            ?: MimeTypeMap.getExtensionFromMimeType(fetchResult.mimeType ?: "")
            ?: "jpeg"
        val imageFile = File(outDir, "${imageUri.encodeUtf8().sha256().hex()}.$fileExtension")
        val result = withContext(Dispatchers.IO) {
            runCatching {
                fetchResult.dataSource.openSource().buffer().use { input ->
                    imageFile.outputStream().sink().buffer().use { output ->
                        output.writeAll(input)
                    }
                }
            }
        }
        return if (result.isSuccess) {
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile))
            application.sendBroadcast(intent)
            ActionResult.success("Saved to the '${imageFile.parentFile?.path}' directory")
        } else {
            val exception = result.exceptionOrNull()
            ActionResult.error("Failed to save picture: ${exception?.message}")
        }
    }
}