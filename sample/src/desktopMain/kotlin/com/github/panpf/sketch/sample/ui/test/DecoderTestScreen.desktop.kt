package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.resources.AssetImages

actual suspend fun buildDecoderTestItems(context: PlatformContext): List<DecoderTestItem> {
    return buildList {
        add(DecoderTestItem(name = "JPEG", imageUri = AssetImages.jpeg.uri))
        add(DecoderTestItem(name = "PNG", imageUri = AssetImages.png.uri))
        add(DecoderTestItem(name = "BMP", imageUri = AssetImages.bmp.uri))
        add(DecoderTestItem(name = "GIF", imageUri = AssetImages.animGif.uri))
        add(DecoderTestItem(name = "SVG", imageUri = AssetImages.svg.uri))
    }
}