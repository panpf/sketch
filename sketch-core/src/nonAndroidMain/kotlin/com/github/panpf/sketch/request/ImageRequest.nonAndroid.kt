package com.github.panpf.sketch.request

import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.decode.BitmapColorType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType


/**
 * Set [ColorType] to use when creating the bitmap.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.request.ImageRequestNonAndroidTest.testColorType
 */
fun ImageRequest.Builder.colorType(colorType: ColorType?): ImageRequest.Builder =
    colorType(colorType?.let { BitmapColorType(it) })

/**
 * Set [ColorSpace] to use when creating the bitmap.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.request.ImageRequestNonAndroidTest.testColorSpace
 */
fun ImageRequest.Builder.colorSpace(colorSpace: ColorSpace?): ImageRequest.Builder =
    colorSpace(colorSpace?.let { BitmapColorSpace(it) })