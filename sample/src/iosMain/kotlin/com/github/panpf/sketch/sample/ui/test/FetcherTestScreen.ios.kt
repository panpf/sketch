package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newComposeResourceUri
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.fetch.newKotlinResourceUri
import com.github.panpf.sketch.images.Base64Images
import com.github.panpf.sketch.images.HttpImages
import com.github.panpf.sketch.images.ImageFile
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.ResourceImageFile
import com.github.panpf.sketch.sample.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import org.jetbrains.compose.resources.ExperimentalResourceApi
import platform.Foundation.NSBundle
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalResourceApi::class)
actual suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean
): List<FetcherTestItem> {
    val fileUriTestFile = getFileUriTestFile(ResourceImages.jpeg)
    val fileUriTestFile2 = getFileUriTestFile(ResourceImages.bmp)
    return buildList {
        add(FetcherTestItem(title = "HTTP", HttpImages.HTTP))
        add(FetcherTestItem(title = "HTTPS", HttpImages.HTTPS))
        add(FetcherTestItem(title = "FILE_URI", newFileUri(fileUriTestFile)))
        add(FetcherTestItem(title = "FILE_PATH", fileUriTestFile2.toString()))
        add(FetcherTestItem(title = "RES_KOTLIN", newKotlinResourceUri("sample.jpeg")))
        add(
            FetcherTestItem(
                title = "RES_COMPOSE",
                newComposeResourceUri(Res.getUri("files/liuyifei.jpg"))
            )
        )
        add(FetcherTestItem(title = "BASE64", Base64Images.KOTLIN_ICON))
    }
}

private suspend fun getFileUriTestFile(image: ImageFile): Path =
    withContext(Dispatchers.IO) {
        val appDataDir = getCachesDirectory().toPath()
        val resourceImage = image as ResourceImageFile
        val imageFile = appDataDir.resolve(resourceImage.resourceName)
        val fileSystem = FileSystem.SYSTEM
        if (!fileSystem.exists(imageFile)) {
            fileSystem.createDirectories(imageFile.parent!!)
            val resourcePath = NSBundle.mainBundle.resourcePath!!.toPath()
            val resourceFile =
                resourcePath.resolve("compose-resources").resolve(resourceImage.resourceName)
            fileSystem.source(resourceFile).buffer().use { input ->
                fileSystem.sink(imageFile).buffer().use { output ->
                    output.writeAll(input)
                }
            }
        }
        imageFile
    }


private fun getCachesDirectory(): String {
    val paths =
        NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
    return paths.first() as String
}