package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.images.Base64Images
import com.github.panpf.sketch.images.ComposeResImageFile
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.HttpImages
import com.github.panpf.sketch.images.KotlinResImageFiles
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
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
): List<PhotoTestItem> {
    val fileUriTestFile = getFileUriTestFile(ComposeResImageFiles.jpeg)
    val fileUriTestFile2 = getFileUriTestFile(ComposeResImageFiles.bmp)
    return buildList {
        add(PhotoTestItem(title = "HTTP", photoUri = HttpImages.HTTP))
        add(PhotoTestItem(title = "HTTPS", photoUri = HttpImages.HTTPS))
        add(PhotoTestItem(title = "FILE_URI", photoUri = newFileUri(fileUriTestFile)))
        add(PhotoTestItem(title = "FILE_PATH", photoUri = fileUriTestFile2.toString()))
        add(PhotoTestItem(title = "RES_KOTLIN", photoUri = KotlinResImageFiles.liuyifei.uri))
        add(PhotoTestItem(title = "RES_COMPOSE", photoUri = ComposeResImageFiles.jpeg.uri))
        add(PhotoTestItem(title = "BASE64", photoUri = Base64Images.KOTLIN_ICON))
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