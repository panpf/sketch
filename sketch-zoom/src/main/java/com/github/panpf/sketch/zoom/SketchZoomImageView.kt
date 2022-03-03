/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.zoom

import android.content.Context
import android.util.AttributeSet
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.viewability.AbsAbilityImageView

open class SketchZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbsAbilityImageView(context, attrs, defStyle) {

    val zoomAbility = ZoomAbility()

    init {
        addViewAbility(zoomAbility)
    }

    override fun submitRequest(request: DisplayRequest) {
        context.sketch.enqueueDisplay(request)
    }

    override fun canScrollHorizontally(direction: Int): Boolean =
        zoomAbility.canScrollHorizontally(direction)

    override fun canScrollVertically(direction: Int): Boolean =
        zoomAbility.canScrollVertically(direction)
}