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
package com.github.panpf.sketch.decode

import android.graphics.drawable.Drawable
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext

/**
 * Decode [Drawable] from [DataSource].
 */
fun interface DrawableDecoder {

    /**
     * Decode [Drawable] from [DataSource] and wrap it as a [DrawableDecodeResult] return.
     */
    @WorkerThread
    suspend fun decode(): Result<DrawableDecodeResult>

    /**
     * [Factory] will be registered in [ComponentRegistry], and will traverse [Factory]
     * to create [DrawableDecoder] when it needs decode [Drawable]
     */
    fun interface Factory {

        /**
         * If the current [Factory]'s [DrawableDecoder] can decode [Drawable] from the current [fetchResult],
         * create a [DrawableDecoder] and return it, otherwise return null
         */
        fun create(
            sketch: Sketch,
            requestContext: RequestContext,
            fetchResult: FetchResult,
        ): DrawableDecoder?
    }
}