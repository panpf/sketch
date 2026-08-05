package com.github.panpf.sketch.componentloadertest.apple.test

import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.FileVideoFrameDecoder
import com.github.panpf.sketch.util.ComponentProvider
import com.github.panpf.sketch.util.VideoComponentProvider
import kotlin.reflect.KClass

internal actual val videoComponentProviderClass: KClass<out ComponentProvider> =
    VideoComponentProvider::class

internal actual val platformVideoDecoderFactories: List<Decoder.Factory> =
    listOf(FileVideoFrameDecoder.Factory())
