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

package com.github.panpf.sketch.request

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.source.DataFrom

/**
 * Data of [RequestInterceptor]
 *
 * @see com.github.panpf.sketch.core.common.test.request.ImageDataTest
 */
data class ImageData(
    /**
     * The decoded image
     */
    val image: Image,

    /**
     * The decoded image info
     */
    val imageInfo: ImageInfo,

    /**
     * The data source of the image
     */
    val dataFrom: DataFrom,

    /**
     * The resize
     */
    val resize: Resize,

    /**
     * Store the transformation history of the Bitmap
     */
    val transformeds: List<String>?,

    /**
     * Store some additional information for consumer use
     */
    val extras: Map<String, String>?,
)