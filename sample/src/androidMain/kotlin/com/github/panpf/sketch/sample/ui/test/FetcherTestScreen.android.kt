package com.github.panpf.sketch.sample.ui.test

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.fetch.newComposeResourceUri
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.images.Base64Images
import com.github.panpf.sketch.images.HttpImages
import com.github.panpf.sketch.images.ImageFile
import com.github.panpf.sketch.images.ResourceImageFile
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.sample.R.mipmap
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.util.versionCodeCompat
import com.github.panpf.tools4a.fileprovider.ktx.getShareFileUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.buffer
import okio.source
import okio.use
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
actual suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean
): List<FetcherTestItem> {
    val testFile = getPhotoFromFiles(context, ResourceImages.jpeg)
    val testFile2 = getPhotoFromFiles(context, ResourceImages.bmp)
    val testFile3 = getPhotoFromFiles(context, ResourceImages.png).toFile()
    val headerUserPackageInfo = loadUserAppPackageInfo(context, true)
    return buildList {
        add(FetcherTestItem(title = "HTTP", HttpImages.HTTP))
        add(FetcherTestItem(title = "HTTPS", HttpImages.HTTPS))
        add(FetcherTestItem(title = "CONTENT", context.getShareFileUri(testFile3).toString()))
        add(FetcherTestItem(title = "FILE_URI", newFileUri(testFile)))
        add(FetcherTestItem(title = "FILE_PATH", testFile2.toString()))
        add(FetcherTestItem(title = "ASSET", ResourceImages.statics.first().uri))
        add(FetcherTestItem(title = "RES_ID", newResourceUri(mipmap.ic_launcher)))
        add(FetcherTestItem(title = "RES_NAME", newResourceUri("drawable", "bg_circle_accent")))
        if (fromCompose) {
            add(
                FetcherTestItem(
                    title = "RES_COMPOSE",
                    newComposeResourceUri(Res.getUri("files/liuyifei.jpg"))
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
                it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
            }
        } else {
            packageList.findLast {
                it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
            }
        } ?: context.packageManager.getPackageInfo(context.packageName, 0))
    }
}

private suspend fun getPhotoFromFiles(context: Context, image: ImageFile): Path =
    withContext(Dispatchers.IO) {
        val appDataDir = (context.getExternalFilesDir(null) ?: context.filesDir).toOkioPath()
        val resourceImage = image as ResourceImageFile
        val imageFile = appDataDir.resolve(resourceImage.resourceName)
        val fileSystem = FileSystem.SYSTEM
        if (!fileSystem.exists(imageFile)) {
            fileSystem.createDirectories(imageFile.parent!!)
            context.assets.open(image.resourceName).source().buffer().use { input ->
                fileSystem.sink(imageFile).buffer().use { output ->
                    output.writeAll(input)
                }
            }
        }
        imageFile
    }