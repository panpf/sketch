package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.images.Base64Images
import com.github.panpf.sketch.images.ComposeResImageFile
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.HttpImages
import com.github.panpf.sketch.images.KotlinResImageFiles
import com.github.panpf.sketch.sample.appId
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