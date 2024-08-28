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

import android.widget.ImageView
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.util.SketchUtils

/**
 * Dispose the request that's attached to this view (if there is one).
 */
fun ImageView.disposeLoad() {
    SketchUtils.dispose(this)
}

/**
 * Get the [ImageResult] of the most recently executed image request that's attached to this view.
 */
val ImageView.imageResult: ImageResult?
    get() = SketchUtils.getResult(this)