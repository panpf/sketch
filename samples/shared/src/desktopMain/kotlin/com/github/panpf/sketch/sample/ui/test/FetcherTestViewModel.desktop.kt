package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.images.Base64Images
import com.github.panpf.sketch.images.ComposeResImageFile
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.HttpImages
import com.github.panpf.sketch.images.KotlinResImageFiles
import com.github.panpf.sketch.sample.appId
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.harawata.appdirs.AppDirsFactory
import okio.buffer
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.File

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

private suspend fun getFileUriTestFile(image: ComposeResImageFile): File =
    withContext(Dispatchers.IO) {
        val appDataDir = AppDirsFactory.getInstance().getUserDataDir(
            /* appName = */ appId,
            /* appVersion = */ null,
            /* appAuthor = */ null,
        )
        val imageFile = File(appDataDir, image.name)
        if (!imageFile.exists()) {
            imageFile.parentFile.mkdirs()
            image.toDataSource(PlatformContext.INSTANCE)
                .openSource().buffer().inputStream()
                .use { input ->
                    imageFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
        }
        imageFile
    }