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
package com.github.panpf.sketch.util

import android.view.View
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.core.R
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.internal.ViewTargetRequestManager

class SketchUtils private constructor() {

    companion object {

        internal fun requestManagerOrNull(view: View): ViewTargetRequestManager? =
            view.getTag(R.id.sketch_request_manager) as ViewTargetRequestManager?

        /**
         * Dispose the request that's attached to this view (if there is one).
         */
        fun dispose(view: View) = requestManagerOrNull(view)?.dispose()

        /**
         * Get the [ImageResult] of the most recently executed image request that's attached to this view.
         */
        fun getResult(view: View): ImageResult? = requestManagerOrNull(view)?.getResult()

        /**
         * Restart ImageRequest
         */
        fun restart(view: View) = requestManagerOrNull(view)?.restart()

        /**
         * Get the [ImageRequest] of the most recently executed image request that's attached to this view.
         */
        fun getRequest(view: View): ImageRequest? =
            requestManagerOrNull(view)?.getRequest()

        /**
         * Get the [Sketch] of the most recently executed image request that's attached to this view.
         */
        fun getSketch(view: View): Sketch? = requestManagerOrNull(view)?.getSketch()
    }
}