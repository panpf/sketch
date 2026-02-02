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
): List<FetcherTestItem> {
    val testFile = getPhotoFromFiles(context, ComposeResImageFiles.jpeg)
    val testFile2 = getPhotoFromFiles(context, ComposeResImageFiles.bmp)
    val testFile3 = getPhotoFromFiles(context, ComposeResImageFiles.png).toFile()
    val headerUserPackageInfo = loadUserAppPackageInfo(context, true)
    return buildList {
        add(FetcherTestItem(title = "HTTP", HttpImages.HTTP))
        add(FetcherTestItem(title = "HTTPS", HttpImages.HTTPS))
        add(FetcherTestItem(title = "CONTENT", context.getShareFileUri(testFile3).toString()))
        add(FetcherTestItem(title = "FILE_URI", newFileUri(testFile)))
        add(FetcherTestItem(title = "FILE_PATH", testFile2.toString()))
        add(FetcherTestItem(title = "ASSET", AssetImageFiles.bird.uri))
        add(FetcherTestItem(title = "RES_ID", newResourceUri(R.drawable.play)))
        add(FetcherTestItem(title = "RES_NAME", newResourceUri("drawable", "bg_circle_accent")))
        if (fromCompose) {
            add(
                FetcherTestItem(
                    title = "RES_COMPOSE",
                    ComposeResImageFiles.longQMSHT.uri
                )
            )
        }
        add(
            FetcherTestItem(
                title = "APP_ICON", newAppIconUri(
                    headerUserPackageInfo.packageName,
                    headerUserPackageInfo.versionCodeCompat
                )
            )
        )
        add(FetcherTestItem(title = "BASE64", Base64Images.KOTLIN_ICON))
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