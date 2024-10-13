package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.images.ResourceImages

actual suspend fun buildDecoderTestItems(
    context: PlatformContext
): List<DecoderTestItem> = buildList {
    add(DecoderTestItem(name = "JPEG", imageUri = ResourceImages.jpeg.uri))
    add(DecoderTestItem(name = "PNG", imageUri = ResourceImages.png.uri))
    add(DecoderTestItem(name = "WEBP", imageUri = ResourceImages.webp.uri))
    add(DecoderTestItem(name = "BMP", imageUri = ResourceImages.bmp.uri))
    add(DecoderTestItem(name = "SVG", imageUri = ResourceImages.svg.uri))
    add(DecoderTestItem(name = "HEIC", imageUri = ResourceImages.heic.uri))
    add(DecoderTestItem(name = "AVIF", imageUri = ResourceImages.avif.uri))
    add(DecoderTestItem(name = "GIF", imageUri = ResourceImages.animGif.uri))
    add(DecoderTestItem(name = "ANIM_WEBP", imageUri = ResourceImages.animWebp.uri))
    add(DecoderTestItem(name = "ANIM_HEIF", imageUri = ResourceImages.animHeif.uri))
}