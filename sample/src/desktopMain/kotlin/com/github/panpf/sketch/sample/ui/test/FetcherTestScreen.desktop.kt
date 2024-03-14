package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.appId
import com.github.panpf.sketch.util.ResourceLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.harawata.appdirs.AppDirsFactory
import java.io.File

actual suspend fun buildFetcherTestItems(context: PlatformContext): List<FetcherTestItem> {
    val fileUriTestFile = getFileUriTestFile(AssetImages.jpeg)
    val fileUriTestFile2 = getFileUriTestFile(AssetImages.bmp)
    return buildList {
        add(FetcherTestItem(title = "HTTP", AssetImages.HTTP))
        add(FetcherTestItem(title = "HTTPS", AssetImages.HTTPS))
        add(FetcherTestItem(title = "File", newFileUri(fileUriTestFile)))
        add(FetcherTestItem(title = "File2", fileUriTestFile2.path))
        add(FetcherTestItem(title = "RESOURCES", newResourceUri("sample.jpeg")))
        add(FetcherTestItem(title = "BASE64", AssetImages.BASE64_IMAGE))
    }
}

private suspend fun getFileUriTestFile(image: AssetImages.Image): File =
    withContext(Dispatchers.IO) {
        val appDataDir = AppDirsFactory.getInstance().getUserDataDir(
            /* appName = */ appId,
            /* appVersion = */ null,
            /* appAuthor = */ null,
        )
        val imageFile = File(appDataDir, image.fileName)
        if (!imageFile.exists()) {
            imageFile.parentFile.mkdirs()
            ResourceLoader.Default.load(image.fileName).use { input ->
                imageFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        imageFile
    }