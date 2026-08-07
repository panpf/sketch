package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.images.Base64Images
import com.github.panpf.sketch.images.ComposeResImageFile
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.KotlinResImageFiles
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import com.github.panpf.sketch.util.appCacheDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
actual suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean
): List<PhotoTestItem> {
    val fileUriTestFile = getFileUriTestFile(context, ComposeResImageFiles.jpeg)
    val fileUriTestFile2 = getFileUriTestFile(context, ComposeResImageFiles.bmp)
    return buildList {
        add(PhotoTestItem(title = "HTTPS", photoUri = getOnePexelsPhoto()))
        add(PhotoTestItem(title = "FILE_URI", photoUri = newFileUri(fileUriTestFile)))
        add(PhotoTestItem(title = "FILE_PATH", photoUri = fileUriTestFile2.toString()))
        add(PhotoTestItem(title = "RES_KOTLIN", photoUri = KotlinResImageFiles.liuyifei.uri))
        add(PhotoTestItem(title = "RES_COMPOSE", photoUri = ComposeResImageFiles.jpeg.uri))
        add(PhotoTestItem(title = "BASE64", photoUri = Base64Images.KOTLIN_ICON))
    }
}

private suspend fun getFileUriTestFile(context: PlatformContext, image: ComposeResImageFile): Path =
    withContext(Dispatchers.IO) {
        val appCacheDir = context.appCacheDirectory()!!
        val imageFile = appCacheDir.resolve(image.name)
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