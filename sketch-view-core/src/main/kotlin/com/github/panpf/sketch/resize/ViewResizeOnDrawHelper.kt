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
import com.github.panpf.sketch.asDrawable
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.drawable.resize
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.target.ViewTarget
import com.github.panpf.sketch.util.Size

/**
 * View resize on draw helper
 *
 * @see com.github.panpf.sketch.view.core.test.resize.ViewResizeOnDrawHelperTest
 */
data object ViewResizeOnDrawHelper : ResizeOnDrawHelper {

    override val key: String = "ViewResizeOnDrawHelper"

    override fun resize(request: ImageRequest, size: Size, image: Image): Image {
        val target = (request.target as? ViewTarget<*>) ?: return image
        val drawable = image.asDrawable(request.context.resources)
        val resizeDrawable = drawable.resize(size, target.scaleType)
        return resizeDrawable.asImage()
    }

    override fun toString(): String = "ViewResizeOnDrawHelper"
}