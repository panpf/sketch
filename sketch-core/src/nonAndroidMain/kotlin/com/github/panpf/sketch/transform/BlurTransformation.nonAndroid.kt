/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.transform

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.util.backgrounded
import com.github.panpf.sketch.util.blur
import com.github.panpf.sketch.util.hasAlpha
import com.github.panpf.sketch.util.mask

internal actual fun blurTransformation(
    image: Image,
    radius: Int,
    hasAlphaBitmapBgColor: Int?,
    maskColor: Int?
): Image {
    require(image is SkiaBitmapImage) {
        "Only SkiaBitmapImage is supported: ${image::class}"
    }
    val inputBitmap = image.bitmap
    // Transparent pixels cannot be blurred
    val compatAlphaBitmap = if (hasAlphaBitmapBgColor != null && inputBitmap.hasAlpha()) {
        inputBitmap.backgrounded(hasAlphaBitmapBgColor)
    } else {
        inputBitmap
    }
    val blurImage = compatAlphaBitmap.apply { blur(radius) }
    val maskImage = blurImage.apply { if (maskColor != null) mask(maskColor) }
    return maskImage.asSketchImage()
}