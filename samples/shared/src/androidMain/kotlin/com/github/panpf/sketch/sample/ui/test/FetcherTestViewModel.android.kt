package com.github.panpf.sketch.sample.ui.test

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.images.AssetImageFiles
import com.github.panpf.sketch.images.Base64Images
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.ContentImageFiles
import com.github.panpf.sketch.images.LocalImageFiles
import com.github.panpf.sketch.images.R
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import com.github.panpf.sketch.sample.util.versionCodeCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
actual suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean
): List<PhotoTestItem> {
    val localImageFiles = LocalImageFiles.with(context)
    val headerUserPackageInfo = loadUserAppPackageInfo(context, true)
    val httpsUri = getOnePexelsPhoto()
    val fileUri = newFileUri(localImageFiles.jpeg.uri)
    val filePath = localImageFiles.bmp.uri
    val composeResourceUri = ComposeResImageFiles.png.uri
    val contentUri = ContentImageFiles.with(context).webp.uri
    val assetUri = AssetImageFiles.bird.uri
    val resourceIdUri = newResourceUri(R.drawable.play)
    val resourceNameUri = newResourceUri("drawable", "bg_circle_accent")
    val appIconUri = newAppIconUri(
        packageName = headerUserPackageInfo.packageName,
        versionCode = headerUserPackageInfo.versionCodeCompat
    )
    val base64Uri = Base64Images.KOTLIN_ICON
    return buildList {
        add(PhotoTestItem(title = "HTTPS", photoUri = httpsUri))
        add(PhotoTestItem(title = "FILE_URI", photoUri = fileUri))
        add(PhotoTestItem(title = "FILE_PATH", photoUri = filePath))
        if (fromCompose) {
            add(PhotoTestItem(title = "RES_COMPOSE", photoUri = composeResourceUri))
        }
        add(PhotoTestItem(title = "CONTENT", photoUri = contentUri))
        add(PhotoTestItem(title = "ASSET", photoUri = assetUri))
        add(PhotoTestItem(title = "RES_ID", photoUri = resourceIdUri))
        add(PhotoTestItem(title = "RES_NAME", photoUri = resourceNameUri))
        add(PhotoTestItem(title = "APP_ICON", photoUri = appIconUri))
        add(PhotoTestItem(title = "BASE64", photoUri = base64Uri))
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