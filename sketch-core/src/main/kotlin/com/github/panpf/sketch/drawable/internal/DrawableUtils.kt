/*
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ------------------------------------------------------------------------
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
package com.github.panpf.sketch.drawable.internal

import android.graphics.Rect
import com.github.panpf.sketch.util.Size
import kotlin.math.min

internal fun calculateFitBounds(contentSize: Size, containerBounds: Rect): Rect {
    val left: Int
    val top: Int
    val right: Int
    val bottom: Int
    if (contentSize.width <= containerBounds.width() && contentSize.height <= containerBounds.height()) {
        left = containerBounds.left + (containerBounds.width() - contentSize.width) / 2
        top = containerBounds.top + (containerBounds.height() - contentSize.height) / 2
        right = left + contentSize.width
        bottom = top + contentSize.height
    } else {
        val scale = min(
            containerBounds.width().toFloat() / contentSize.width,
            containerBounds.height().toFloat() / contentSize.height
        )
        val scaledWidth = (contentSize.width * scale).toInt()
        val scaledHeight = (contentSize.height * scale).toInt()
        left = containerBounds.left + (containerBounds.width() - scaledWidth) / 2
        top = containerBounds.top + (containerBounds.height() - scaledHeight) / 2
        right = left + scaledWidth
        bottom = top + scaledHeight
    }
    return Rect(left, top, right, bottom)
}