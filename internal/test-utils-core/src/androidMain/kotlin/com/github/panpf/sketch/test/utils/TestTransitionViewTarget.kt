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

package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asDrawableOrThrow
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.internal.BaseRequestDelegate
import com.github.panpf.sketch.request.internal.BaseRequestManager
import com.github.panpf.sketch.request.internal.RequestDelegate
import com.github.panpf.sketch.request.internal.RequestManager
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.target.TransitionViewTarget
import kotlinx.coroutines.Job

class TestTransitionViewTarget : Target, TransitionViewTarget {

    override var drawable: Drawable? = null

    override val fitScale: Boolean get() = true

    override fun onStart(requestContext: RequestContext, placeholder: Image?) {
        this.drawable = placeholder?.asDrawableOrThrow()
    }

    override fun onSuccess(requestContext: RequestContext, result: Image) {
        this.drawable = result.asDrawableOrThrow()
    }

    override fun onError(requestContext: RequestContext, error: Image?) {
        this.drawable = error?.asDrawableOrThrow()
    }

    private val requestManager = BaseRequestManager()

    override fun getRequestManager(): RequestManager = requestManager

    override fun newRequestDelegate(
        sketch: Sketch,
        initialRequest: ImageRequest,
        job: Job
    ): RequestDelegate = BaseRequestDelegate(sketch, initialRequest, this, job)
}