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

import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.state.StateImage

open class DownloadOptions {
    /**
     * Disabled disk caching
     */
    var isCacheInDiskDisabled = false

    /**
     * Limit request processing depth
     */
    var requestLevel: RequestLevel? = null

    constructor()
    constructor(from: DownloadOptions) {
        copy(from)
    }

    open fun reset() {
        isCacheInDiskDisabled = false
        requestLevel = null
    }

    fun copy(options: DownloadOptions) {
        isCacheInDiskDisabled = options.isCacheInDiskDisabled
        requestLevel = options.requestLevel
    }

    /**
     * Generate option key for assembling the request key
     *
     * @see SketchImageView.optionsKey
     * @see com.github.panpf.sketch.util.SketchUtils.makeRequestKey
     */
    open fun makeKey(): String {
        return ""
    }

    /**
     * Generate option key for [StateImage] to assemble the memory cache for [StateImage]
     *
     * @see com.github.panpf.sketch.util.SketchUtils.makeRequestKey
     */
    open fun makeStateImageKey(): String {
        return ""
    }
}