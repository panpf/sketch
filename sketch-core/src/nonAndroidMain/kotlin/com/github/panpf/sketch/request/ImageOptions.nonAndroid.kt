package com.github.panpf.sketch.request

import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.name
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType


/**
 * Set [ColorType] to use when creating the bitmap.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.request.ImageOptionsNonAndroidTest.testBitmapConfig
 */
fun ImageOptions.Builder.bitmapConfig(config: ColorType): ImageOptions.Builder =
    bitmapConfig(BitmapConfig(config))

/**
 * Set [ColorSpace] to use when creating the bitmap.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.request.ImageOptionsNonAndroidTest.testColorSpace
 */
fun ImageOptions.Builder.colorSpace(colorSpace: ColorSpace): ImageOptions.Builder =
    colorSpace(colorSpace.name())