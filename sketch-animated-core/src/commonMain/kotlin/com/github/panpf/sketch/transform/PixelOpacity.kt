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

/**
 * Represents the opacity of an image's pixels after applying an [AnimatedTransformation].
 *
 * @see com.github.panpf.sketch.animated.core.common.test.transform.PixelOpacityTest.testPixelOpacity
 */
enum class PixelOpacity {

    /**
     * Indicates that the [AnimatedTransformation] did not change the image's opacity.
     *
     * Return this unless you add transparent pixels to the image or remove all transparent
     * pixels in the image.
     */
    UNCHANGED,

    /**
     * Indicates that the [AnimatedTransformation] added transparent pixels to the image.
     */
    TRANSLUCENT,

    /**
     * Indicates that the [AnimatedTransformation] removed all transparent pixels in the image.
     */
    OPAQUE
}