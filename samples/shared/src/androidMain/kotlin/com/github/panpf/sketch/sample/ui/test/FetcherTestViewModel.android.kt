package com.github.panpf.sketch.sample.ui.test

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.images.AssetImageFiles
import com.github.panpf.sketch.images.Base64Images
import com.github.panpf.sketch.images.ComposeResImageFile
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.HttpImages
import com.github.panpf.sketch.images.R
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import com.github.panpf.sketch.sample.util.versionCodeCompat
import com.github.panpf.tools4a.fileprovider.ktx.getShareFileUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.buffer
import okio.use
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
actual suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean
): List<PhotoTestItem> {
    val testFile = getPhotoFromFiles(context, ComposeResImageFiles.jpeg)
    val testFile2 = getPhotoFromFiles(context, ComposeResImageFiles.bmp)
    val testFile3 = getPhotoFromFiles(context, ComposeResImageFiles.png).toFile()
    val headerUserPackageInfo = loadUserAppPackageInfo(context, true)
    return buildList {
        add(PhotoTestItem(title = "HTTP", photoUri = HttpImages.HTTP))
        add(PhotoTestItem(title = "HTTPS", photoUri = HttpImages.HTTPS))
        add(
            PhotoTestItem(
                title = "CONTENT",
                photoUri = context.getShareFileUri(testFile3).toString()
            )
        )
        add(PhotoTestItem(title = "FILE_URI", photoUri = newFileUri(testFile)))
        add(PhotoTestItem(title = "FILE_PATH", photoUri = testFile2.toString()))
        add(PhotoTestItem(title = "ASSET", photoUri = AssetImageFiles.bird.uri))
        add(PhotoTestItem(title = "RES_ID", photoUri = newResourceUri(R.drawable.play)))
        add(
            PhotoTestItem(
                title = "RES_NAME",
                photoUri = newResourceUri("drawable", "bg_circle_accent")
            )
        )
        if (fromCompose) {
            add(PhotoTestItem(title = "RES_COMPOSE", photoUri = ComposeResImageFiles.longQMSHT.uri))
        }
        add(
            PhotoTestItem(
                title = "APP_ICON",
                photoUri = newAppIconUri(
                    headerUserPackageInfo.packageName,
                    headerUserPackageInfo.versionCodeCompat
                )
            )
        )
        add(PhotoTestItem(title = "BASE64", photoUri = Base64Images.KOTLIN_ICON))
    }
}

private suspend fun loadUserAppPackageInfo(
    context: PlatformContext,
    fromHeader: Boolean
): PackageInfo {
    return withContext(Dispatchers.IO) {
        val packageList =
            context.packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        (if (fromHeader) {
            packageList.find {
                it.applicationInfo!!.flags and ApplicationInfo.FLAG_SYSTEM == 0
            }
        } else {
            packageList.findLast {
                it.applicationInfo!!.flags and ApplicationInfo.FLAG_SYSTEM == 0
            }
        } ?: context.packageManager.getPackageInfo(context.packageName, 0))
    }
}

private suspend fun getPhotoFromFiles(context: Context, image: ComposeResImageFile): Path =
    withContext(Dispatchers.IO) {
        val appDataDir = (context.getExternalFilesDir(null) ?: context.filesDir).toOkioPath()
        val imageFile = appDataDir.resolve(image.name)
        val fileSystem = FileSystem.SYSTEM
        if (!fileSystem.exists(imageFile)) {
            fileSystem.createDirectories(imageFile.parent!!)
            image.toDataSource(context).openSource().buffer().use { input ->
                fileSystem.sink(imageFile).buffer().use { output ->
                    output.writeAll(input)
                }
            }
        }
        imageFile
    }