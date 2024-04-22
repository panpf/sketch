package com.github.panpf.sketch.sample.ui.test

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newComposeResourceUri
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.images.MyImage
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.images.MyResourceImage
import com.github.panpf.sketch.sample.R.mipmap
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

actual suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean
): List<FetcherTestItem> {
    val testFile = getPhotoFromFiles(context, MyImages.jpeg)
    val testFile2 = getPhotoFromFiles(context, MyImages.bmp)
    val testFile3 = getPhotoFromFiles(context, MyImages.png).toFile()
    val headerUserPackageInfo = loadUserAppPackageInfo(context, true)
    return buildList {
        add(FetcherTestItem(title = "HTTP", MyImages.HTTP))
        add(FetcherTestItem(title = "HTTPS", MyImages.HTTPS))
        add(FetcherTestItem(title = "CONTENT", context.getShareFileUri(testFile3).toString()))
        add(FetcherTestItem(title = "FILE_URI", newFileUri(testFile)))
        add(FetcherTestItem(title = "FILE_PATH", testFile2.toString()))
        add(FetcherTestItem(title = "ASSET", MyImages.statics.first().uri))
        add(FetcherTestItem(title = "RES_ID", newResourceUri(mipmap.ic_launcher)))
        add(FetcherTestItem(title = "RES_NAME", newResourceUri("drawable", "bg_circle_accent")))
        if (fromCompose) {
            add(FetcherTestItem(title = "RES_COMPOSE", newComposeResourceUri("files/liuyifei.jpg")))
        }
        add(
            FetcherTestItem(
                title = "APP_ICON", newAppIconUri(
                    headerUserPackageInfo.packageName,
                    headerUserPackageInfo.versionCodeCompat
                )
            )
        )
        add(FetcherTestItem(title = "BASE64", MyImages.BASE64_IMAGE))
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

private suspend fun getPhotoFromFiles(context: Context, image: MyImage): Path =
    withContext(Dispatchers.IO) {
        val appDataDir = (context.getExternalFilesDir(null) ?: context.filesDir).toOkioPath()
        val resourceImage = image as MyResourceImage
        val imageFile = appDataDir.resolve(resourceImage.fileName)
        val fileSystem = FileSystem.SYSTEM
        if (!fileSystem.exists(imageFile)) {
            fileSystem.createDirectories(imageFile.parent!!)
            context.assets.open(image.fileName).source().buffer().use { input ->
                fileSystem.sink(imageFile).buffer().use { output ->
                    output.writeAll(input)
                }
            }
        }
        imageFile
    }