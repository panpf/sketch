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

package com.github.panpf.sketch.cache

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataSource
import okio.BufferedSink

/**
 * Create an ImageSerializer for a specific platform
 *
 * @see com.github.panpf.sketch.core.android.test.cache.ImageSerializerAndroidTest.testCreateImageSerializer
 * @see com.github.panpf.sketch.core.nonandroid.test.cache.ImageSerializerNonAndroidTest.testCreateImageSerializer
 */
expect fun createImageSerializer(): ImageSerializer?

/**
 * Image serialization interface, used to serialize image objects into disk cache
 *
 * @see com.github.panpf.sketch.core.android.test.cache.ImageSerializerAndroidTest
 * @see com.github.panpf.sketch.core.nonandroid.test.cache.ImageSerializerNonAndroidTest
 */
interface ImageSerializer {

    fun supportImage(image: Image): Boolean

    @WorkerThread
    fun compress(image: Image, sink: BufferedSink)

    @WorkerThread
    fun decode(requestContext: RequestContext, imageInfo: ImageInfo, dataSource: DataSource): Image
}