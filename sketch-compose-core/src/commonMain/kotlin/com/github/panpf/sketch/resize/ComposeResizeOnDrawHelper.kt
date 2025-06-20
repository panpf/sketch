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

package com.github.panpf.sketch.resize

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.asPainter
import com.github.panpf.sketch.painter.resize
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.target.ComposeTarget
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toSize

/**
 * [ResizeOnDrawHelper] implementation based on Compose
 *
 * @see com.github.panpf.sketch.compose.core.common.test.resize.ComposeResizeOnDrawHelperTest
 */
data object ComposeResizeOnDrawHelper : ResizeOnDrawHelper {

    override val key: String = "ComposeResizeOnDrawHelper"

    override fun resize(request: ImageRequest, size: Size, image: Image): Image {
        val target = (request.target as? ComposeTarget) ?: return image
        val painter = image.asPainter()
        val composeSize = size.toSize()
        val resizePainter = painter.resize(
            size = composeSize,
            contentScale = target.contentScale,
            alignment = target.alignment
        )
        return resizePainter.asImage()
    }

    override fun toString(): String = "ComposeResizeOnDrawHelper"
}