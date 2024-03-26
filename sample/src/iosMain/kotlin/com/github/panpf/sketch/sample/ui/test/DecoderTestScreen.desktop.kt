package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.resources.AssetImages

actual suspend fun buildDecoderTestItems(
    context: PlatformContext
): List<DecoderTestItem> = buildList {
    add(DecoderTestItem(name = "JPEG", imageUri = AssetImages.jpeg.uri))
    add(DecoderTestItem(name = "PNG", imageUri = AssetImages.png.uri))
    add(DecoderTestItem(name = "WEBP", imageUri = AssetImages.webp.uri))
    add(DecoderTestItem(name = "BMP", imageUri = AssetImages.bmp.uri))
    add(DecoderTestItem(name = "SVG", imageUri = AssetImages.svg.uri))
    add(DecoderTestItem(name = "HEIC", imageUri = AssetImages.heic.uri))
    add(DecoderTestItem(name = "GIF", imageUri = AssetImages.animGif.uri))
    add(DecoderTestItem(name = "ANIM_WEBP", imageUri = AssetImages.animWebp.uri))
    add(DecoderTestItem(name = "ANIM_HEIF", imageUri = AssetImages.animHeif.uri))
}