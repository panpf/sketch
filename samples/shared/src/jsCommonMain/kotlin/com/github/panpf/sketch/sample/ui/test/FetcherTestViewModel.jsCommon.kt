package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.images.Base64Images
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
actual suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean
): List<PhotoTestItem> {
    val httpsUri = getOnePexelsPhoto()
    val composeResourceUri = ComposeResImageFiles.png.uri
    val base64Uri = Base64Images.KOTLIN_ICON
    return buildList {
        add(PhotoTestItem(title = "HTTPS", photoUri = httpsUri))
        add(PhotoTestItem(title = "RES_COMPOSE", photoUri = composeResourceUri))
        add(PhotoTestItem(title = "BASE64", photoUri = base64Uri))
    }
}