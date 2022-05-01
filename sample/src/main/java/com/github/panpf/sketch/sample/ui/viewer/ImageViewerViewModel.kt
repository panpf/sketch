package com.github.panpf.sketch.sample.ui.viewer

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.ui.common.ActionResult
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.MD5Utils
import com.github.panpf.tools4a.fileprovider.ktx.getShareFileUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ImageViewerViewModel(application: Application) : LifecycleAndroidViewModel(application) {

    suspend fun share(imageUri: String): ActionResult {
        val application = application1
        val fetchResult = withContext(Dispatchers.IO) {
            val fetcher = application1.sketch.componentRegistry
                .newFetcher(LoadRequest(application1, imageUri))
            fetcher.fetch()
        }

        val fileExtension = fetchResult.mimeType
            ?.let { readFileExtensionFromMimeType(it) } ?: "jpeg"
        val imageFile =
            File(application.getExternalFilesDir("share"), "share_temp.$fileExtension").apply {
                delete()
            }

        try {
            withContext(Dispatchers.IO) {
                fetchResult.dataSource.newInputStream().use { input ->
                    imageFile.outputStream().use { output ->
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

    suspend fun save(imageUri: String): ActionResult {
        val application = application1
        val fetcher = withContext(Dispatchers.IO) {
            application.sketch.componentRegistry.newFetcher(LoadRequest(application, imageUri))
        }
        if (fetcher is FileUriFetcher) {
            return ActionResult.error("Local files do not need to be saved")
        }

        val fetchResult = try {
            withContext(Dispatchers.IO) {
                fetcher.fetch()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ActionResult.error("Failed to save picture: ${e.message}")
        }

        val picturesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val outDir = File(picturesDir, "sketch3").apply { mkdirs() }
        val fileExtension = fetchResult.mimeType
            ?.let { readFileExtensionFromMimeType(it) } ?: "jpeg"
        val imageFile = File(outDir, "${MD5Utils.md5(imageUri)}.$fileExtension")
        if (!imageFile.exists()) {
            try {
                withContext(Dispatchers.IO) {
                    fetchResult.dataSource.newInputStream().use { input ->
                        imageFile.outputStream().use { output ->
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