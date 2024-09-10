package com.github.panpf.sketch.request

import com.github.panpf.sketch.decode.BitmapConfig
import org.jetbrains.skia.ColorType


/**
 * Set [ColorType] to use when creating the bitmap.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.request.ImageOptionsNonAndroidTest.testBitmapConfig
 */
fun ImageOptions.Builder.bitmapConfig(config: ColorType): ImageOptions.Builder =
    bitmapConfig(BitmapConfig(config))