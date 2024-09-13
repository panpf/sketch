package com.github.panpf.sketch.request

import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.decode.BitmapColorType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType


/**
 * Set [ColorType] to use when creating the bitmap.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.request.ImageOptionsNonAndroidTest.testColorType
 */
fun ImageOptions.Builder.colorType(colorType: ColorType?): ImageOptions.Builder =
    colorType(colorType?.let { BitmapColorType(it) })

/**
 * Set [ColorSpace] to use when creating the bitmap.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.request.ImageOptionsNonAndroidTest.testColorSpace
 */
fun ImageOptions.Builder.colorSpace(colorSpace: ColorSpace?): ImageOptions.Builder =
    colorSpace(colorSpace?.let { BitmapColorSpace(it) })