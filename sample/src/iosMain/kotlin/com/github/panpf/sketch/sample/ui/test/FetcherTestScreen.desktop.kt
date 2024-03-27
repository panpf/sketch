package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.fetch.newKotlinResourceUri
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.resources.AssetImages.ResourceImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import platform.Foundation.NSBundle
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

actual suspend fun buildFetcherTestItems(context: PlatformContext): List<FetcherTestItem> {
    val fileUriTestFile = getFileUriTestFile(AssetImages.jpeg)
    val fileUriTestFile2 = getFileUriTestFile(AssetImages.bmp)
    return buildList {
        add(FetcherTestItem(title = "HTTP", AssetImages.HTTP))
        add(FetcherTestItem(title = "HTTPS", AssetImages.HTTPS))
        add(FetcherTestItem(title = "File", newFileUri(fileUriTestFile)))
        add(FetcherTestItem(title = "File2", fileUriTestFile2.toString()))
        add(FetcherTestItem(title = "RESOURCES", newKotlinResourceUri("sample.jpeg")))
        // TODO ComposeResourceUriFetcher
        add(FetcherTestItem(title = "BASE64", AssetImages.BASE64_IMAGE))
    }
}

private suspend fun getFileUriTestFile(image: AssetImages.Image): Path =
    withContext(Dispatchers.IO) {
        val appDataDir = getCachesDirectory().toPath()
        val resourceImage = image as ResourceImage
        val imageFile = appDataDir.resolve(resourceImage.fileName)
        val fileSystem = FileSystem.SYSTEM
        if (!fileSystem.exists(imageFile)) {
            fileSystem.createDirectories(imageFile.parent!!)
            val resourcePath = NSBundle.mainBundle.resourcePath!!.toPath()
            val resourceFile =
                resourcePath.resolve("compose-resources").resolve(resourceImage.fileName)
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