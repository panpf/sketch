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
package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.request.Image
import com.github.panpf.sketch.request.asDrawable
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.transition.TransitionViewTarget

class TestTransitionTarget : Target, TransitionViewTarget {

    override var drawable: Drawable? = null

    override val supportDisplayCount: Boolean = true

    override fun onStart(requestContext: RequestContext, placeholder: Image?) {
        this.drawable = placeholder?.asDrawable(requestContext.request.context.resources)
    }

    override fun onSuccess(requestContext: RequestContext, result: Image) {
        this.drawable = result.asDrawable(requestContext.request.context.resources)
    }

    override fun onError(requestContext: RequestContext, error: Image?) {
        this.drawable = error?.asDrawable(requestContext.request.context.resources)
    }
}