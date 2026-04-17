package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem

actual suspend fun buildDecoderTestItems(
    context: PlatformContext
): List<PhotoTestItem> = buildList {
    add(PhotoTestItem(title = "JPEG", photoUri = ComposeResImageFiles.jpeg.uri))
    add(PhotoTestItem(title = "PNG", photoUri = ComposeResImageFiles.png.uri))
    add(PhotoTestItem(title = "WEBP", photoUri = ComposeResImageFiles.webp.uri))
    add(PhotoTestItem(title = "BMP", photoUri = ComposeResImageFiles.bmp.uri))
    add(PhotoTestItem(title = "SVG", photoUri = ComposeResImageFiles.svg.uri))
    add(PhotoTestItem(title = "HEIC", photoUri = ComposeResImageFiles.heic.uri))
    add(PhotoTestItem(title = "AVIF", photoUri = ComposeResImageFiles.avif.uri))
    add(PhotoTestItem(title = "GIF", photoUri = ComposeResImageFiles.animGif.uri))
    add(PhotoTestItem(title = "ANIM_WEBP", photoUri = ComposeResImageFiles.animWebp.uri))
    add(PhotoTestItem(title = "ANIM_HEIF", photoUri = ComposeResImageFiles.animHeif.uri))
}