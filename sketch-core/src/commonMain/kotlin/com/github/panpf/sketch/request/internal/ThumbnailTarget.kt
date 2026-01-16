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

package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.target.Target

class ThumbnailTarget(
    private val target: Target,
) : Target by target {

    override fun getListener(): Listener? = null

    override fun getProgressListener(): ProgressListener? = null

    override fun getImageOptions(): ImageOptions? {
        return super.getImageOptions()
            ?.newOptions {
                // The cache key for the original request is configured for the original uri and does not apply to thumbnail requests.
                memoryCacheKey(key = null)
                resultCacheKey(key = null)
                downloadCacheKey(key = null)
            }
    }
}