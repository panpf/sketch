/*
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.transform

import android.graphics.Canvas
import android.graphics.PostProcessor
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.util.Key

/**
 * An interface for making transformations to an animated image's pixel data.
 */
interface AnimatedTransformation : Key {

    /**
     * Apply the transformation to the [canvas].
     *
     * @param canvas The [Canvas] to draw on.
     * @return The opacity of the image after drawing.
     */
    fun transform(canvas: Canvas): PixelOpacity
}

/**
 * Convert this [AnimatedTransformation] to a [PostProcessor].
 *
 * @see com.github.panpf.sketch.animated.android.test.transform.AnimatedTransformationTest.testAsPostProcessor
 */
@RequiresApi(28)
internal fun AnimatedTransformation.asPostProcessor() =
    PostProcessor { canvas -> transform(canvas).flag }
