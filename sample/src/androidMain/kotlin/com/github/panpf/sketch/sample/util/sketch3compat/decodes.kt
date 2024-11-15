@file:Suppress("PackageDirectoryMismatch")

package com.github.panpf.sketch.decode

@Deprecated(
    message = "Use Decoder instead",
    replaceWith = ReplaceWith("Decoder", "com.github.panpf.sketch.decode.Decoder")
)
typealias BitmapDecoder = Decoder

@Deprecated(
    message = "Use DecodeInterceptor instead",
    replaceWith = ReplaceWith(
        "DecodeInterceptor",
        "com.github.panpf.sketch.decode.DecodeInterceptor"
    )
)
typealias BitmapDecodeInterceptor = DecodeInterceptor

@Deprecated(
    message = "Use DecodeResult instead",
    replaceWith = ReplaceWith("DecodeResult", "com.github.panpf.sketch.decode.DecodeResult")
)
typealias BitmapDecodeResult = DecodeResult


@Deprecated(
    message = "Use Decoder instead",
    replaceWith = ReplaceWith("Decoder", "com.github.panpf.sketch.decode.Decoder")
)
typealias DrawableDecoder = Decoder

@Deprecated(
    message = "Use DecodeInterceptor instead",
    replaceWith = ReplaceWith(
        "DecodeInterceptor",
        "com.github.panpf.sketch.decode.DecodeInterceptor"
    )
)
typealias DrawableDecodeInterceptor = DecodeInterceptor

@Deprecated(
    message = "Use DecodeResult instead",
    replaceWith = ReplaceWith("DecodeResult", "com.github.panpf.sketch.decode.DecodeResult")
)
typealias DrawableDecodeResult = DecodeResult