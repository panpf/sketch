/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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

import com.github.panpf.sketch.BufferedImageImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.circleCropped

internal actual fun circleCropTransformation(image: Image, scale: Scale): Image? {
    val inputBufferedImage = image.asOrNull<BufferedImageImage>()?.bufferedImage ?: return null
    val outBufferedImage = inputBufferedImage.circleCropped(scale)
    return outBufferedImage.asSketchImage()
}