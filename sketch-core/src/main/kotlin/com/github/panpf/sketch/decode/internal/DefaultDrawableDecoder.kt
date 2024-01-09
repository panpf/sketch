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
package com.github.panpf.sketch.decode.internal

import android.graphics.drawable.BitmapDrawable
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext

class DefaultDrawableDecoder(
    private val sketch: Sketch,
    private val requestContext: RequestContext,
    private val fetchResult: FetchResult
) : DrawableDecoder {

    @WorkerThread
    override suspend fun decode(): Result<DrawableDecodeResult> {
        val bitmapDecodeResult = BitmapDecodeInterceptorChain(
            sketch = sketch,
            request = requestContext.request,
            requestContext = requestContext,
            fetchResult = fetchResult,
            interceptors = sketch.components.getBitmapDecodeInterceptorList(requestContext.request),
            index = 0,
        ).proceed()
            .let { it.getOrNull() ?: return Result.failure(it.exceptionOrNull()!!) }
        val sketchBitmapDrawable = BitmapDrawable(
            /* res = */ requestContext.request.context.resources,
            /* bitmap = */ bitmapDecodeResult.bitmap,
        )
        val drawableDecodeResult = DrawableDecodeResult(
            drawable = sketchBitmapDrawable,
            imageInfo = bitmapDecodeResult.imageInfo,
            dataFrom = bitmapDecodeResult.dataFrom,
            transformedList = bitmapDecodeResult.transformedList,
            extras = bitmapDecodeResult.extras,
        )
        return Result.success(drawableDecodeResult)
    }

    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): DrawableDecoder = DefaultDrawableDecoder(sketch, requestContext, fetchResult)

        override fun toString(): String = "DefaultDrawableDecoder"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}