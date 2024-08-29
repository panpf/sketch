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

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.transform.TransformResult
import com.github.panpf.sketch.transform.Transformation
import kotlinx.coroutines.delay

class DelayTransformation(
    val delayTime: Long = 1000,
    val onTransform: (() -> Unit)? = null
) : Transformation {

    override val key: String
        get() = "DelayTransformation"

    override suspend fun transform(
        sketch: Sketch,
        requestContext: RequestContext,
        input: Image
    ): TransformResult? {
        onTransform?.invoke()
        delay(delayTime)
        return null
    }
}