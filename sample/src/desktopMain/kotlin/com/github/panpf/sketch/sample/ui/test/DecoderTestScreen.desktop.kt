package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.images.MyImages

actual suspend fun buildDecoderTestItems(
    context: PlatformContext
): List<DecoderTestItem> = buildList {
    add(DecoderTestItem(name = "JPEG", imageUri = MyImages.jpeg.uri))
    add(DecoderTestItem(name = "PNG", imageUri = MyImages.png.uri))
    add(DecoderTestItem(name = "WEBP", imageUri = MyImages.webp.uri))
    add(DecoderTestItem(name = "BMP", imageUri = MyImages.bmp.uri))
    add(DecoderTestItem(name = "SVG", imageUri = MyImages.svg.uri))
    add(DecoderTestItem(name = "HEIC", imageUri = MyImages.heic.uri))
    add(DecoderTestItem(name = "GIF", imageUri = MyImages.animGif.uri))
    add(DecoderTestItem(name = "ANIM_WEBP", imageUri = MyImages.animWebp.uri))
    add(DecoderTestItem(name = "ANIM_HEIF", imageUri = MyImages.animHeif.uri))
}