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

import com.github.panpf.sketch.request.Image
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.target.Target

class TestTarget : Target {

    override val supportDisplayCount: Boolean = true

    var startImage: Image? = null
    var successImage: Image? = null
    var errorImage: Image? = null

    override fun onStart(requestContext: RequestContext, placeholder: Image?) {
        super.onStart(requestContext, placeholder)
        startImage = placeholder
    }

    override fun onSuccess(requestContext: RequestContext, result: Image) {
        super.onSuccess(requestContext, result)
        successImage = result
    }

    override fun onError(requestContext: RequestContext, error: Image?) {
        super.onError(requestContext, error)
        errorImage = error
    }
}