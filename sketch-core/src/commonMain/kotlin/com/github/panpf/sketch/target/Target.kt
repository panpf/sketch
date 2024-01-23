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
package com.github.panpf.sketch.target

import androidx.annotation.MainThread
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.internal.RequestDelegate
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.SizeResolver
import kotlinx.coroutines.Job

/**
 * A listener that accepts the result of an image request.
 */
interface Target {

    /**
     * Called when the request starts.
     */
    @MainThread
    fun onStart(requestContext: RequestContext, placeholder: Image?) {

    }

    /**
     * Called if the request completes successfully.
     */
    @MainThread
    fun onSuccess(requestContext: RequestContext, result: Image) {

    }

    /**
     * Called if an error occurs while executing the request.
     */
    @MainThread
    fun onError(requestContext: RequestContext, error: Image?) {

    }

    /**
     * If display counting is not supported, memory caching will not be used.
     * If true is returned, call the SketchCountBitmapDrawable.countBitmap.setIsDisplayed() method to record the number of impressions.
     * Otherwise, there will be image confusion and crashes
     */
    fun supportDisplayCount(): Boolean = false

    fun getImageOptions(): ImageOptions? = null

    fun getSizeResolver(): SizeResolver? = null

    fun getScale(): Scale? = null

    fun getLifecycleResolver(): LifecycleResolver? = null

    fun getListener(): Listener? = null

    fun getProgressListener(): ProgressListener? = null

    fun getRequestDelegate(
        sketch: Sketch,
        initialRequest: ImageRequest,
        job: Job
    ): RequestDelegate? = null
}