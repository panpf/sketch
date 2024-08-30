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
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.isNotEmpty

/**
 * Resize the image while drawing
 *
 * @see com.github.panpf.sketch.core.common.test.resize.ResizeOnDrawTest
 */
fun Image.resizeOnDraw(request: ImageRequest, size: Size?): Image {
    if (size?.isNotEmpty == true && request.resizeOnDraw == true) {
        val resizeOnDrawHelper = request.target?.getResizeOnDrawHelper()
        if (resizeOnDrawHelper != null) {
            return resizeOnDrawHelper.resize(request, size, this)
        }
    }
    return this
}

/**
 * Use ResizeDrawable or ResizePainter to wrap an Image to resize it while drawing
 *
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 */
interface ResizeOnDrawHelper : Key {

    fun resize(request: ImageRequest, size: Size, image: Image): Image
}