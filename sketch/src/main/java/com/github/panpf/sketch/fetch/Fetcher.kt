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
package com.github.panpf.sketch.fetch

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapDecoder
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.request.ImageRequest

/**
 * [Fetcher] get the data stream from the uri of [ImageRequest] and wrap it as a [FetchResult] return
 * for use by [BitmapDecoder] or [DrawableDecoder]
 */
fun interface Fetcher {

    /**
     * Get the data stream from the uri of [ImageRequest] and wrap it as a [FetchResult] return
     */
    @WorkerThread
    suspend fun fetch(): FetchResult

    /**
     * [Factory] will be registered in [ComponentRegistry], and will traverse [Factory]
     * to create [Fetcher] when it needs to extract [ImageRequest] data
     */
    fun interface Factory {

        /**
         * If the current [Factory]'s [Fetcher] can extract data from the current [request],
         * create a [Fetcher] and return it, otherwise return null
         */
        fun create(sketch: Sketch, request: ImageRequest): Fetcher?
    }
}
