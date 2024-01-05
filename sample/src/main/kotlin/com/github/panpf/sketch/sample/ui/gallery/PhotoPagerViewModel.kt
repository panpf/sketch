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
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import androidx.core.graphics.ColorUtils
import com.github.panpf.sketch.datasource.BasedStreamDataSource
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.ui.base.ActionResult
import com.github.panpf.sketch.sketch
import com.github.panpf.tools4a.fileprovider.ktx.getShareFileUri
import com.github.panpf.tools4j.security.ktx.getMD5Digest
import com.github.panpf.tools4k.lang.asOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.File

class PhotoPagerViewModel(application: Application) : LifecycleAndroidViewModel(application) {

    private val _buttonBgColor = MutableStateFlow(ColorUtils.setAlphaComponent(Color.parseColor("#bf5660"), 160))
    val buttonBgColor: StateFlow<Int> = _buttonBgColor

    fun setButtonBgColor(color: Int) {
        _buttonBgColor.value = ColorUtils.setAlphaComponent(color, 160)
    }

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

        val fileExtension = fetchResult.mimeType
            ?.let { readFileExtensionFromMimeType(it) } ?: "jpeg"
        val imageFile =
            File(application.getExternalFilesDir("share"), "share_temp.$fileExtension").apply {
                delete()
            }

        try {
            withContext(Dispatchers.IO) {
                fetchResult.dataSource.asOrThrow<BasedStreamDataSource>().newInputStream()
                    .use { input ->
                        imageFile.outputStream().buffered().use { output ->
                            input.copyTo(output)
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
        val fileExtension = fetchResult.mimeType
            ?.let { readFileExtensionFromMimeType(it) } ?: "jpeg"
        val imageFile = File(outDir, "${imageUri.getMD5Digest()}.$fileExtension")
        if (!imageFile.exists()) {
            try {
                withContext(Dispatchers.IO) {
                    fetchResult.dataSource.asOrThrow<BasedStreamDataSource>().newInputStream()
                        .use { input ->
                            imageFile.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return ActionResult.error("Failed to save picture: ${e.message}")
            }

            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile))
            application.sendBroadcast(intent)
        }

        return ActionResult.success("Saved to the '${imageFile.parentFile?.path}' directory")
    }

    private fun readFileExtensionFromMimeType(mimeType: String): String? {
        val lastIndexOf = mimeType.lastIndexOf("/").takeIf { it >= 0 } ?: return null
        return mimeType.substring(lastIndexOf + 1).takeIf { "" != it.trim { it1 -> it1 <= ' ' } }
            ?.let {
                if (it == "svg+xml") {
                    "svg"
                } else {
                    it
                }
            }
    }
}