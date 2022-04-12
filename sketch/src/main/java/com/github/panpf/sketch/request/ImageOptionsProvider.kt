package com.github.panpf.sketch.request

interface ImageOptionsProvider {
    var displayImageOptions: ImageOptions?
}

fun ImageOptionsProvider.updateDisplayImageOptions(configBlock: (ImageOptions.Builder.() -> Unit)) {
    displayImageOptions =
        displayImageOptions?.newOptions(configBlock) ?: ImageOptions(configBlock)
}