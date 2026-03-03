package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.images.Base64Images
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.HttpImages
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
actual suspend fun buildFetcherTestItems(
    context: PlatformContext,
    fromCompose: Boolean
): List<PhotoTestItem> {
    return buildList {
        add(PhotoTestItem(title = "HTTP", photoUri = HttpImages.HTTP))
        add(PhotoTestItem(title = "HTTPS", photoUri = HttpImages.HTTPS))
        add(PhotoTestItem(title = "RES_COMPOSE", photoUri = ComposeResImageFiles.jpeg.uri))
        add(PhotoTestItem(title = "BASE64", photoUri = Base64Images.KOTLIN_ICON))
    }
}