package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.images.Base64Images
import com.github.panpf.sketch.images.ComposeResImageFile
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.HttpImages
import com.github.panpf.sketch.images.KotlinResImageFiles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import org.jetbrains.compose.resources.ExperimentalResourceApi
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalResourceApi::class)
actual suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean
): List<FetcherTestItem> {
    val fileUriTestFile = getFileUriTestFile(ComposeResImageFiles.jpeg)
    val fileUriTestFile2 = getFileUriTestFile(ComposeResImageFiles.bmp)
    return buildList {
        add(FetcherTestItem(title = "HTTP", HttpImages.HTTP))
        add(FetcherTestItem(title = "HTTPS", HttpImages.HTTPS))
        add(FetcherTestItem(title = "FILE_URI", newFileUri(fileUriTestFile)))
        add(FetcherTestItem(title = "FILE_PATH", fileUriTestFile2.toString()))
        add(FetcherTestItem(title = "RES_KOTLIN", KotlinResImageFiles.liuyifei.uri))
        add(
            FetcherTestItem(
                title = "RES_COMPOSE",
                ComposeResImageFiles.jpeg.uri
            )
        )
        add(FetcherTestItem(title = "BASE64", Base64Images.KOTLIN_ICON))
    }
}

private suspend fun getFileUriTestFile(image: ComposeResImageFile): Path =
    withContext(Dispatchers.IO) {
        val appDataDir = getCachesDirectory().toPath()
        val imageFile = appDataDir.resolve(image.name)
        val fileSystem = FileSystem.SYSTEM
        if (!fileSystem.exists(imageFile)) {
            fileSystem.createDirectories(imageFile.parent!!)
            image.toDataSource(PlatformContext.INSTANCE).openSource().buffer().use { input ->
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