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
package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.Listener

class CombinedListener(
    val fromTargetListener: Listener?,
    val fromBuilderListener: Listener?,
    val fromBuilderListeners: List<Listener>? = null,
) : Listener {

    override fun onStart(request: ImageRequest) {
        fromTargetListener?.onStart(request)
        fromBuilderListener?.onStart(request)
        fromBuilderListeners?.forEach {
            it.onStart(request)
        }
    }

    override fun onCancel(request: ImageRequest) {
        fromTargetListener?.onCancel(request)
        fromBuilderListener?.onCancel(request)
        fromBuilderListeners?.forEach {
            it.onCancel(request)
        }
    }

    override fun onError(request: ImageRequest, error: ImageResult.Error) {
        fromTargetListener?.onError(request, error)
        fromBuilderListener?.onError(request, error)
        fromBuilderListeners?.forEach {
            it.onError(request, error)
        }
    }

    override fun onSuccess(request: ImageRequest, result: ImageResult.Success) {
        fromTargetListener?.onSuccess(request, result)
        fromBuilderListener?.onSuccess(request, result)
        fromBuilderListeners?.forEach {
            it.onSuccess(request, result)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CombinedListener
        if (fromTargetListener != other.fromTargetListener) return false
        if (fromBuilderListener != other.fromBuilderListener) return false
        if (fromBuilderListeners != other.fromBuilderListeners) return false
        return true
    }

    override fun hashCode(): Int {
        var result = fromTargetListener.hashCode()
        result = 31 * result + (fromBuilderListener?.hashCode() ?: 0)
        result = 31 * result + (fromBuilderListeners?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "CombinedListener(" +
                "fromProvider=$fromTargetListener, " +
                "fromBuilder=$fromBuilderListener, " +
                "fromBuilderListeners=$fromBuilderListeners" +
                ")"
    }
}