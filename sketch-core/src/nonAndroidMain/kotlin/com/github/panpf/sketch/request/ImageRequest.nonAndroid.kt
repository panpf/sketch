package com.github.panpf.sketch.request

import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.name
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType


/**
 * Set [ColorType] to use when creating the bitmap.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.request.ImageRequestNonAndroidTest.testBitmapConfig
 */
fun ImageRequest.Builder.bitmapConfig(config: ColorType): ImageRequest.Builder =
    bitmapConfig(BitmapConfig(config))

/**
 * Set [ColorSpace] to use when creating the bitmap.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.request.ImageRequestNonAndroidTest.testColorSpace
 */
fun ImageRequest.Builder.colorSpace(colorSpace: ColorSpace): ImageRequest.Builder =
    colorSpace(colorSpace.name())