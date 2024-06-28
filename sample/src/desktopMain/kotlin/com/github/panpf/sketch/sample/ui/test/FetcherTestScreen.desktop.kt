package com.github.panpf.sketch.sample.ui.test

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.ResourceLoader
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newComposeResourceUri
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.fetch.newKotlinResourceUri
import com.github.panpf.sketch.images.MyImage
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.images.MyResourceImage
import com.github.panpf.sketch.sample.appId
import com.github.panpf.sketch.sample.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.harawata.appdirs.AppDirsFactory
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.File

@OptIn(ExperimentalResourceApi::class)
actual suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean
): List<FetcherTestItem> {
    val fileUriTestFile = getFileUriTestFile(MyImages.jpeg)
    val fileUriTestFile2 = getFileUriTestFile(MyImages.bmp)
    return buildList {
        add(FetcherTestItem(title = "HTTP", MyImages.HTTP))
        add(FetcherTestItem(title = "HTTPS", MyImages.HTTPS))
        add(FetcherTestItem(title = "FILE_URI", newFileUri(fileUriTestFile)))
        add(FetcherTestItem(title = "FILE_PATH", fileUriTestFile2.toString()))
        add(FetcherTestItem(title = "RES_KOTLIN", newKotlinResourceUri("sample.jpeg")))
        add(
            FetcherTestItem(
                title = "RES_COMPOSE",
                newComposeResourceUri(Res.getUri("files/liuyifei.jpg"))
            )
        )
        add(FetcherTestItem(title = "BASE64", MyImages.BASE64_IMAGE))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private suspend fun getFileUriTestFile(image: MyImage): File =
    withContext(Dispatchers.IO) {
        val appDataDir = AppDirsFactory.getInstance().getUserDataDir(
            /* appName = */ appId,
            /* appVersion = */ null,
            /* appAuthor = */ null,
        )
        val resourceImage = image as MyResourceImage
        val imageFile = File(appDataDir, resourceImage.fileName)
        if (!imageFile.exists()) {
            imageFile.parentFile.mkdirs()
            ResourceLoader.Default.load(resourceImage.fileName).use { input ->
                imageFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        imageFile
    }