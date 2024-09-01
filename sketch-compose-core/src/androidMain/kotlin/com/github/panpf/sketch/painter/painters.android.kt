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

package com.github.panpf.sketch.painter

import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.AndroidBitmapImage
import com.github.panpf.sketch.AndroidDrawableImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.PainterImage

/**
 * Convert the Image to a Painter
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.PaintersAndroidTest.testImageAsPainter
 */
actual fun Image.asPainter(): Painter = when (this) {
    is PainterImage -> painter
    is AndroidBitmapImage -> bitmap.asImageBitmap().asPainter()
    is AndroidDrawableImage -> drawable.asPainter()
    else -> throw IllegalArgumentException("Not supported conversion to Painter from Image '$this'")
}

/**
 * Convert the painter to a platform log string
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.PaintersAndroidTest.testImageAsPainter
 */
actual fun Painter.platformToLogString(): String? = null