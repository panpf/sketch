package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newComposeResourceUri
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.fetch.newKotlinResourceUri
import com.github.panpf.sketch.images.Base64Images
import com.github.panpf.sketch.images.HttpImages
import com.github.panpf.sketch.images.ImageFile
import com.github.panpf.sketch.images.ResourceImageFile
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.sample.appId
import com.github.panpf.sketch.sample.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.harawata.appdirs.AppDirsFactory
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.File
import java.io.InputStream

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

private suspend fun getFileUriTestFile(image: ImageFile): File =
    withContext(Dispatchers.IO) {
        val appDataDir = AppDirsFactory.getInstance().getUserDataDir(
            /* appName = */ appId,
            /* appVersion = */ null,
            /* appAuthor = */ null,
        )
        val resourceImage = image as ResourceImageFile
        val imageFile = File(appDataDir, resourceImage.resourceName)
        if (!imageFile.exists()) {
            imageFile.parentFile.mkdirs()
            ClassLoaderResourceLoader.Default.load(resourceImage.resourceName).use { input ->
                imageFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        imageFile
    }


/**
 * Resource loader based on JVM current context class loader.
 *
 * Copy from compose Resources.kt
 */
private class ClassLoaderResourceLoader {

    companion object {
        val Default = ClassLoaderResourceLoader()
    }

    fun load(resourcePath: String): InputStream {
        val contextClassLoader = Thread.currentThread().contextClassLoader!!
        val resource = contextClassLoader.getResourceAsStream(resourcePath)
            ?: (::ClassLoaderResourceLoader.javaClass).getResourceAsStream(resourcePath)
        return requireNotNull(resource) { "Resource $resourcePath not found" }
    }
}