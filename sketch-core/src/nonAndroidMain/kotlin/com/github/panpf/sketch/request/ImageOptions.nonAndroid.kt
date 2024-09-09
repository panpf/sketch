package com.github.panpf.sketch.request

import com.github.panpf.sketch.decode.BitmapConfig
import org.jetbrains.skia.ColorType


/**
 * Configure bitmap quality
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.request.ImageOptionsNonAndroidTest.testBitmapConfig
 */
fun ImageOptions.Builder.bitmapConfig(config: ColorType): ImageOptions.Builder =
    bitmapConfig(BitmapConfig(config))