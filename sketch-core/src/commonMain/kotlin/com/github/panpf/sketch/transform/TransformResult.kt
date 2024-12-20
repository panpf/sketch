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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.transform

import com.github.panpf.sketch.Image

/**
 * Bitmap transformation result
 *
 * @see com.github.panpf.sketch.core.common.test.transform.TransformResultTest
 */
data class TransformResult constructor(
    /**
     * Transformed [Image]
     */
    val image: Image,

    /**
     * Store the information of this transformation to record the transformation history of Image
     */
    val transformed: String
)