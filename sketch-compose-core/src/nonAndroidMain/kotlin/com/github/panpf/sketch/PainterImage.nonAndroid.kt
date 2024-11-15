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

package com.github.panpf.sketch

import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.painter.AnimatedImagePainter
import com.github.panpf.sketch.painter.asPainter

/**
 * Convert the Image to a Painter
 *
 * @see com.github.panpf.sketch.compose.core.nonandroid.test.PainterImageNonAndroidTest.testImageAsPainterOrNull
 */
actual fun Image.asPainterOrNull(filterQuality: FilterQuality): Painter? = when (this) {
    is PainterImage -> painter
    is BitmapImage -> bitmap.asComposeImageBitmap().asPainter(filterQuality)
    is AnimatedImage -> AnimatedImagePainter(this)
    else -> null
}