package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.images.Base64Images
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.KotlinResImageFiles
import com.github.panpf.sketch.images.LocalImageFiles
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
actual suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean
): List<PhotoTestItem> {
    val localImageFiles = LocalImageFiles.with(context)
    val httpsUri = getOnePexelsPhoto()
    val fileUri = newFileUri(localImageFiles.jpeg.uri)
    val filePath = localImageFiles.bmp.uri
    val composeResourceUri = ComposeResImageFiles.png.uri
    val kotlinResourceUri = KotlinResImageFiles.liuyifei.uri
    val base64Uri = Base64Images.KOTLIN_ICON
    return buildList {
        add(PhotoTestItem(title = "HTTPS", photoUri = httpsUri))
        add(PhotoTestItem(title = "FILE_URI", photoUri = fileUri))
        add(PhotoTestItem(title = "FILE_PATH", photoUri = filePath))
        add(PhotoTestItem(title = "RES_COMPOSE", photoUri = composeResourceUri))
        add(PhotoTestItem(title = "RES_KOTLIN", photoUri = kotlinResourceUri))
        add(PhotoTestItem(title = "BASE64", photoUri = base64Uri))
    }
}