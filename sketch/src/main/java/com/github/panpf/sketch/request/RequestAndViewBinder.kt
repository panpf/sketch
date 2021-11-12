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
package com.github.panpf.sketch.request

import com.github.panpf.sketch.SketchView
import com.github.panpf.sketch.util.SketchUtils.Companion.findDisplayRequest
import java.lang.ref.WeakReference

/**
 * Request与ImageView的关系绑定器
 */
class RequestAndViewBinder(imageView: SketchView) {

    private var displayRequest: DisplayRequest? = null
    private val imageViewReference: WeakReference<SketchView> = WeakReference(imageView)

    fun setDisplayRequest(displayRequest: DisplayRequest?) {
        this.displayRequest = displayRequest
    }

    val view: SketchView?
        get() {
            val sketchView = imageViewReference.get()
            return if (displayRequest != null) {
                val holderDisplayRequest = findDisplayRequest(sketchView)
                if (holderDisplayRequest != null && holderDisplayRequest === displayRequest) {
                    sketchView
                } else {
                    null
                }
            } else {
                sketchView
            }
        }

    val isBroken: Boolean
        get() = view == null

}