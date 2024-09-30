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

package com.github.panpf.sketch.target

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.MainThread
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.internal.RequestDelegate
import com.github.panpf.sketch.request.internal.RequestManager
import com.github.panpf.sketch.resize.ResizeOnDrawHelper
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.Transition
import kotlinx.coroutines.Job

/**
 * [Target] is mainly used to display [Image].
 *
 * [Target] can also provide [RequestManager], [RequestDelegate], [Listener], [ProgressListener],
 * [LifecycleResolver], [SizeResolver], [ScaleDecider], [ResizeOnDrawHelper], [CrossfadeTransition]
 * related to [Target] when building the request, [ImageOptions], [ComponentRegistry] and other configurations
 *
 * @see com.github.panpf.sketch.compose.core.common.test.target.GenericComposeTargetTest
 * @see com.github.panpf.sketch.view.core.test.target.GenericViewTargetTest
 * @see com.github.panpf.sketch.view.core.test.target.RemoteViewsTargetTest
 */
interface Target {

    /**
     * The current image displayed by the target.
     *
     * @see com.github.panpf.sketch.state.CurrentStateImage
     */
    val currentImage: Image?
        get() = null


    /**
     * Get the [RequestManager] associated with the target.
     */
    fun getRequestManager(): RequestManager? = null

    /**
     * Create a new [RequestDelegate] for the request.
     */
    fun newRequestDelegate(
        sketch: Sketch,
        initialRequest: ImageRequest,
        job: Job
    ): RequestDelegate? = null


    /**
     * Get the [Listener] associated with the target.
     */
    fun getListener(): Listener? = null

    /**
     * Get the [ProgressListener] associated with the target.
     */
    fun getProgressListener(): ProgressListener? = null

    /**
     * Get the [LifecycleResolver] associated with the target.
     */
    fun getLifecycleResolver(): LifecycleResolver? = null


    /**
     * Get the [SizeResolver] associated with the target.
     */
    fun getSizeResolver(): SizeResolver? = null

    /**
     * Get the [ScaleDecider] associated with the target.
     */
    fun getScaleDecider(): ScaleDecider? = null

    /**
     * Get the [ResizeOnDrawHelper] associated with the target.
     */
    fun getResizeOnDrawHelper(): ResizeOnDrawHelper? = null

    /**
     * Get the [ImageOptions] associated with the target.
     */
    fun getImageOptions(): ImageOptions? = null

    /**
     * Get the [ComponentRegistry] associated with the target.
     */
    fun getComponents(): ComponentRegistry? = null


    /**
     * Convert a generic [Transition.Factory] to a [Transition.Factory] appropriate for the current Target
     */
    fun convertTransition(factory: Transition.Factory): Transition.Factory? = null


    /**
     * Called when the request starts.
     */
    @MainThread
    fun onStart(sketch: Sketch, request: ImageRequest, placeholder: Image?) {

    }

    /**
     * Called if the request completes successfully.
     */
    @MainThread
    fun onSuccess(sketch: Sketch, request: ImageRequest, result: Image) {

    }

    /**
     * Called if an error occurs while executing the request.
     */
    @MainThread
    fun onError(sketch: Sketch, request: ImageRequest, error: Image?) {

    }

//    TODO All interfaces used in ImageRequest and ImageOptions must implement equals, hashCode and toString
//    override fun equals(other: Any?): Boolean

//    override fun hashCode(): Int

//    override fun toString(): String
}