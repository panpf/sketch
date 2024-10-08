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

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.ProgressListener

/**
 * [ProgressListener] Combination of two [ProgressListener]
 *
 * @see com.github.panpf.sketch.core.common.test.request.internal.PairProgressListenerTest
 */
class PairProgressListener constructor(
    val first: ProgressListener,
    val second: ProgressListener,
) : ProgressListener {

    override fun onUpdateProgress(request: ImageRequest, progress: Progress) {
        first.onUpdateProgress(request, progress)
        second.onUpdateProgress(request, progress)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as PairProgressListener
        if (first != other.first) return false
        if (second != other.second) return false
        return true
    }

    override fun hashCode(): Int {
        var result = first.hashCode()
        result = 31 * result + second.hashCode()
        return result
    }

    override fun toString(): String {
        return "PairProgressListener(first=$first, second=$second)"
    }
}