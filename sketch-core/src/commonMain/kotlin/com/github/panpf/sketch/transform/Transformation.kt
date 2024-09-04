/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
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

package com.github.panpf.sketch.transform

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.util.Key

/**
 * An interface for making transformations to an image's pixel data.
 *
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 *
 * @see com.github.panpf.sketch.core.common.test.transform.TransformationTest
 */
interface Transformation : Key {

    /**
     * The unique cache key for this transformation.
     *
     * The key is added to the image request's memory cache key and should contain any params that
     * are part of this transformation (e.g. size, scale, color, radius, etc.).
     */
    override val key: String

    /**
     * Apply the transformation to [input] and return the transformed [Image].
     *
     * @param requestContext [RequestContext].
     * @param input The input [Image] to transform. Don't recycle it, it will cause unpredictable errors
     * @return The transformed [Image].
     */
    @WorkerThread
    suspend fun transform(
        sketch: Sketch, // TODO remove
        requestContext: RequestContext,
        input: Image
    ): TransformResult?
}

/**
 * Merge two transformation lists, the transformation of the same key only retains the one in the left list
 *
 * @see com.github.panpf.sketch.core.common.test.transform.TransformationTest.testMerge
 */
fun List<Transformation>?.merge(other: List<Transformation>?): List<Transformation>? =
    if (this != null) {
        if (other != null) {
            this.plus(other).distinctBy {
                it.key
            }
        } else {
            this
        }
    } else {
        other
    }