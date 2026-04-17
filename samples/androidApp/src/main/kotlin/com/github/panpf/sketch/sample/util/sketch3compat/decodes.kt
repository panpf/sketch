@file:Suppress("PackageDirectoryMismatch")

package com.github.panpf.sketch.decode

import com.github.panpf.sketch.request.ImageData

@Deprecated(
    message = "Use Decoder instead",
    replaceWith = ReplaceWith("Decoder", "com.github.panpf.sketch.decode.Decoder")
)
typealias BitmapDecoder = Decoder

@Deprecated(
    message = "Use com.github.panpf.sketch.request.ImageData instead",
    replaceWith = ReplaceWith("com.github.panpf.sketch.request.ImageData")
)
typealias BitmapDecodeResult = ImageData


@Deprecated(
    message = "Use Decoder instead",
    replaceWith = ReplaceWith("Decoder", "com.github.panpf.sketch.decode.Decoder")
)
typealias DrawableDecoder = Decoder

@Deprecated(
    message = "Use com.github.panpf.sketch.request.ImageData instead",
    replaceWith = ReplaceWith("com.github.panpf.sketch.request.ImageData")
)
typealias DrawableDecodeResult = ImageData