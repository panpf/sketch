package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.images.ComposeResImageFiles

actual suspend fun buildDecoderTestItems(
    context: PlatformContext
): List<DecoderTestItem> = buildList {
    add(DecoderTestItem(name = "JPEG", imageUri = ComposeResImageFiles.jpeg.uri))
    add(DecoderTestItem(name = "PNG", imageUri = ComposeResImageFiles.png.uri))
    add(DecoderTestItem(name = "WEBP", imageUri = ComposeResImageFiles.webp.uri))
    add(DecoderTestItem(name = "BMP", imageUri = ComposeResImageFiles.bmp.uri))
    add(DecoderTestItem(name = "SVG", imageUri = ComposeResImageFiles.svg.uri))
    add(DecoderTestItem(name = "HEIC", imageUri = ComposeResImageFiles.heic.uri))
    add(DecoderTestItem(name = "AVIF", imageUri = ComposeResImageFiles.avif.uri))
    add(DecoderTestItem(name = "GIF", imageUri = ComposeResImageFiles.animGif.uri))
    add(DecoderTestItem(name = "ANIM_WEBP", imageUri = ComposeResImageFiles.animWebp.uri))
    add(DecoderTestItem(name = "ANIM_HEIF", imageUri = ComposeResImageFiles.animHeif.uri))
}