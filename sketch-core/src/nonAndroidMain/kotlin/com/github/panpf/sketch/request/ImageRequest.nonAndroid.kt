package com.github.panpf.sketch.request

import com.github.panpf.sketch.decode.BitmapConfig
import org.jetbrains.skia.ColorType


/**
 * Configure bitmap quality
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.request.ImageRequestNonAndroidTest.testBitmapConfig
 */
fun ImageRequest.Builder.bitmapConfig(config: ColorType): ImageRequest.Builder =
    bitmapConfig(BitmapConfig(config))